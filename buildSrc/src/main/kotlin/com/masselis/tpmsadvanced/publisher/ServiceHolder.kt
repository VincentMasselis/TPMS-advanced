package com.masselis.tpmsadvanced.publisher

import com.google.api.services.androidpublisher.AndroidPublisher
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference

public interface ServiceHolder {
    @get:ServiceReference("android-publisher-service")
    public abstract val service: Property<AndroidPublisherService>
}

public val ServiceHolder.androidPublisher: AndroidPublisher
    get() = service.get().androidPublisher