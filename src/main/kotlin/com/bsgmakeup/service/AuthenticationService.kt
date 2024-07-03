package com.bsgmakeup.service

import com.auth0.jwt.JWT
import com.bsgmakeup.dto.AuthenticateUserDTO
import com.bsgmakeup.dto.CreateUserDTO
import com.bsgmakeup.dto.NotificationDTO
import com.bsgmakeup.entity.UserEntity
import com.bsgmakeup.repository.UserRepository
import com.bsgmakeup.repository.UserTokenRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.security.Key

@Service
class AuthenticationService(
    protected val userRepository: UserRepository,
    protected val userTokenRepository: UserTokenRepository,
    private val sendNotificationService: SendNotificationService,
) {

    fun signUp(newUser: CreateUserDTO): UserEntity {
        val passwordEncoder = BCryptPasswordEncoder()

        val newUserEntity = UserEntity(
            name = newUser.name,
            lastName = newUser.lastName,
            phoneNumber = newUser.phoneNumber,
            email = newUser.email,
            password = passwordEncoder.encode(newUser.password),
            profileUrl = null
        )

        val savedUser = userRepository.save(newUserEntity)
        val notificationContent = savedUser.name + " " + savedUser.lastName + " se ha unido a la plataforma"

        sendNotificationService.notifyAdmins(NotificationDTO(
            userIdFrom = savedUser.id,
            title = "Usuario nuevo",
            content = notificationContent,
            icon = "group_add"
        ))

        return savedUser
    }

    fun userExists(phoneNumber: String): Boolean {
        val user: UserEntity? = userRepository.findByPhoneNumber(phoneNumber)
        return user != null
    }

    fun validateUser(userToAuth: AuthenticateUserDTO): UserEntity? {
        val userEntity = userRepository.findByPhoneNumber(userToAuth.phoneNumber)

        if (userEntity != null && validatePassword(userToAuth.password, userEntity.password)) {
            return userEntity
        }

        return null
    }

    fun validatePassword(signInPassword: String, userPassword: String): Boolean {
        val passwordEncoder = BCryptPasswordEncoder()

        return passwordEncoder.matches(signInPassword, userPassword)
    }

    fun signInUser(userEntity: UserEntity): String {
        val userToken = userTokenRepository.findByUserId(userEntity.id)

        val userData = hashMapOf<String, Any>()
        userData["user"] = userEntity
        userData["userRole"] = userEntity.role
        userData["userId"] = userEntity.id
        userData["currentToken"] = userToken?.token ?: ""

        return Jwts.builder().setIssuer(userEntity.id)
            .setClaims(userData)
            .signWith(getJwtKey()).compact()
    }

    fun isAValidToken(token: String?): Boolean {
        if (token == null) {
            return false
        }

        return try {
            Jwts.parser().setSigningKey(getJwtKey()).parseClaimsJws(token).body
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isUserAllowed(token: String?): Boolean {
        if (isAValidToken(token)) {
            val userRole: String = getDataFromToken(token as String, "userRole") as String
            return userRole != "user"
        }

        return false
    }

    fun getJwtKey(): Key {
        val jwtKey = "daf66e01593f61a15b857cf433aae03a005812b31234e149036bcc8dee755dbb"

        return Keys.hmacShaKeyFor(
            Decoders.BASE64.decode(jwtKey)
        )
    }

    fun getDataFromToken(token: String, data: String): String? {
        if (isAValidToken(token)) {
            val decodedJWT = JWT.decode(token)

            return decodedJWT.getClaim(data).asString()
        }
        return null
    }
}
