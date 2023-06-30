package com.masselis.tpmsadvanced.publisher

import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

internal abstract class AndroidPublisherService : BuildService<AndroidPublisherService.Params> {

    val packageName: String = parameters.packageName.get()

    val androidPublisher by lazy {
        AndroidPublisher
            .Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                HttpCredentialsAdapter(
                    GoogleCredentials.fromStream(
                        parameters
                            .serviceAccountCredentials
                            .get()
                            .asFile
                            .inputStream()
                    )
                )
            )
            .setApplicationName("TPMS Advanced")
            .build()
    }

    interface Params : BuildServiceParameters {
        val serviceAccountCredentials: RegularFileProperty
        val packageName: Property<String>
    }
}