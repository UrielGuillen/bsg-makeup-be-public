package com.bsgmakeup.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.IOException
import javax.annotation.PostConstruct

@Configuration
class FirebaseConfig {

    @Value("\${app.firebase-configuration-file}")
    private lateinit var firebaseConfigPath: String

    @PostConstruct
    fun initialize() {
        try {
            val inputStream = ClassPathResource(firebaseConfigPath).inputStream

            val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream)).build()

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
