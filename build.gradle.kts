// Root build — plugin declarations only (no apply).
// Actual configuration lives in :app/build.gradle.kts.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose)      apply false
    alias(libs.plugins.ksp)                 apply false
    alias(libs.plugins.hilt.android)        apply false
}
