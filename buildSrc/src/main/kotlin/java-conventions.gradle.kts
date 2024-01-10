 plugins {
     application
 }

 java {
     toolchain {
         languageVersion.set(JavaLanguageVersion.of(17))
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

 repositories {
     mavenCentral()
 }