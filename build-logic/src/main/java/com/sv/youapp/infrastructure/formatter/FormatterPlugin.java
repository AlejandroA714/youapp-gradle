package com.sv.youapp.infrastructure.formatter;

import com.diffplug.gradle.spotless.FormatExtension;
import com.diffplug.gradle.spotless.KotlinExtension;
import com.diffplug.gradle.spotless.KotlinGradleExtension;
import com.diffplug.gradle.spotless.SpotlessExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class FormatterPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getPluginManager().apply("com.diffplug.spotless");
        target.getExtensions().configure(SpotlessExtension.class, spotless -> {
        spotless.kotlinGradle((KotlinGradleExtension ext) -> {
        //ext.ktlint();
    });
//        spotless.kotlin((KotlinExtension ext) -> {
//        ext.ktlint();
//        ext.target("**/*.kt");
//    });
        spotless.java(ext -> {
        ext.googleJavaFormat();
        ext.target("**/*.java");
    });
        spotless.format("misc", (FormatExtension ext) -> {
        ext.target("**/*.md", "**/.gitignore");
        ext.trimTrailingWhitespace();
        ext.endWithNewline();
    });
    });
    }
}
