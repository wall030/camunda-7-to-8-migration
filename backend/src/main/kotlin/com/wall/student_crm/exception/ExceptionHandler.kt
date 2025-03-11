package com.wall.student_crm.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException::class)
    fun handleServiceExceptions(ex: ServiceException): ResponseEntity<Map<String, Int>> {
        val errorCode = ErrorCode.fromException(ex)
        val status = getHttpStatus(errorCode)
        return createErrorResponse(status, errorCode.code)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralExceptions(ex: Exception): ResponseEntity<Map<String, Int>> {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR.code)
    }

    private fun createErrorResponse(
        status: HttpStatus,
        errorCode: Int
    ): ResponseEntity<Map<String, Int>> {
        return ResponseEntity.status(status)
            .body(mapOf("errorCode" to errorCode))
    }

    private fun getHttpStatus(errorCode: ErrorCode): HttpStatus =
        when (errorCode) {
            ErrorCode.STUDENT_NOT_FOUND, ErrorCode.COURSE_NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorCode.DUPLICATE_STUDENT, ErrorCode.DUPLICATE_COURSE -> HttpStatus.CONFLICT
            ErrorCode.INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
        }
}
