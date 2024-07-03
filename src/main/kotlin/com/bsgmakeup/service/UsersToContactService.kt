package com.bsgmakeup.service

import com.bsgmakeup.dto.NotificationDTO
import com.bsgmakeup.dto.UserToContactDTO
import com.bsgmakeup.entity.UsersToContactEntity
import com.bsgmakeup.repository.UsersToContactRepository
import org.springframework.stereotype.Service

@Service
class UsersToContactService(
    private val usersToContactRepository: UsersToContactRepository,
    private val sendNotificationService: SendNotificationService
) {

    fun createUserToContact(userToContactDTO: UserToContactDTO): String {
        val userExists: UsersToContactEntity? = usersToContactRepository.findByPhone(userToContactDTO.phone)
        if (userExists != null) {
            return "DUPLICATED"
        }

        val userToContactEntity = UsersToContactEntity(
            name = userToContactDTO.name,
            phone = userToContactDTO.phone,
            message = userToContactDTO.message
        )

        val createEntity: UsersToContactEntity = usersToContactRepository.save(userToContactEntity)

        if (createEntity.id.isEmpty()) {
            return "ERROR"
        }

        val notificationContent = (createEntity.name + " ha solicitado ser contactado, su telefono es"
                + createEntity.phone)

        sendNotificationService.notifyAdmins(NotificationDTO(
            userIdFrom = createEntity.id,
            title = "Usuario a contactar creado",
            content = notificationContent,
            icon = "contact_phone"
        ))

        return "SUCCESS"
    }
}
