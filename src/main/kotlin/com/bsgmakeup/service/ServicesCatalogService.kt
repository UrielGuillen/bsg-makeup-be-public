package com.bsgmakeup.service

import com.bsgmakeup.dto.ServicesCatalogDTO
import com.bsgmakeup.repository.MicroRetouchRepository
import com.bsgmakeup.repository.ServicesCatalogRepository
import org.springframework.stereotype.Service

@Service
class ServicesCatalogService(private val servicesCatalogRepository: ServicesCatalogRepository,
                             private val microRetouchRepository: MicroRetouchRepository) {

    fun getServicesByUserType(userType: String?): ServicesCatalogDTO {
        if (userType != null) {
            if (userType == "user") {
                return ServicesCatalogDTO(servicesCatalogRepository.findByUserType(userType), emptyList())
            }

            return ServicesCatalogDTO(
                servicesCatalogRepository.findAll(),
                microRetouchRepository.findAll()
            )
        }

        return ServicesCatalogDTO(emptyList(), emptyList())
    }
}
