plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
}

group = "com.example"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
}

kotlin {
    js {
        browser {
            webpackTask {
                mainOutputFileName = "main.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
        }
    }
}

val extensionBuildDir: Provider<Directory> = project.layout.buildDirectory.dir("playground")

tasks.register<Copy>("prepareExtension") {
    dependsOn("jsBrowserProductionWebpack")

    val manifestFile = project.layout.projectDirectory.file("data/manifest.json")
    inputs.file(manifestFile)
    inputs.property("version", version.toString())

    doFirst {
        with(extensionBuildDir.get().asFile) {
            deleteRecursively()
            mkdirs()
            val manifest = manifestFile.asFile.readText()
            resolve(manifestFile.asFile.name).writeText(
                manifest.replace("<VERSION>", version.toString())
            )
        }
    }

    from(project.layout.buildDirectory.file("kotlin-webpack/js/productionExecutable/main.js"))
    into(extensionBuildDir)
}

tasks.register<Zip>("packExtension") {
    dependsOn("prepareExtension")
    inputs.dir(extensionBuildDir)

    from(extensionBuildDir)
    archiveFileName.set("playground.zip")
}