val projectRevision: String by project

plugins {
    kotlin("jvm")
    id("signing")
    id("maven-publish")
    id("io.codearte.nexus-staging")
}

group = "ru.potatophobe"
version = projectRevision

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            val ossrhUsername: String by project
            val ossrhPassword: String by project

            url = run {
                if (projectRevision.endsWith("SNAPSHOT")) uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                else uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            }
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("Validsl")
                description.set("Type-safe and extensible DSL to validate Kotlin objects")
                url.set("https://github.com/potatophobe/validsl")
                developers {
                    developer {
                        id.set("potatophobe")
                        name.set("Artem Stroev")
                        email.set("potatophobe@gmail.com")
                    }
                }
                licenses {
                    license {
                        name.set("Apache License Version 2.0")
                        url.set("https://raw.githubusercontent.com/potatophobe/validsl/master/LICENSE")
                    }
                }
                scm {
                    url.set("https://github.com/potatophobe/validsl")
                    connection.set("scm:git:https://github.com/potatophobe/validsl.git")
                    developerConnection.set("scm:git:https://github.com/potatophobe/validsl.git")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

nexusStaging {
    val ossrhUsername: String by project
    val ossrhPassword: String by project

    packageGroup = "ru.potatophobe"
    serverUrl = "https://s01.oss.sonatype.org/service/local/"
    username = ossrhUsername
    password = ossrhPassword
}
