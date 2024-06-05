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
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
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
                                .inputStream()
                        ) { transport }
                        .createScoped(listOf(ANDROIDPUBLISHER))
                ).wrap {
                    val timeout = 2.minutes.inWholeMilliseconds.toInt()
                    setConnectTimeout(timeout)
                    setReadTimeout(timeout)
                    setWriteTimeout(timeout)
                }
            )
            .setApplicationName("TPMS Advanced publisher")
            .build()
    }

    private fun HttpRequestInitializer.wrap(block: HttpRequest.() -> Unit) =
        HttpRequestInitializer { request ->
            initialize(request)
            request.block()
        }

    internal interface Params : BuildServiceParameters {
        val serviceAccountCredentials: RegularFileProperty
    }
}