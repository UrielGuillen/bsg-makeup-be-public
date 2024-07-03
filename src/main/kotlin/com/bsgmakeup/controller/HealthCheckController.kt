package com.bsgmakeup.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {

    @GetMapping("/health")
    fun healthCheck(): String {
        return "OK";
    }

    @GetMapping("/verify-token")
    fun verifyFirebaseToken(@RequestHeader("Authorization") token: String?): Boolean {
        if (token != null) {
            val decodedToken: FirebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)

            System.out.println(decodedToken)

            val uid: String = decodedToken.uid

            System.out.println("Token valid???")
            System.out.println(uid)

            return true;
        }

        System.out.println("Token doesn't received")
        return false;
    }
}
