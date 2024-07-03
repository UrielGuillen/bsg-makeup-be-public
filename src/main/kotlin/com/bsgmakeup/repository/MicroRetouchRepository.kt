package com.bsgmakeup.repository

import com.bsgmakeup.entity.MicroRetouchEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface MicroRetouchRepository: MongoRepository<MicroRetouchEntity, String> {
}