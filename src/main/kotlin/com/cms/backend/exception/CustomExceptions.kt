package com.cms.backend.exception

class ResourceNotFoundException(message: String) : RuntimeException(message)

class DuplicateResourceException(message: String) : RuntimeException(message)

class InvalidRequestException(message: String) : RuntimeException(message)

class UnauthorizedException(message: String) : RuntimeException(message)

class FileUploadException(message: String) : RuntimeException(message)

class FileDeleteException(message: String) : RuntimeException(message)





