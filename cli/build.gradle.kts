val jansiVersion: String by project
val jcommanderVersion: String by project
val log4jVersion: String by project

base {
    archivesName = "reconstruct-cli"
}

configurations {
    listOf(apiElements, runtimeElements).forEach {
        it.get().outgoing.artifacts.clear()
        it.get().outgoing.artifact(tasks.shadowJar)
    }
}

dependencies {
    compileJar(project(path = ":reconstruct-common"))
    compileJar("org.apache.logging.log4j:log4j-core:${log4jVersion}")
    compileJar("org.apache.logging.log4j:log4j-slf4j2-impl:${log4jVersion}")
    compileJar("org.fusesource.jansi:jansi:${jansiVersion}")
    compileJar("org.jcommander:jcommander:${jcommanderVersion}")
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            artifact(tasks.javadocJar)
            artifact(tasks.shadowJar)
            artifact(tasks.sourcesJar)
        }
    }
}

tasks.compileJava {
    dependsOn(":reconstruct-common:build")
}

tasks.jar {
    enabled = false
    dependsOn(tasks.shadowJar)

    manifest {
        attributes(
            "Main-Class" to "io.github.lxgaming.reconstruct.cli.Main"
        )
    }
}

tasks.shadowJar {
    archiveClassifier = ""
    configurations = listOf(project.configurations.compileJar.get())

    dependencies {
        exclude(dependency("net.sf.proguard:proguard-base"))
    }

    exclude("META-INF/maven/**")
    exclude("META-INF/DEPENDENCIES")
    exclude("META-INF/LICENSE")
    exclude("META-INF/LICENSE.txt")
    exclude("META-INF/NOTICE")
}