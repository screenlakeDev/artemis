// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.android.library") version "8.1.0" apply false
    id("maven-publish")

}

publishing {
    repositories {
        mavenLocal()
        // Other repositories (e.g., Maven Central) can be added here.
    }
}

buildscript {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

}