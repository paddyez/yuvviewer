plugins {
    java
    jacoco
    id("org.sonarqube") version "3.3"
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
tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "org.yuvViewer.Main"
        )
    }
}
tasks.test {
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
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    implementation("org.apache.logging.log4j:log4j-api:2.26.0")
    implementation("org.apache.logging.log4j:log4j-core:2.26.0")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    pitestClasspath("org.pitest:pitest-command-line:1.25.5")
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