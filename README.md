repo-properties-resolver
========================

Introduction
------------

repo-properties-resolver is a lightweight Kotlin library designed to simplify reading Maven repository configurations from Gradle properties files (gradle.properties).  
It supports extracting repository URLs, credentials, and other common options, allowing multiple Gradle plugins or build scripts to reuse a single source of repository configuration.

This library works seamlessly in both Project and Settings contexts within Gradle builds.

Features
--------

- Load and merge properties from the user Gradle home ~/.gradle/gradle.properties and the project root gradle.properties
- Parse repository configurations following a standardized naming convention:
    - hfx.<repoId>.repo.url
    - hfx.<repoId>.repo.user
    - hfx.<repoId>.repo.password
    - hfx.<repoId>.repo.allowInsecure
      > Note: If `hfx.<repoId>.repo.allowInsecure` is **not set** and the URL starts with `http:`,  
      > the `allowInsecureProtocol` flag will default to **true** to allow insecure HTTP connections.
- Return a list of ResolvedRepository data classes representing repositories and their credentials
- Designed for easy integration into Gradle plugins or build scripts for automatic repository injection

Usage
-----

Add the dependency in your Gradle build:

    dependencies {
        implementation("io.github.neallon:repo-properties-resolver:1.0.0")
    }

Make sure your build script resolves Maven Central or other default repositories:

    repositories {
        mavenCentral()
    }

Sample gradle.properties configuration:

    hfx.myRepo.repo.url=https://maven.aliyun.com/repository/public
    hfx.myRepo.repo.user=admin
    hfx.myRepo.repo.password=secret
    hfx.customRepo.repo.url=https://custom.repo.com/repository
    hfx.customRepo.repo.allowInsecure=true

Read repositories in your Gradle plugin or build script:

    val repositories = RepositoryPropertiesReader.from(project) // or settings
    repositories.forEach { repo ->
        println("Repository: ${repo.name}, URL: ${repo.url}")
        if (repo.user != null) {
            println("Credentials: ${repo.user}/****")
        }
    }

Inject repositories into Gradle DSL:

    repositories.forEach { repo ->
        repositories.maven {
            name = repo.name
            url = uri(repo.url)
            if (repo.user != null && repo.password != null) {
                credentials {
                    username = repo.user
                    password = repo.password
                }
            }
            if (repo.isAllowInsecureProtocol) {
                allowInsecureProtocol = true
            }
        }
    }

Publishing
----------

Published to Maven Central under:

- Group ID: io.github.neallon
- Artifact ID: repo-properties-resolver
- Version: 1.0.0

License
-------

MIT License © 2025 Neallon