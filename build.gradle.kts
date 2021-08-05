
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${DependencyVersions.KOTLIN}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}

subprojects {
    configurations.configureEach {
        // We forcefully exclude AppCompat + MDC from any transitive dependencies.
        // This is a Compose app, so there's no need for these.
        exclude(group = "androidx.appcompat", module = "appcompat")
        exclude(group = "com.google.android.material", module = "material")
        exclude(group = "com.google.android.material", module = "material")
    }
}

plugins {
    id("com.github.ben-manes.versions") version "0.36.0"
}

//tasks.named("dependencyUpdates", DependencyUpdatesTask::class.java).configure {
//    rejectVersionIf {
//        candidate.version.contains("alpha") && !this@rejectVersionIf.currentVersion.contains("alpha")
//    }
//}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
