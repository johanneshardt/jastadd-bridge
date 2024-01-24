rootProject.name = "jastadd-bridge"
include("server")
include("interop")
include("CalcRAG")
project(":CalcRAG").projectDir = file("examples/CalcRAG")

plugins {
    id("org.gradle.toolchains.foojay-resolver") version "0.8.0"
}

toolchainManagement {
    jvm {
        javaRepositories {
            repository("foojay") {
                resolverClass = org.gradle.toolchains.foojay.FoojayToolchainResolver::class.java
            }
        }
    }
}