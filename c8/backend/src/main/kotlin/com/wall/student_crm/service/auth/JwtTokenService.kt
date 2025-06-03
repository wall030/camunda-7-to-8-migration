package com.wall.student_crm.service.auth

import com.wall.student_crm.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.Date

@Service
class JwtTokenService(
    private val jwtProperties: JwtProperties
) {

    private val key: Key = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun generateToken(userDetails: UserDetails): String {
        val claims = mapOf<String, Any>(
            "username" to userDetails.username,
            "authorities" to userDetails.authorities.map { it.authority }
        )

        return createToken(claims, userDetails.username, jwtProperties.expirationTime)
    }

    fun generateRefreshToken(userDetails: UserDetails): String {
        val claims = mapOf<String, Any>(
            "username" to userDetails.username,
            "type" to "refresh"
        )

        return createToken(claims, userDetails.username, jwtProperties.refreshExpirationTime)
    }

    private fun createToken(claims: Map<String, Any>, subject: String, expirationTime: Long): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    fun extractAuthorities(token: String): List<String> {
        val claims = extractAllClaims(token)
        return claims["authorities"] as? List<String> ?: emptyList()
    }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    fun validateToken(token: String): Boolean {
        return try {
            !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }
}