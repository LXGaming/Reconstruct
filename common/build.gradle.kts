plugins {
    id("net.kyori.blossom")
}

val asmVersion: String by project
val commonVersion: String by project
val proguardVersion: String by project
val slf4jVersion: String by project

base {
    archivesName = "reconstruct-common"
}

configurations {
    listOf(apiElements, runtimeElements).forEach {
        it.get().outgoing.artifacts.clear()
        it.get().outgoing.artifact(tasks.shadowJar)
    }
}

dependencies {
    api("io.github.lxgaming:common:${commonVersion}")
    compileJar("net.sf.proguard:proguard-base:${proguardVersion}")
    api("org.slf4j:slf4j-api:${slf4jVersion}")
    api("org.ow2.asm:asm-commons:${asmVersion}")
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("version", project.version.toString())
            }
        }
    }
}

tasks.jar {
    enabled = false
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    from("../LICENSE")
    rename("LICENSE", "LICENSE-Reconstruct")
}

tasks.shadowJar {
    archiveClassifier = ""
    configurations = listOf(project.configurations.compileJar.get())

    exclude("META-INF/maven/**")
    exclude {
        val proguardFiles = listOf(
            "proguard/obfuscate/MappingProcessor.class",
            "proguard/obfuscate/MappingReader.class"
        )

        // Exclude all ProGuard class files except the MappingProcessor & MappingReader
        if (it.path.startsWith("proguard/") && it.name.endsWith(".class")) {
            return@exclude !proguardFiles.contains(it.path)
        }

        return@exclude false
    }
}