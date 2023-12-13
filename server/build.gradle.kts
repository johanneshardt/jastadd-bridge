plugins {
    id("application")
}

group = "org.dagjohannes"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }
}

// Package into fat jar
tasks.jar {
    archiveFileName.set("server.jar")
    destinationDirectory.set(file(layout.projectDirectory))
    manifest.attributes["Main-Class"] = "org.dagjohannes.Main"

    val dependencies =
            configurations
                    .runtimeClasspath
                    .get()
                    .map { zipTree(it) }
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA") // TODO avoid signatures from dependencies
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:0.21.1")
    implementation(files("code-prober.jar"))
    implementation("org.jastadd:jastadd:2.3.6")
    // logging
    implementation("org.tinylog:tinylog-api:2.6.2")
    implementation("org.tinylog:tinylog-impl:2.6.2")
}

tasks.test {
    useJUnitPlatform()
}