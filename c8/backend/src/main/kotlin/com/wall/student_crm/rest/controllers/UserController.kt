package com.wall.student_crm.rest.controllers

import com.wall.student_crm.rest.dto.user.ChangePasswordRequest
import com.wall.student_crm.rest.dto.user.CreateUserRequest
import com.wall.student_crm.rest.dto.user.UpdateUserRequest
import com.wall.student_crm.rest.dto.user.UserDto
import com.wall.student_crm.rest.dto.toDto
import com.wall.student_crm.service.business.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('USER')")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/create")
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserDto> {
        return try {
            val user = userService.createUser(
                username = request.username,
                email = request.email,
                password = request.password,
                firstName = request.firstName,
                lastName = request.lastName,
                groupIds = request.groupIds
            )
            ResponseEntity.status(HttpStatus.CREATED).body(user.toDto())
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.user.id")
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UserDto> {
        return try {
            val user = userService.updateUser(
                userId = id,
                username = request.username,
                email = request.email,
                firstName = request.firstName,
                lastName = request.lastName,
                groupIds = request.groupIds
            )
            ResponseEntity.ok(user.toDto())
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }


    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.user.id")
    fun changePassword(
        @PathVariable id: Long,
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<Any> {
        return try {
            userService.changePassword(id, request.oldPassword, request.newPassword)
            ResponseEntity.ok(mapOf("message" to "Password changed successfully"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            userService.deleteUser(id)
            ResponseEntity.ok(mapOf("message" to "User deleted successfully"))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

}