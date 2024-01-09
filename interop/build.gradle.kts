plugins {
    `java-conventions`
}

version = "1.0-SNAPSHOT"

// Package into fat jar
tasks.jar {
    archiveFileName.set("jastaddbridge-interop.jar")
    destinationDirectory.set(file(layout.projectDirectory))

    val dependencies =
            configurations
                    .runtimeClasspath
                    .get()
                    .map { zipTree(it) }
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA") // avoid signatures from dependencies
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}