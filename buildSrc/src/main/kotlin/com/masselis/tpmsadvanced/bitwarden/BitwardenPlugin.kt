package com.masselis.tpmsadvanced.bitwarden

import Keys
import com.masselis.tpmsadvanced.bitwarden.valuesource.BitwardenCredentials
import com.masselis.tpmsadvanced.bitwarden.valuesource.FetchBitwardenAttachment
import com.masselis.tpmsadvanced.bitwarden.valuesource.FetchBitwardenField
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.from
import java.util.Properties

private const val APP_KEYSTORE_ITEM = "TPMS-Advanced - App Keystore"
private const val PUBLISHER_SERVICE_ACCOUNT_ITEM = "TPMS-Advanced - Play Publisher Service Account"
private const val GOOGLE_SERVICES_ITEM = "TPMS-Advanced - Google Services"
private const val BUILD_KEYS_ITEM = "TPMS-Advanced - Build Keys"

public class BitwardenPlugin : Plugin<Project> {
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
        val credentials = providers.provider {
            if (ext.email.isPresent.not() || ext.password.isPresent.not() || ext.server.isPresent.not())
                return@provider null
            BitwardenCredentials(
                ext.email.get(),
                ext.password.get(),
                ext.server.get()
            )
        }

        val cliConfigDir = layout.projectDirectory.dir(".gradle/bitwarden-cli")

        fun fetchAttachment(itemName: String, outputFile: RegularFile) = providers
            .from(FetchBitwardenAttachment::class) {
                this.credentials = credentials
                this.cliConfigDir = cliConfigDir
                this.itemName = itemName
                this.outputFile = outputFile
            }
            .orNull

        fun fetchField(itemName: String, fieldName: String) = providers
            .from(FetchBitwardenField::class) {
                this.credentials = credentials
                this.cliConfigDir = cliConfigDir
                this.itemName = itemName
                this.fieldName = fieldName
            }
            .orNull

        fetchAttachment(
            APP_KEYSTORE_ITEM,
            layout.projectDirectory.file("secrets/app-keystore")
        )
        fetchAttachment(
            PUBLISHER_SERVICE_ACCOUNT_ITEM,
            layout.projectDirectory.file("secrets/publisher-service-account.json")
        )
        fetchAttachment(
            GOOGLE_SERVICES_ITEM,
            layout.projectDirectory.file("app/phone/google-services.json")
        )

        val appKeyStorePwd = fetchField(BUILD_KEYS_ITEM, "APP_KEY_STORE_PWD")
        val appKeyAlias = fetchField(BUILD_KEYS_ITEM, "APP_KEY_ALIAS")
        val githubToken = fetchField(BUILD_KEYS_ITEM, "GITHUB_TOKEN")
        if (appKeyStorePwd != null && appKeyAlias != null && githubToken != null)
            extra["keys"] = Keys(appKeyStorePwd, appKeyAlias, githubToken)
    }
}
