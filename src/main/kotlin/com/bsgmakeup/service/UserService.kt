package com.bsgmakeup.service

import com.bsgmakeup.dto.UserDataDTO
import com.bsgmakeup.dto.UserPublicDataDTO
import com.bsgmakeup.entity.UserEntity
import com.bsgmakeup.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    protected val userRepository: UserRepository
) {

    fun getUserData(userId: String?): UserDataDTO? {
        if (userId != null) {
            val userEntity = userRepository.findById(userId)

            return UserDataDTO(
                id = userEntity.get().id,
                name = userEntity.get().name,
                lastName = userEntity.get().lastName,
                phoneNumber = userEntity.get().phoneNumber,
                email = userEntity.get().email,
                profileUrl = userEntity.get().profileUrl
            )
        }

        return null
    }

    fun updateUserProfileImage(userId: String?, profileImageUrl: String): UserEntity? {
        if (userId != null) {
            val userEntity = userRepository.findById(userId)

            if (userEntity.isPresent) run {
                val userUpdated: UserEntity = userEntity.get()
                userUpdated.profileUrl = profileImageUrl
                userUpdated.modifiedDate = Date()

                return userRepository.save(userUpdated)
            }

            return null
        }

        return null
    }

    fun updateUserBasicData(userId: String?, userDataDTO: UserPublicDataDTO): UserDataDTO? {
        if (userId != null) {
            val userEntity = userRepository.findById(userId)

            if (userEntity.isPresent) run {
                val userToUpdate: UserEntity = userEntity.get()
                userToUpdate.name = userDataDTO.name
                userToUpdate.lastName = userDataDTO.lastName
                userToUpdate.email = userDataDTO.email
                userToUpdate.modifiedDate = Date()

                val userUpdated = userRepository.save(userToUpdate)

                return UserDataDTO(
                    id = userUpdated.id,
                    name = userUpdated.name,
                    lastName = userUpdated.lastName,
                    email = userUpdated.email,
                    phoneNumber = userUpdated.phoneNumber,
                    profileUrl = userUpdated.profileUrl
                )
            }

            return null
        }

        return null
    }
}
