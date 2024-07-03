package com.bsgmakeup.repository

import com.bsgmakeup.entity.ServicesCatalogEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface ServicesCatalogRepository: MongoRepository<ServicesCatalogEntity, String> {
    fun findByUserType(userType: String): List<ServicesCatalogEntity>

}
