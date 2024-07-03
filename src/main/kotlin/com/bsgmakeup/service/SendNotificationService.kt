package com.bsgmakeup.service

import com.bsgmakeup.dto.NotificationDTO
import com.bsgmakeup.entity.NotificationsEntity
import com.bsgmakeup.entity.UserEntity
import com.bsgmakeup.entity.UserTokenEntity
import com.bsgmakeup.repository.NotificationsRepository
import com.bsgmakeup.repository.UserRepository
import com.bsgmakeup.repository.UserTokenRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ExecutionException

@Service
class SendNotificationService(
    private val notificationsRepository: NotificationsRepository,
    private val userTokenRepository: UserTokenRepository,
    private val userRepository: UserRepository
) {

    fun getNotificationsByUserId(userId: String): List<NotificationsEntity> {
        return notificationsRepository.findByUserIdToAndActiveOrderByCreatedAtDesc(userId, true)
    }

    fun markAsRead(notificationId: String): NotificationsEntity? {
        val notificationsEntity = notificationsRepository.findById(notificationId)

        if (notificationsEntity.isPresent) {
            val notificationUpdated = notificationsEntity.get()
            notificationUpdated.hasBeenRead = true
            notificationUpdated.updatedAt = Date()

            return notificationsRepository.save(notificationUpdated)
        }

        return null
    }

    fun markAllAsRead(userId: String): List<NotificationsEntity> {
        val notifications: List<NotificationsEntity> = notificationsRepository.findByUserIdToAndActiveOrderByCreatedAtDesc(userId, true)

        notifications.forEach { notification ->
            notification.hasBeenRead = true
        }

        return notificationsRepository.saveAll(notifications)
    }

    fun saveUserToken(token: String, userId: String, userRole: String): UserEntity {
        val userTokenEntity = userTokenRepository.findByUserId(userId)

        if (userTokenEntity != null) {
            userTokenEntity.token = token
            userTokenEntity.updatedAt = Date()

            userTokenRepository.save(userTokenEntity)

        } else {
            val newUserTokenEntity = UserTokenEntity(
                token = token,
                userId = userId,
                role = userRole
            )
            userTokenRepository.save(newUserTokenEntity)
        }

        return userRepository.findById(userId).get()
    }

    fun notifyAdmins(notificationDTO: NotificationDTO) {
        val adminTokens = userTokenRepository.findAllByRole("admin")

        adminTokens.forEach { adminToken ->
            createNotification(NotificationDTO(
                userIdFrom = notificationDTO.userIdFrom,
                userIdTo = adminToken.userId,
                title = notificationDTO.title,
                content = notificationDTO.content,
                icon = notificationDTO.icon,
                ))

            sendNotification(adminToken.token, notificationDTO.title, notificationDTO.content)
        }
    }

    fun notifyUser(notification: NotificationDTO) {
        val userTokenEntity = userTokenRepository.findByUserId(notification.userIdTo)

        if (userTokenEntity != null) {
            createNotification(notification)

            sendNotification(userTokenEntity.token, notification.title, notification.content)
        }
    }

    fun removeToken(userId: String) {
        userTokenRepository.deleteByUserId(userId)
    }

    fun deleteNotification(notificationId: String) {
        val notificationEntity = notificationsRepository.findById(notificationId)

        if (notificationEntity.isPresent) {
            val notificationUpdated = notificationEntity.get()

            notificationUpdated.active = false
            notificationUpdated.deletedAt = Date()
            notificationUpdated.updatedAt = Date()

            notificationsRepository.save(notificationUpdated)
        }
    }

    fun deleteAllNotifications(userId: String) {
        val notificationsList = notificationsRepository.findByUserIdToAndActiveOrderByCreatedAtDesc(userId, true)

        notificationsList.forEach { notification: NotificationsEntity ->
            notification.active = false
            notification.deletedAt = Date()
            notification.updatedAt = Date()
        }

        notificationsRepository.saveAll(notificationsList)
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    private fun sendNotification(userTargetToken: String, title: String, body: String) {
        val message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .setToken(userTargetToken)
            .build()

        FirebaseMessaging.getInstance().sendAsync(message).get()
    }

    private fun createNotification(notification: NotificationDTO): NotificationsEntity {
        val notificationsEntity = NotificationsEntity(
            userIdFrom = notification.userIdFrom,
            userIdTo = notification.userIdTo,
            title = notification.title,
            content = notification.content,
            icon = notification.icon,
        )

        return notificationsRepository.save(notificationsEntity)
    }
}
