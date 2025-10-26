package com.cms.backend.service

import com.cms.backend.dto.CreateUserRequest
import com.cms.backend.dto.UpdateUserRequest
import com.cms.backend.dto.UserResponse
import com.cms.backend.entity.User
import com.cms.backend.exception.DuplicateResourceException
import com.cms.backend.exception.ResourceNotFoundException
import com.cms.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun createUser(request: CreateUserRequest): UserResponse {
        logger.info("Creating user with email: ${request.email}")

        if (userRepository.existsByEmail(request.email)) {
            throw DuplicateResourceException("User with email ${request.email} already exists")
        }

        if (userRepository.existsByUsername(request.username)) {
            throw DuplicateResourceException("User with username ${request.username} already exists")
        }

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            firstName = request.firstName,
            lastName = request.lastName,
            username = request.username,
            bio = request.bio,
            avatarUrl = request.avatarUrl,
            role = request.role
        )

        val savedUser = userRepository.save(user)
        logger.info("User created successfully with id: ${savedUser.id}")
        return UserResponse.fromEntity(savedUser)
    }

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserResponse {
        val user = findUserById(id)
        return UserResponse.fromEntity(user)
    }

    @Transactional(readOnly = true)
    fun getUserByUsername(username: String): UserResponse {
        val user = userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User with username $username not found") }
        return UserResponse.fromEntity(user)
    }

    @Transactional(readOnly = true)
    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { UserResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getActiveUsers(): List<UserResponse> {
        return userRepository.findAllByActiveTrue().map { UserResponse.fromEntity(it) }
    }

    fun updateUser(id: Long, request: UpdateUserRequest): UserResponse {
        logger.info("Updating user with id: $id")
        val user = findUserById(id)

        val updatedUser = user.copy(
            firstName = request.firstName ?: user.firstName,
            lastName = request.lastName ?: user.lastName,
            bio = request.bio ?: user.bio,
            avatarUrl = request.avatarUrl ?: user.avatarUrl,
            role = request.role ?: user.role,
            active = request.active ?: user.active
        )

        val savedUser = userRepository.save(updatedUser)
        logger.info("User updated successfully with id: ${savedUser.id}")
        return UserResponse.fromEntity(savedUser)
    }

    fun deleteUser(id: Long) {
        logger.info("Deleting user with id: $id")
        val user = findUserById(id)
        userRepository.delete(user)
        logger.info("User deleted successfully with id: $id")
    }

    private fun findUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }
    }
}





