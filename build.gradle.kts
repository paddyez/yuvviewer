plugins {
    java
    jacoco
    id("org.sonarqube") version "7.5.0.8588"
    kotlin("jvm")
}
group = "org.yuvViewer"
version = "1.0-SNAPSHOT"
description = "yuvViewer"
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:deprecation")
    options.isDeprecation = true
}
tasks.compileJava {
    options.headerOutputDirectory.set(layout.buildDirectory.dir("generated/jni"))
}
val runtimeClasspath = configurations.runtimeClasspath
val copyRuntimeDependencies by tasks.registering(Sync::class) {
    from(runtimeClasspath)
    into(layout.buildDirectory.dir("libs/lib"))
}
tasks.jar {
    dependsOn(copyRuntimeDependencies)
    manifest {
        attributes(
            "Main-Class" to "org.yuvViewer.Main",
            "Class-Path" to runtimeClasspath.get().files.joinToString(" ") { "lib/${it.name}" }
        )
    }
}
tasks.test {
    dependsOn(tasks.jar)
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
repositories {
    mavenCentral()
}
val pitestClasspath by configurations.creating
dependencies {
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor("org.projectlombok:lombok:1.18.48")
    implementation("org.apache.logging.log4j:log4j-api:2.26.1")
    implementation("org.apache.logging.log4j:log4j-core:2.26.1")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.1.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    pitestClasspath("org.pitest:pitest-command-line:1.30.0")
    pitestClasspath("org.pitest:pitest-junit5-plugin:1.2.2")
    implementation(kotlin("stdlib-jdk8"))
}
tasks.register<JavaExec>("pitest") {
    group = "verification"
    description = "Runs Pitest mutation testing."
    dependsOn(tasks.classes, tasks.testClasses)
    mainClass.set("org.pitest.mutationtest.commandline.MutationCoverageReport")
    val sourceSets = project.extensions.getByType<SourceSetContainer>()
    classpath = files(pitestClasspath, sourceSets["main"].runtimeClasspath, sourceSets["test"].runtimeClasspath)
    // GUI classes (MainFrame, YUVViewer, SettingsDialog, FrameAboutBox, Main) require a
    // display and are skipped by all tests in headless mode. Exclude them from mutation
    // analysis so the report reflects only actually-testable code.
    args(
        "--reportDir", layout.buildDirectory.dir("reports/pitest").get().asFile.absolutePath,
        "--targetClasses", "org.yuvViewer.utils.*",
        "--excludedClasses", "*Test,*Tests",
        "--targetTests", "org.yuvViewer.*",
        "--sourceDirs", "src/main/java",
        "--outputFormats", "HTML,XML",
        "--timestampedReports", "false",
        "--verbose", "false"
    )
}
kotlin {
    jvmToolchain(25)
}