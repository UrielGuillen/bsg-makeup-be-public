package com.bsgmakeup.repository

import com.bsgmakeup.entity.ManualUserEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface ManualUserRepository: MongoRepository<ManualUserEntity, String> {
}
