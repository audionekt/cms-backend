package com.cms.backend.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.PutObjectRequest
import com.cms.backend.exception.FileDeleteException
import com.cms.backend.exception.FileUploadException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.util.ReflectionTestUtils

class S3ServiceTest {

    private val amazonS3: AmazonS3 = mockk()
    private lateinit var s3Service: S3Service

    @BeforeEach
    fun setup() {
        s3Service = S3Service(amazonS3, "test-bucket", "us-east-1")
    }

    @Test
    fun `should upload file successfully`() {
        // Given
        val file = MockMultipartFile(
            "file",
            "test-image.jpg",
            "image/jpeg",
            "test content".toByteArray()
        )

        val putRequestSlot = slot<PutObjectRequest>()
        every { amazonS3.putObject(capture(putRequestSlot)) } returns mockk()

        // When
        val (s3Key, fileUrl) = s3Service.uploadFile(file)

        // Then
        assertThat(s3Key).isNotEmpty
        assertThat(s3Key).contains("test-image")
        assertThat(s3Key).endsWith(".jpg")
        assertThat(fileUrl).startsWith("https://test-bucket.s3.us-east-1.amazonaws.com/")
        assertThat(fileUrl).contains(s3Key)

        verify(exactly = 1) { amazonS3.putObject(any<PutObjectRequest>()) }

        // Verify request details
        assertThat(putRequestSlot.captured.bucketName).isEqualTo("test-bucket")
        assertThat(putRequestSlot.captured.key).isEqualTo(s3Key)
        assertThat(putRequestSlot.captured.metadata.contentType).isEqualTo("image/jpeg")
        assertThat(putRequestSlot.captured.metadata.contentLength).isEqualTo(12L)
    }

    @Test
    fun `should throw exception when uploading empty file`() {
        // Given
        val emptyFile = MockMultipartFile(
            "file",
            "empty.jpg",
            "image/jpeg",
            ByteArray(0)
        )

        // When & Then
        assertThatThrownBy { s3Service.uploadFile(emptyFile) }
            .isInstanceOf(FileUploadException::class.java)
            .hasMessageContaining("File is empty")

        verify(exactly = 0) { amazonS3.putObject(any<PutObjectRequest>()) }
    }

    @Test
    fun `should generate unique filenames`() {
        // Given
        val file1 = MockMultipartFile("file", "test.jpg", "image/jpeg", "content".toByteArray())
        val file2 = MockMultipartFile("file", "test.jpg", "image/jpeg", "content".toByteArray())

        every { amazonS3.putObject(any<PutObjectRequest>()) } returns mockk()

        // When
        val (s3Key1, _) = s3Service.uploadFile(file1)
        val (s3Key2, _) = s3Service.uploadFile(file2)

        // Then
        assertThat(s3Key1).isNotEqualTo(s3Key2)
    }

    @Test
    fun `should handle files without extension`() {
        // Given
        val file = MockMultipartFile("file", "noextension", "text/plain", "content".toByteArray())

        every { amazonS3.putObject(any<PutObjectRequest>()) } returns mockk()

        // When
        val (s3Key, _) = s3Service.uploadFile(file)

        // Then
        assertThat(s3Key).isNotEmpty
        assertThat(s3Key).doesNotContain(".")
    }

    @Test
    fun `should handle files with special characters in name`() {
        // Given
        val file = MockMultipartFile(
            "file",
            "file with spaces & special@chars!.jpg",
            "image/jpeg",
            "content".toByteArray()
        )

        every { amazonS3.putObject(any<PutObjectRequest>()) } returns mockk()

        // When
        val (s3Key, _) = s3Service.uploadFile(file)

        // Then
        // Should replace special chars with hyphens
        assertThat(s3Key).matches("file-with-spaces---special-chars--\\d+-[a-z0-9]+\\.jpg")
    }

    @Test
    fun `should throw FileUploadException when S3 upload fails`() {
        // Given
        val file = MockMultipartFile("file", "test.jpg", "image/jpeg", "content".toByteArray())

        every { amazonS3.putObject(any<PutObjectRequest>()) } throws RuntimeException("S3 error")

        // When & Then
        val exception = try {
            s3Service.uploadFile(file)
            null
        } catch (e: FileUploadException) {
            e
        }
        
        assertThat(exception).isNotNull
        assertThat(exception).isInstanceOf(FileUploadException::class.java)
        assertThat(exception!!.message).contains("Failed to upload file to S3")
        assertThat(exception.cause).isInstanceOf(RuntimeException::class.java)
        assertThat(exception.cause?.message).isEqualTo("S3 error")
    }

    @Test
    fun `should delete file successfully`() {
        // Given
        val s3Key = "test-file.jpg"

        val deleteRequestSlot = slot<DeleteObjectRequest>()
        every { amazonS3.deleteObject(capture(deleteRequestSlot)) } returns Unit

        // When
        s3Service.deleteFile(s3Key)

        // Then
        verify(exactly = 1) { amazonS3.deleteObject(any<DeleteObjectRequest>()) }
        assertThat(deleteRequestSlot.captured.bucketName).isEqualTo("test-bucket")
        assertThat(deleteRequestSlot.captured.key).isEqualTo(s3Key)
    }

    @Test
    fun `should throw FileDeleteException when deletion fails`() {
        // Given
        val s3Key = "test-file.jpg"

        every { amazonS3.deleteObject(any<DeleteObjectRequest>()) } throws RuntimeException("S3 delete error")

        // When & Then
        assertThatThrownBy { s3Service.deleteFile(s3Key) }
            .isInstanceOf(FileDeleteException::class.java)
            .hasMessageContaining("Failed to delete file from S3")
    }

    @Test
    fun `should generate correct file URL`() {
        // When
        val url = s3Service.getFileUrl("path/to/file.jpg")

        // Then
        assertThat(url).isEqualTo("https://test-bucket.s3.us-east-1.amazonaws.com/path/to/file.jpg")
    }

    @Test
    fun `should truncate long filenames`() {
        // Given
        val longFileName = "a".repeat(100) + ".jpg"
        val file = MockMultipartFile("file", longFileName, "image/jpeg", "content".toByteArray())

        every { amazonS3.putObject(any<PutObjectRequest>()) } returns mockk()

        // When
        val (s3Key, _) = s3Service.uploadFile(file)

        // Then
        // The entire filename should be reasonable length (base name + timestamp + uuid + extension)
        // Format is: baseName-timestamp-uuid.extension
        // Base name is truncated to 50 chars max
        assertThat(s3Key.length).isLessThan(100)  // Reasonable total length
        
        // Extract just the base name (everything before first dash that's not part of the repeated 'a's)
        val baseNamePart = s3Key.substringBefore("-")
        assertThat(baseNamePart.length).isLessThanOrEqualTo(50)
    }
}

