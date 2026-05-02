// Top-level build file
plugins {
    id("com.android.application") version "8.13.2" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}