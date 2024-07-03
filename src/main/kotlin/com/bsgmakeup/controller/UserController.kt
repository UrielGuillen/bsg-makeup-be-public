package com.bsgmakeup.controller

import com.bsgmakeup.dto.ResponseData
import com.bsgmakeup.dto.UserDataDTO
import com.bsgmakeup.dto.UserPublicDataDTO
import com.bsgmakeup.entity.UserEntity
import com.bsgmakeup.service.AuthenticationService
import com.bsgmakeup.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    protected val authenticationService: AuthenticationService,
    protected val userService: UserService
) {

    @GetMapping("/get-public-data")
    fun listAll(@RequestHeader("Authorization") token: String?): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val userId = authenticationService.getDataFromToken(token, "userId")
            val userData = userService.getUserData(userId)

            return ResponseEntity.ok(
                ResponseData(true, "User public data retrieved correctly", userData)
            )
        }

        return ResponseEntity.badRequest().body(
            ResponseData(false, "Invalid token", null)
        )
    }

    @PutMapping("/update-profile-image")
    fun updateUserProfile(@RequestHeader("Authorization") token: String?, @RequestBody profileImageUrl: String): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val userId = authenticationService.getDataFromToken(token, "userId")

            val userUpdated: UserEntity? = userService.updateUserProfileImage(userId, profileImageUrl)

            if (userUpdated != null && userUpdated.profileUrl !== null) {
                return ResponseEntity.ok(
                    ResponseData(true, "Profile image updated", null)
                )
            }

            return ResponseEntity.badRequest().body(
                ResponseData(false, "Error trying to update user image", null)
            )
        }

        return ResponseEntity.badRequest().body(
            ResponseData(false, "Invalid token", null)
        )
    }

    @PutMapping("/update-basic-data")
    fun updateUserBasicData(@RequestHeader("Authorization") token: String?, @RequestBody userDataDTO: UserPublicDataDTO): ResponseEntity<ResponseData> {
        if (authenticationService.isAValidToken(token) && token != null) {
            val userId = authenticationService.getDataFromToken(token, "userId")

            val userUpdated: UserDataDTO? = userService.updateUserBasicData(userId, userDataDTO)

            if (userUpdated != null) {
                return ResponseEntity.ok(
                    ResponseData(true, "User basic data updated", userUpdated)
                )
            }

            return ResponseEntity.badRequest().body(
                ResponseData(false, "Error trying to update user basic data", null)
            )
        }

        return ResponseEntity.badRequest().body(
            ResponseData(false, "Invalid token", null)
        )
    }
}
