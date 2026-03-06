package com.shakeyourtown.missions.github

import com.shakeyourtown.missions.storage.SettingsStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Publishes missions.json to a GitHub repository via the GitHub Contents API.
 * This is what makes missions visible to citizens on GitHub Pages.
 *
 * API: PUT /repos/{owner}/{repo}/contents/{path}
 * Docs: https://docs.github.com/en/rest/repos/contents#create-or-update-file-contents
 */
object GitHubPublisher {

    private val httpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; encodeDefaults = true })
            }
        }
    }

    private val jsonParser = Json { ignoreUnknownKeys = true }

    /**
     * Push the given JSON content to the configured GitHub repository.
     * Returns a Result with a success message or error.
     */
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun publish(jsonContent: String): Result<String> {
        val settings = SettingsStorage.load()

        if (settings.githubOwner.isBlank() || settings.githubRepo.isBlank() || settings.githubToken.isBlank()) {
            return Result.failure(IllegalStateException("Configuration GitHub incomplète. Renseignez le propriétaire, dépôt et token dans les paramètres."))
        }

        val owner = settings.githubOwner
        val repo = settings.githubRepo
        val path = settings.githubFilePath.ifBlank { "data/missions.json" }
        val token = settings.githubToken

        return try {
            // 1. Get current file SHA (needed for updates, not for creation)
            val currentSha = getCurrentFileSha(owner, repo, path, token)

            // 2. Encode content to Base64
            val contentBase64 = Base64.encode(jsonContent.encodeToByteArray())

            // 3. PUT the file
            val apiUrl = "https://api.github.com/repos/$owner/$repo/contents/$path"
            val requestBody = GitHubPutRequest(
                message = "Mise à jour des missions",
                content = contentBase64,
                sha = currentSha
            )

            val response = httpClient.put(apiUrl) {
                header("Authorization", "Bearer $token")
                header("Accept", "application/vnd.github+json")
                header("X-GitHub-Api-Version", "2022-11-28")
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status.value in 200..299) {
                Result.success("Missions publiées avec succès !")
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(RuntimeException("Erreur GitHub (${response.status.value}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(RuntimeException("Erreur de publication: ${e.message}"))
        }
    }

    /**
     * Get the SHA of the current file (needed to update an existing file).
     * Returns null if the file doesn't exist yet (first publish).
     */
    private suspend fun getCurrentFileSha(
        owner: String,
        repo: String,
        path: String,
        token: String
    ): String? {
        return try {
            val apiUrl = "https://api.github.com/repos/$owner/$repo/contents/$path"
            val response = httpClient.get(apiUrl) {
                header("Authorization", "Bearer $token")
                header("Accept", "application/vnd.github+json")
                header("X-GitHub-Api-Version", "2022-11-28")
            }
            if (response.status.value == 200) {
                val body = response.bodyAsText()
                val fileInfo: GitHubFileResponse = jsonParser.decodeFromString(body)
                fileInfo.sha
            } else {
                null // File doesn't exist yet
            }
        } catch (_: Exception) {
            null
        }
    }
}

@Serializable
private data class GitHubPutRequest(
    val message: String,
    val content: String,
    val sha: String? = null
)

@Serializable
private data class GitHubFileResponse(
    val sha: String,
    val name: String = "",
    val path: String = ""
)
