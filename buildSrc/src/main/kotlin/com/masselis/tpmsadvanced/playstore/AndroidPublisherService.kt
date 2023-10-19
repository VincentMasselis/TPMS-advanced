package com.masselis.tpmsadvanced.playstore

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes.ANDROIDPUBLISHER
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

internal abstract class AndroidPublisherService : BuildService<AndroidPublisherService.Params> {

    val editsLock: Lock = ReentrantLock()

    val androidPublisher: AndroidPublisher by lazy {
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        AndroidPublisher
            .Builder(
                transport,
                GsonFactory.getDefaultInstance(),
                HttpCredentialsAdapter(
                    GoogleCredentials
                        .fromStream(
                            parameters
                                .serviceAccountCredentials
                                .get()
                                .asFile
                                .inputStream(),
                            { transport }
                        )
                        .createScoped(listOf(ANDROIDPUBLISHER))
                ).withHttpTimeout(2.minutes)
            )
            .setApplicationName("TPMS Advanced publisher")
            .build()
    }

    private fun HttpRequestInitializer.withHttpTimeout(timeout: Duration) =
        object : HttpRequestInitializer {
            override fun initialize(request: HttpRequest) {
                this@withHttpTimeout.initialize(request)
                request.setConnectTimeout(timeout.inWholeMilliseconds.toInt())
                request.setReadTimeout(timeout.inWholeMilliseconds.toInt())
                request.setWriteTimeout(timeout.inWholeMilliseconds.toInt())
            }
        }

    internal interface Params : BuildServiceParameters {
        val serviceAccountCredentials: RegularFileProperty
    }
}