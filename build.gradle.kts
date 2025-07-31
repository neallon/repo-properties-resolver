plugins {
    kotlin("jvm") version "2.1.20"
    signing
    `maven-publish`
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
    jvmToolchain(11)
}
signing {
    useGpgCmd() // 使用系统 GPG 命令
    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "repo-properties-resolver"
            version = project.version.toString()

            pom {
                name.set("Repo Properties Resolver")
                description.set("A lightweight utility to resolve Maven repositories from gradle.properties")
                url.set("https://github.com/neallon/repo-properties-resolver")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("neallon")
                        name.set("neallon")
                        email.set("zhai.yf@foxmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/neallon/repo-properties-resolver.git")
                    developerConnection.set("scm:git:ssh://github.com/neallon/repo-properties-resolver.git")
                    url.set("https://github.com/neallon/repo-properties-resolver")
                }
            }
        }
    }

    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

            credentials {
                username = findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME")
                password = findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}