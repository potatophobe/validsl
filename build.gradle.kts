val projectRevision: String by project

plugins {
    kotlin("jvm")
}

group = "ru.potatophobe"
version = projectRevision

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
