plugins {
    kotlin("jvm") version "2.2.0-Beta2"
    id("com.gradleup.shadow") version "8.3.0"
}

group = "com.github.lms5413"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://jitpack.io") {
        name = "jitpack"
    }

    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    implementation("org.jetbrains.exposed:exposed-core:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.3")
    implementation("com.github.lms5413:inventory-api:1.0.8")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        val projectPackage = "com.github.lms5413.punishment"
        relocate ("co.aikar.commands", "$projectPackage.acf")
        relocate ("co.aikar.locales", "$projectPackage.locales")
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}
