import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

val projectVersion = "0.1"
val projectName = "Zhe Parser"
val applicationMainClassName = "zhe.ParSy.AppKt"

repositories {
    jcenter()
}

plugins {
    kotlin("jvm") version "1.7.10"

    // Apply the application plugin to add support for building a CLI
    // application.
    application

    // This pluging allows greater flexibility in customizing test builds.
    id("org.unbroken-dome.test-sets") version "4.0.0"
}

val fatJar = task("fatJar", type=Jar::class) {
    description = "Creates a self-contained fat JAR of the application "+
                  "that can be run."
    manifest {
        attributes["Implementation-Title"] = projectName
	attributes["Implementation-Version"] = projectVersion
        attributes["Main-Class"] = applicationMainClassName
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val dependencies = configurations
	.runtimeClasspath
	.get()
	.map(::zipTree)
    from(dependencies)
    with(tasks.jar.get())
}

tasks {
    "build" {
	dependsOn(fatJar)
    }
    "distTar" {
	dependsOn(fatJar)
    }
    "distZip" {
	dependsOn(fatJar)
    }
    "startScripts" {
	dependsOn(fatJar)
    }
}

application {
    mainClassName = "zhe.ParSy.AppKt"
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
}

tasks.test {
    useJUnitPlatform() // JUnit5

    testLogging {
        lifecycle {
            events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
            showStandardStreams = true
        }
        info.events = lifecycle.events
        info.exceptionFormat = lifecycle.exceptionFormat
    }

    val failedTests = mutableListOf<TestDescriptor>()
    val skippedTests = mutableListOf<TestDescriptor>()

    // See https://github.com/gradle/kotlin-dsl/issues/836
    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
            when (result.resultType) {
                TestResult.ResultType.FAILURE -> failedTests.add(testDescriptor)
                TestResult.ResultType.SKIPPED -> skippedTests.add(testDescriptor)
                else -> Unit
            }
        }

        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) { // root suite
                logger.lifecycle("----")
                logger.lifecycle("Test result: ${result.resultType}")
                logger.lifecycle(
                        "Test summary: ${result.testCount} tests, " +
                        "${result.successfulTestCount} succeeded, " +
                        "${result.failedTestCount} failed, " +
                        "${result.skippedTestCount} skipped")
                failedTests.takeIf { it.isNotEmpty() }?.prefixedSummary("\tFailed Tests")
                skippedTests.takeIf { it.isNotEmpty() }?.prefixedSummary("\tSkipped Tests:")
            }
        }

        private infix fun List<TestDescriptor>.prefixedSummary(subject: String) {
                logger.lifecycle(subject)
                forEach { test -> logger.lifecycle("\t\t${test.displayName()}") }
        }

        private fun TestDescriptor.displayName() = parent?.let { "${it.name} - $name" } ?: "$name"
    })
}
