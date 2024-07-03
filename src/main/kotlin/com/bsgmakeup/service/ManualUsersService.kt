package com.bsgmakeup.service

import com.bsgmakeup.entity.ManualUserEntity
import com.bsgmakeup.repository.ManualUserRepository
import org.springframework.stereotype.Service

@Service
class ManualUsersService(
    private val manualUserRepository: ManualUserRepository
) {

    fun getAllUsersRegistered(): List<ManualUserEntity> {
        return manualUserRepository.findAll()
    }
}
