package com.masselis.tpmsadvanced.bitwarden

import com.masselis.tpmsadvanced.bitwarden.task.FetchAttachmentsTask
import com.masselis.tpmsadvanced.bitwarden.valuesource.FetchSession
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.from
import org.gradle.kotlin.dsl.register
import org.gradle.process.ExecOperations
import java.util.Properties
import javax.inject.Inject

public abstract class BitwardenPlugin : Plugin<Project> {

    @get:Inject
    internal abstract val execOperations: ExecOperations

    override fun apply(project: Project): Unit = with(project) {
        val ext = extensions.create<BitwardenExtension>("bitwarden")

        val localProperties = providers
            .fileContents(layout.projectDirectory.file("local.properties"))
            .asText
            .map { text -> Properties().apply { load(text.reader()) } }

        fun resolve(envVar: String, propertyKey: String): Provider<String> = providers
            .environmentVariable(envVar)
            .orElse(localProperties.map { it.getProperty(propertyKey).orEmpty() })

        ext.email.convention(resolve("BITWARDEN_EMAIL", "bitwarden.email"))
        ext.password.convention(resolve("BITWARDEN_PASSWORD", "bitwarden.password"))
        ext.server.convention(
            resolve("BITWARDEN_SERVER", "bitwarden.server").map {
                when (it.lowercase()) {
                    "bitwarden.com", BitwardenServer.BitwardenCom.url -> BitwardenServer.BitwardenCom
                    "bitwarden.eu", BitwardenServer.BitwardenEu.url -> BitwardenServer.BitwardenEu
                    else -> BitwardenServer.SelfHosted(it)
                }
            }
        )
        ext.item.convention(resolve("BITWARDEN_ITEM", "bitwarden.item"))

        val session = providers.from(FetchSession::class) {
            this.email = ext.email
            this.password = ext.password
            this.server = ext.server
        }

        tasks.register<FetchAttachmentsTask>("downloadBitwardenSecretFiles") {
            this.session = session
            this.itemName = ext.item
            this.files = mapOf(
                "app-keystore" to rootProject.layout.projectDirectory.file("secrets/app-keystore"),
                "publisher-service-account.json" to rootProject.layout.projectDirectory.file("secrets/publisher-service-account.json"),
                "google-services.json" to rootProject.layout.projectDirectory.file("app/phone/google-services.json"),
                "keys.json" to rootProject.layout.projectDirectory.file("secrets/keys.json"),
            )
        }
    }
}
