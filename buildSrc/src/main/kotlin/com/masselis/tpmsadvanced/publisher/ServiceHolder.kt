package com.masselis.tpmsadvanced.publisher

import com.google.api.services.androidpublisher.AndroidPublisher
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import java.util.concurrent.locks.Lock

internal interface ServiceHolder {
    @get:ServiceReference("android-publisher-service")
    val service: Property<AndroidPublisherService>
}

internal val ServiceHolder.androidPublisher: AndroidPublisher
    get() = service.get().androidPublisher

internal val ServiceHolder.editsLock: Lock
    get() = service.get().editsLock
