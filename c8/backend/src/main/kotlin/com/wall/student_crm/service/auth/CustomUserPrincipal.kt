package com.wall.student_crm.service.auth

import com.wall.student_crm.persistence.user.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class CustomUserPrincipal(
    private val user: UserEntity
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()

        authorities.add(SimpleGrantedAuthority("ROLE_USER"))

        user.groups.forEach { group ->
            authorities.add(SimpleGrantedAuthority("ROLE_${group.name.uppercase()}"))
        }

        return authorities
    }

    override fun getPassword(): String = user.password
    override fun getUsername(): String = user.username
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    fun getUser(): UserEntity = user
}