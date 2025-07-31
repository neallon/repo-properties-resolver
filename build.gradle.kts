plugins {
    kotlin("jvm") version "2.1.20"
    `maven-publish`
    id ("org.danilopianini.publish-on-central") version "9.1.0"
}

group = "io.github.neallon"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11) // 你需要的JDK版本
}

signing {
    useGpgCmd() // 调用本地 gpg 命令签名，如果你本机已配置 gpg agent
    sign(publishing.publications)
}


publishing {
    publications {
        withType<MavenPublication> {
            pom {
                developers {
                    developer {
                        name.set("Repo Properties Resolver")
                        email.set("zhai.yf@foxmail.com")
                        url.set("https://github.com/neallon/repo-properties-resolver")
                    }
                }
            }
        }
    }
}

publishOnCentral {
    repoOwner.set("neallon")
    projectDescription.set("A lightweight Kotlin library to resolve Maven repository definitions from gradle.properties for Gradle plugins.")
    projectLongName.set(project.name)
    licenseName.set("Apache License, Version 2.0")
    licenseUrl.set("http://www.apache.org/licenses/LICENSE-2.0")
    projectUrl.set("https://github.com/${repoOwner.get()}/${project.name}")
    scmConnection.set("scm:git:https://github.com/${repoOwner.get()}/${project.name}")

    repository(projectUrl.get(), "GitHub") {
        user.set(findProperty("GITHUB_USERNAME")as String)
        password.set(findProperty("GITHUB_TOKEN") as String)
    }
}
