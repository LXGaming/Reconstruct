import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("java")
    id("maven-publish")
    id("signing")
    id("com.gradleup.shadow") version "8.3.4" apply false
    id("net.kyori.blossom") version "2.1.0" apply false
}

subprojects {
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    val annotationsVersion: String by project
    val junitVersion: String by project

    group = "io.github.lxgaming"

    val compileJar: Configuration by configurations.creating

    configurations {
        implementation {
            extendsFrom(compileJar)
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:${junitVersion}")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        withJavadocJar()
        withSourcesJar()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.base.archivesName.get()
                version = project.version.toString()
                pom {
                    name = "Reconstruct"
                    description = "ProGuard Deobfuscator"
                    url = "https://github.com/LXGaming/Reconstruct"
                    developers {
                        developer {
                            id = "lxgaming"
                            name = "LXGaming"
                        }
                    }
                    issueManagement {
                        system = "GitHub Issues"
                        url = "https://github.com/LXGaming/Reconstruct/issues"
                    }
                    licenses {
                        license {
                            name = "The Apache License, Version 2.0"
                            url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                        }
                    }
                    scm {
                        connection = "scm:git:https://github.com/LXGaming/Reconstruct.git"
                        developerConnection = "scm:git:https://github.com/LXGaming/Reconstruct.git"
                        url = "https://github.com/LXGaming/Reconstruct"
                    }
                }
            }
        }
        repositories {
            if (project.hasProperty("sonatypeUsername") && project.hasProperty("sonatypePassword")) {
                maven {
                    name = "sonatype"
                    url = if (project.version.toString().contains("-SNAPSHOT")) {
                        uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
                    } else {
                        uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
                    }

                    credentials {
                        username = project.property("sonatypeUsername").toString()
                        password = project.property("sonatypePassword").toString()
                    }
                }
            }
        }
    }

    signing {
        if (project.hasProperty("signingKey") && project.hasProperty("signingPassword")) {
            useInMemoryPgpKeys(
                project.property("signingKey").toString(),
                project.property("signingPassword").toString()
            )
        }

        sign(publishing.publications["maven"])
    }

    tasks.jar {
        manifest {
            attributes(
                "Implementation-Title" to "Reconstruct",
                "Implementation-Vendor" to "LX_Gaming",
                "Implementation-Version" to project.version.toString(),
                "Specification-Title" to "Reconstruct",
                "Specification-Vendor" to "LX_Gaming",
                "Specification-Version" to "1"
            )
        }
    }

    tasks.javadoc {
        isFailOnError = false
        options {
            this as CoreJavadocOptions

            addStringOption("Xdoclint:none", "-quiet")
        }
    }

    tasks.test {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
        }

        useJUnitPlatform()
    }
}

tasks.jar {
    enabled = false
}