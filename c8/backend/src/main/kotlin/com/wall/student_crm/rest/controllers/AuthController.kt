package com.wall.student_crm.rest.controllers

import com.wall.student_crm.persistence.user.UserEntity
import com.wall.student_crm.rest.dto.group.GroupDto
import com.wall.student_crm.rest.dto.auth.LoginRequest
import com.wall.student_crm.rest.dto.auth.LoginResponse
import com.wall.student_crm.rest.dto.auth.RefreshTokenRequest
import com.wall.student_crm.rest.dto.user.UserDto
import com.wall.student_crm.service.auth.CustomUserDetailsService
import com.wall.student_crm.service.auth.CustomUserPrincipal
import com.wall.student_crm.service.auth.JwtTokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenService: JwtTokenService,
    private val userDetailsService: CustomUserDetailsService,
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
        return try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.username,
                    loginRequest.password
                )
            )

            val userDetails = authentication.principal as CustomUserPrincipal
            val accessToken = jwtTokenService.generateToken(userDetails)
            val refreshToken = jwtTokenService.generateRefreshToken(userDetails)

            val userDto = mapUserToDto(userDetails.getUser())

            val response = LoginResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = 86400,
                user = userDto
            )

            ResponseEntity.ok(response)
        } catch (e: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Invalid username or password"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Login failed: ${e.message}"))
        }
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody refreshRequest: RefreshTokenRequest): ResponseEntity<Any> {
        return try {
            val token = refreshRequest.refreshToken

            if (jwtTokenService.validateToken(token)) {
                val username = jwtTokenService.extractUsername(token)
                val userDetails = userDetailsService.loadUserByUsername(username) as CustomUserPrincipal

                val newAccessToken = jwtTokenService.generateToken(userDetails)
                val newRefreshToken = jwtTokenService.generateRefreshToken(userDetails)

                val userDto = mapUserToDto(userDetails.getUser())

                val response = LoginResponse(
                    accessToken = newAccessToken,
                    refreshToken = newRefreshToken,
                    expiresIn = 86400,
                    user = userDto
                )

                ResponseEntity.ok(response)
            } else {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(mapOf("error" to "Invalid refresh token"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Token refresh failed: ${e.message}"))
        }
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Any> {
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }

    private fun mapUserToDto(user: UserEntity): UserDto {
        return UserDto(
            id = user.id,
            username = user.username,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            groups = user.groups.map { group ->
                GroupDto(
                    id = group.id,
                    name = group.name,
                    description = group.description
                )
            }
        )
    }
}