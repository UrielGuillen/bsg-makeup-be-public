package com.bsgmakeup.controller

import com.bsgmakeup.dto.AuthenticateUserDTO
import com.bsgmakeup.dto.CreateUserDTO
import com.bsgmakeup.dto.ResponseData
import com.bsgmakeup.entity.UserEntity
import com.bsgmakeup.service.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthenticationController(
    private val authService: AuthenticationService
) {

    @PostMapping("/sign-up")
    fun signUp(@RequestBody createUser: CreateUserDTO): ResponseEntity<ResponseData> {

        if (!authService.userExists(createUser.phoneNumber)) {
            val userEntity: UserEntity = authService.signUp(createUser)

            val token = authService.signInUser(userEntity)

            return ResponseEntity.ok(
                ResponseData(true, "User created successfully", token)
            )
        }

        return ResponseEntity.internalServerError().body(
            ResponseData(false, "User cannot be created", null)
        )
    }

    @PostMapping("/sign-in")
    fun signIn(@RequestBody userToAuth: AuthenticateUserDTO): ResponseEntity<ResponseData> {
        val userEntity = authService.validateUser(userToAuth)

        if (userEntity != null) {
            val token = authService.signInUser(userEntity)

            return ResponseEntity.ok(
                ResponseData(true, "User sign in successfully", token)
            )
        }

        return ResponseEntity.badRequest().body(
            ResponseData(false, "User cannot be founded", null)
        )
    }
}