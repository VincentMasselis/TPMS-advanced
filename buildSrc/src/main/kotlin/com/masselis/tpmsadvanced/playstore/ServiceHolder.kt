package com.masselis.tpmsadvanced.playstore

import com.google.api.services.androidpublisher.AndroidPublisher
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import java.util.concurrent.locks.Lock

internal interface ServiceHolder {
    @get:ServiceReference("android-publisher-service")
    val androidPublisherService: Property<AndroidPublisherService>
}

internal val ServiceHolder.androidPublisher: AndroidPublisher
    get() = androidPublisherService.get().androidPublisher

internal val ServiceHolder.editsLock: Lock
    get() = androidPublisherService.get().editsLock
