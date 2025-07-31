package io.github.neallon.repo

import io.github.neallon.repo.model.ResolvedRepository
import java.io.File
import java.util.Properties

class GradlePropertiesRepositoryLoader(
    private val gradleUserHomeDir: File? = null,
    private val projectRootDir: File? = null,
    private val extraProperties: Map<String, String> = emptyMap() // Simulate Gradle project.properties
) {

    /**
     * Load and parse all repository configurations
     */
    fun loadRepositories(): List<ResolvedRepository> {
        val props = Properties()

        // 1. Load ~/.gradle/gradle.properties
        gradleUserHomeDir?.let { userHome ->
            val userPropsFile = File(userHome, "gradle.properties")
            if (userPropsFile.exists()) {
                userPropsFile.inputStream().use { inputStream -> props.load(inputStream) }
            }
        }

        // 2. Load project root gradle.properties and override properties with the same key
        projectRootDir?.let { projectRoot ->
            val projectPropsFile = File(projectRoot, "gradle.properties")
            if (projectPropsFile.exists()) {
                projectPropsFile.inputStream().use { inputStream -> props.load(inputStream) }
            }
        }

        // 3. Load extra properties, set only when key does not exist to preserve priority
        extraProperties.forEach { (key, value) ->
            if (!props.containsKey(key)) {
                props[key] = value
            }
        }

        return parseRepositories(props)
    }

    /**
     * Parse repository list from Properties.
     * Naming convention:
     *  - hfx.<repoId>.repo.url
     *  - hfx.<repoId>.repo.user
     *  - hfx.<repoId>.repo.password
     *  - hfx.<repoId>.repo.allowInsecure (defaults to true if URL uses http)
     */
    private fun parseRepositories(properties: Properties): List<ResolvedRepository> {
        val repoIdRegex = Regex("""^hfx\.(.+?)\.repo\.url$""")

        val repoIds = properties.stringPropertyNames()
            .mapNotNull { key -> repoIdRegex.matchEntire(key)?.groupValues?.get(1) }
            .distinct()

        val repositories = mutableListOf<ResolvedRepository>()

        repoIds.forEach { id ->
            val prefix = "hfx.$id.repo"
            val url = properties.getProperty("$prefix.url") ?: run {
                println("[WARN] Missing URL for repository '$id', skipping...")
                return@forEach
            }
            val rawName = properties.getProperty("$prefix.name") ?: id
            val name = rawName.split(Regex("\\s+"))
                .joinToString("") { it.replaceFirstChar(Char::uppercaseChar) }

            val user = properties.getProperty("$prefix.user")
            val password = properties.getProperty("$prefix.password")

            val allowInsecureProp = properties.getProperty("$prefix.allowInsecure")
            val allowInsecureProtocol = allowInsecureProp?.toBooleanStrictOrNull() ?: url.startsWith("http:")

            repositories += ResolvedRepository(
                id = id,
                name = name,
                url = url,
                user = user,
                password = password,
                allowInsecureProtocol = allowInsecureProtocol
            )
        }

        return repositories
    }
}