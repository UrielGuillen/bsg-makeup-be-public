package com.bsgmakeup.controller

import com.bsgmakeup.dto.ResponseData
import com.bsgmakeup.service.AuthenticationService
import com.bsgmakeup.service.SendNotificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notifications")
class SendNotificationController(
    private val sendNotificationService: SendNotificationService,
    private val authenticationService: AuthenticationService,
) {

    @GetMapping
    fun getNotifications(@RequestHeader("Authorization") token: String?): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val userId: String = authenticationService.getDataFromToken(token, "userId") as String
            val notifications = sendNotificationService.getNotificationsByUserId(userId)

            return ResponseEntity.ok(
                ResponseData(true, "Notifications retrieved successfully", notifications)
            )
        }

        return badRequestResponse()
    }

    @PutMapping("/read")
    fun markNotificationAsRead(@RequestHeader("Authorization") token: String?,
                               @RequestBody notificationId: String): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val updatedNotification = sendNotificationService.markAsRead(notificationId)

            return ResponseEntity.ok(
                ResponseData(updatedNotification != null, "", updatedNotification)
            )
        }

        return badRequestResponse()
    }

    @PutMapping("/read/all")
    fun markAllNotificationAsRead(@RequestHeader("Authorization") token: String?): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val userId: String = authenticationService.getDataFromToken(token, "userId") as String
            val updatedNotifications = sendNotificationService.markAllAsRead(userId)

            return ResponseEntity.ok(
                ResponseData(updatedNotifications.isNotEmpty(), "", updatedNotifications)
            )
        }

        return badRequestResponse()
    }

    @PostMapping("/save-user-token")
    fun createAppointment(@RequestHeader("Authorization") token: String?,
                          @RequestBody notificationToken: String): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {

            val userId = authenticationService.getDataFromToken(token, "userId") as String
            val userRole = authenticationService.getDataFromToken(token, "userRole") as String

             val userEntity = sendNotificationService.saveUserToken(notificationToken, userId, userRole)

            return ResponseEntity.ok(
                ResponseData(true, "", authenticationService.signInUser(userEntity))
            )
        }

        return badRequestResponse()
    }

    @DeleteMapping("/delete-user-token")
    fun deleteUserToken(@RequestHeader("Authorization") token: String?): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val userId: String = authenticationService.getDataFromToken(token, "userId") as String

            sendNotificationService.removeToken(userId)

            return ResponseEntity.ok(
                ResponseData(true, "User token deleted successfully", null)
            )
        }

        return badRequestResponse()
    }

    @DeleteMapping("/delete")
    fun deleteNotification(@RequestHeader("Authorization") token: String?,
                           @RequestParam notificationId: String): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            sendNotificationService.deleteNotification(notificationId)

            return ResponseEntity.ok(
                ResponseData(true, "Notification deleted successfully", notificationId)
            )
        }

        return badRequestResponse()
    }

    @DeleteMapping("/delete/all")
    fun deleteAllNotifications(@RequestHeader("Authorization") token: String?): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val userId: String = authenticationService.getDataFromToken(token, "userId") as String

            sendNotificationService.deleteAllNotifications(userId)

            return ResponseEntity.ok(
                ResponseData(true, "Notifications deleted successfully", null)
            )
        }

        return badRequestResponse()
    }

    private fun badRequestResponse(): ResponseEntity<ResponseData> {
        return ResponseEntity.badRequest().body(
            ResponseData(false, "Invalid token", null)
        )
    }
}
