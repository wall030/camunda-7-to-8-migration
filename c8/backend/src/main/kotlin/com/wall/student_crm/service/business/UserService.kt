package com.wall.student_crm.service.business

import com.wall.student_crm.persistence.group.GroupRepository
import com.wall.student_crm.persistence.user.UserEntity
import com.wall.student_crm.persistence.user.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * Erstellt einen neuen Benutzer
     */
    fun createUser(
        username: String,
        email: String,
        password: String,
        firstName: String? = null,
        lastName: String? = null,
        groupIds: Set<Long> = emptySet()
    ): UserEntity {
        // Prüfe ob Username bereits existiert
        if (userRepository.findByUsername(username).isPresent) {
            throw IllegalArgumentException("Username '$username' already exists")
        }

        // Prüfe ob Email bereits existiert
        if (userRepository.findByEmail(email).isPresent) {
            throw IllegalArgumentException("Email '$email' already exists")
        }

        // Lade Gruppen falls vorhanden
        val groups = if (groupIds.isNotEmpty()) {
            groupRepository.findAllById(groupIds).toSet()
        } else {
            emptySet()
        }

        val user = UserEntity(
            username = username,
            email = email,
            password = passwordEncoder.encode(password),
            firstName = firstName,
            lastName = lastName,
            groups = groups
        )

        return userRepository.save(user)
    }

    /**
     * Aktualisiert einen bestehenden Benutzer
     */
    fun updateUser(
        userId: Long,
        username: String? = null,
        email: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        groupIds: Set<Long>? = null
    ): UserEntity {
        val existingUser = findById(userId)

        // Prüfe Username-Eindeutigkeit falls geändert
        if (username != null && username != existingUser.username) {
            if (userRepository.findByUsername(username).isPresent) {
                throw IllegalArgumentException("Username '$username' already exists")
            }
        }

        // Prüfe Email-Eindeutigkeit falls geändert
        if (email != null && email != existingUser.email) {
            if (userRepository.findByEmail(email).isPresent) {
                throw IllegalArgumentException("Email '$email' already exists")
            }
        }

        // Lade neue Gruppen falls angegeben
        val groups = if (groupIds != null) {
            groupRepository.findAllById(groupIds).toSet()
        } else {
            existingUser.groups
        }

        val updatedUser = existingUser.copy(
            username = username ?: existingUser.username,
            email = email ?: existingUser.email,
            firstName = firstName ?: existingUser.firstName,
            lastName = lastName ?: existingUser.lastName,
            groups = groups
        )

        return userRepository.save(updatedUser)
    }

    /**
     * Ändert das Passwort eines Benutzers
     */
    fun changePassword(userId: Long, newPassword: String): UserEntity {
        val user = findById(userId)
        val updatedUser = user.copy(password = passwordEncoder.encode(newPassword))
        return userRepository.save(updatedUser)
    }

    /**
     * Ändert das Passwort mit Validierung des alten Passworts
     */
    fun changePassword(userId: Long, oldPassword: String, newPassword: String): UserEntity {
        val user = findById(userId)

        if (!passwordEncoder.matches(oldPassword, user.password)) {
            throw IllegalArgumentException("Current password is incorrect")
        }

        return changePassword(userId, newPassword)
    }

    /**
     * Fügt einen Benutzer zu Gruppen hinzu
     */
    fun addUserToGroups(userId: Long, groupIds: Set<Long>): UserEntity {
        val user = findById(userId)
        val newGroups = groupRepository.findAllById(groupIds).toSet()
        val allGroups = user.groups + newGroups

        val updatedUser = user.copy(groups = allGroups)
        return userRepository.save(updatedUser)
    }

    /**
     * Entfernt einen Benutzer aus Gruppen
     */
    fun removeUserFromGroups(userId: Long, groupIds: Set<Long>): UserEntity {
        val user = findById(userId)
        val groupsToRemove = groupRepository.findAllById(groupIds).toSet()
        val remainingGroups = user.groups - groupsToRemove

        val updatedUser = user.copy(groups = remainingGroups)
        return userRepository.save(updatedUser)
    }

    /**
     * Setzt die Gruppen eines Benutzers (ersetzt alle bestehenden)
     */
    fun setUserGroups(userId: Long, groupIds: Set<Long>): UserEntity {
        val user = findById(userId)
        val groups = if (groupIds.isNotEmpty()) {
            groupRepository.findAllById(groupIds).toSet()
        } else {
            emptySet()
        }

        val updatedUser = user.copy(groups = groups)
        return userRepository.save(updatedUser)
    }

    /**
     * Findet einen Benutzer anhand der ID
     */
    @Transactional(readOnly = true)
    fun findById(id: Long): UserEntity {
        return userRepository.findById(id)
            .orElseThrow { NoSuchElementException("User with id $id not found") }
    }

    /**
     * Findet einen Benutzer anhand des Benutzernamens
     */
    @Transactional(readOnly = true)
    fun findByUsername(username: String): UserEntity? {
        return userRepository.findByUsername(username).orElse(null)
    }

    /**
     * Findet einen Benutzer anhand der E-Mail
     */
    @Transactional(readOnly = true)
    fun findByEmail(email: String): UserEntity? {
        return userRepository.findByEmail(email).orElse(null)
    }

    /**
     * Gibt alle Benutzer zurück
     */
    @Transactional(readOnly = true)
    fun findAll(): List<UserEntity> {
        return userRepository.findAll()
    }

    /**
     * Gibt alle Benutzer mit Paginierung zurück
     */
    @Transactional(readOnly = true)
    fun findAll(page: Int, size: Int, sortBy: String = "username", sortDirection: String = "ASC"): Page<UserEntity> {
        val sort = if (sortDirection.uppercase() == "DESC") {
            Sort.by(sortBy).descending()
        } else {
            Sort.by(sortBy).ascending()
        }

        val pageable: Pageable = PageRequest.of(page, size, sort)
        return userRepository.findAll(pageable)
    }

    /**
     * Sucht Benutzer anhand verschiedener Kriterien
     */
    @Transactional(readOnly = true)
    fun searchUsers(searchTerm: String): List<UserEntity> {
        return userRepository.findAll().filter { user ->
            user.username.contains(searchTerm, ignoreCase = true) ||
                    user.email.contains(searchTerm, ignoreCase = true) ||
                    user.firstName?.contains(searchTerm, ignoreCase = true) == true ||
                    user.lastName?.contains(searchTerm, ignoreCase = true) == true
        }
    }

    /**
     * Prüft ob ein Benutzer existiert
     */
    @Transactional(readOnly = true)
    fun existsById(id: Long): Boolean {
        return userRepository.existsById(id)
    }

    /**
     * Prüft ob ein Benutzername existiert
     */
    @Transactional(readOnly = true)
    fun existsByUsername(username: String): Boolean {
        return userRepository.findByUsername(username).isPresent
    }

    /**
     * Prüft ob eine E-Mail existiert
     */
    @Transactional(readOnly = true)
    fun existsByEmail(email: String): Boolean {
        return userRepository.findByEmail(email).isPresent
    }

    /**
     * Löscht einen Benutzer
     */
    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw NoSuchElementException("User with id $id not found")
        }
        userRepository.deleteById(id)
    }

    /**
     * Löscht einen Benutzer anhand des Benutzernamens
     */
    fun deleteByUsername(username: String) {
        val user = userRepository.findByUsername(username)
            .orElseThrow { NoSuchElementException("User with username $username not found") }
        userRepository.delete(user)
    }

    /**
     * Zählt alle Benutzer
     */
    @Transactional(readOnly = true)
    fun count(): Long {
        return userRepository.count()
    }

    /**
     * Prüft ob das Passwort korrekt ist
     */
    @Transactional(readOnly = true)
    fun isPasswordCorrect(userId: Long, password: String): Boolean {
        val user = findById(userId)
        return passwordEncoder.matches(password, user.password)
    }

    /**
     * Prüft ob das Passwort für einen Benutzernamen korrekt ist
     */
    @Transactional(readOnly = true)
    fun isPasswordCorrect(username: String, password: String): Boolean {
        val user = findByUsername(username) ?: return false
        return passwordEncoder.matches(password, user.password)
    }

    /**
     * Gibt alle Benutzer einer bestimmten Gruppe zurück
     */
    @Transactional(readOnly = true)
    fun findUsersByGroupId(groupId: Long): List<UserEntity> {
        return userRepository.findAll().filter { user ->
            user.groups.any { it.id == groupId }
        }
    }

    /**
     * Gibt alle Benutzer mehrerer Gruppen zurück
     */
    @Transactional(readOnly = true)
    fun findUsersByGroupIds(groupIds: Set<Long>): List<UserEntity> {
        return userRepository.findAll().filter { user ->
            user.groups.any { group -> group.id in groupIds }
        }
    }

    /**
     * Prüft ob ein Benutzer zu einer bestimmten Gruppe gehört
     */
    @Transactional(readOnly = true)
    fun isUserInGroup(userId: Long, groupId: Long): Boolean {
        val user = findById(userId)
        return user.groups.any { it.id == groupId }
    }

    /**
     * Prüft ob ein Benutzer zu einer der angegebenen Gruppen gehört
     */
    @Transactional(readOnly = true)
    fun isUserInAnyGroup(userId: Long, groupIds: Set<Long>): Boolean {
        val user = findById(userId)
        return user.groups.any { group -> group.id in groupIds }
    }
}