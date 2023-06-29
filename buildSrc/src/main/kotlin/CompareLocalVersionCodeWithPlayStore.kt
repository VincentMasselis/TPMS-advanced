import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.com.google.gson.Gson
import javax.inject.Inject

public abstract class CompareLocalVersionCodeWithPlayStore : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:InputFile
    public abstract val serviceAccountCredentials: RegularFileProperty

    @get:Input
    public abstract val currentVc: Property<Int>

    init {
        group = "publishing"
        description =
            "Ensure the artifact to be promoted by promoteArtifact will be generated from the current commit"
    }

    @TaskAction
    internal fun process() {
        val playStoreVc = AndroidPublisher.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            HttpCredentialsAdapter(
                GoogleCredentials.fromStream(serviceAccountCredentials.asFile.get().inputStream())
            )
        ).setApplicationName("TPMS Advanced")
            .build()
            .edits()
            .run {
                val packageName = "com.masselis.tpmsadvanced"
                val id = insert(packageName, null).execute().id
                tracks()
                    .get(packageName, id, "beta")
                    .execute()
                    .releases
                    .first()
                    .versionCodes
                    .first()
                    .toInt()
                    .also { commit(packageName, id).execute() }
            }
        if (playStoreVc != currentVc.get())
            throw GradleException("Current commit version code (${currentVc.get()}) differs to the current in beta from the play store ($playStoreVc)")
    }

    internal companion object {
        private val gson = Gson()
    }
}