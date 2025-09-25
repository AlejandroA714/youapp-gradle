package com.sv.youapp.formatter

import org.gradle.api.Project
import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import com.diffplug.gradle.spotless.KotlinExtension
import com.diffplug.gradle.spotless.KotlinGradleExtension
import com.diffplug.gradle.spotless.FormatExtension

class FormatterPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply("com.diffplug.spotless")
        target.extensions.configure(SpotlessExtension::class.java) { spotless ->
            spotless.kotlinGradle { ext: KotlinGradleExtension ->
                ext.ktlint()
            }
            spotless.kotlin { ext: KotlinExtension ->
                ext.ktlint()
                ext.target("**/*.kt")
            }
            spotless.java { ext ->
                ext.googleJavaFormat()
                ext.target("**/*.java")
            }
            spotless.format("misc") { ext: FormatExtension ->
                ext.target("**/*.md", "**/.gitignore")
                ext.trimTrailingWhitespace()
                ext.endWithNewline()
            }
        }
    }
}