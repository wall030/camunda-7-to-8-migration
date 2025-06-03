package com.wall.student_crm.persistence.group

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface GroupRepository : JpaRepository<GroupEntity, Long> {
    fun findByName(name: String): Optional<GroupEntity>
}