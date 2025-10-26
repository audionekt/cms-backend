package com.cms.backend.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.cms.backend.exception.FileDeleteException
import com.cms.backend.exception.FileUploadException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*

@Service
class S3Service(
    private val amazonS3: AmazonS3,
    @Value("\${aws.s3.bucket}") private val bucketName: String,
    @Value("\${aws.region}") private val region: String
) {
    private val logger = LoggerFactory.getLogger(S3Service::class.java)

    fun uploadFile(file: MultipartFile): Pair<String, String> {
        if (file.isEmpty) {
            throw FileUploadException("File is empty")
        }

        val fileName = generateFileName(file.originalFilename ?: "file")
        val s3Key = fileName

        try {
            val metadata = ObjectMetadata()
            metadata.contentType = file.contentType
            metadata.contentLength = file.size

            val putObjectRequest = PutObjectRequest(bucketName, s3Key, file.inputStream, metadata)
            // Note: No ACL set - bucket uses bucket policy or Block Public Access settings

            amazonS3.putObject(putObjectRequest)
            
            val fileUrl = getFileUrl(s3Key)
            logger.info("File uploaded successfully: $s3Key")
            
            return Pair(s3Key, fileUrl)
        } catch (e: IOException) {
            logger.error("Error uploading file to S3: ${e.message}", e)
            throw FileUploadException("Failed to upload file to S3: ${e.message}")
        }
    }

    fun deleteFile(s3Key: String) {
        try {
            amazonS3.deleteObject(DeleteObjectRequest(bucketName, s3Key))
            logger.info("File deleted successfully: $s3Key")
        } catch (e: Exception) {
            logger.error("Error deleting file from S3: ${e.message}", e)
            throw FileDeleteException("Failed to delete file from S3: ${e.message}")
        }
    }

    fun getFileUrl(s3Key: String): String {
        return "https://$bucketName.s3.$region.amazonaws.com/$s3Key"
    }

    private fun generateFileName(originalFilename: String): String {
        val timestamp = System.currentTimeMillis()
        val uuid = UUID.randomUUID().toString().substring(0, 8)
        val extension = originalFilename.substringAfterLast(".", "")
        val baseName = originalFilename.substringBeforeLast(".")
            .replace(Regex("[^a-zA-Z0-9-_]"), "-")
            .take(50)
        
        return if (extension.isNotEmpty()) {
            "$baseName-$timestamp-$uuid.$extension"
        } else {
            "$baseName-$timestamp-$uuid"
        }
    }
}
