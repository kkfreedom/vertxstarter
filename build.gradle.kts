import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "top.leostudio"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenLocal()
  mavenCentral()
}

val vertxVersion = "4.2.1"
val junitJupiterVersion = "5.8.2"

val mainVerticleName = "top.leostudio.vertxstarter.GraphQLVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web-client:4.2.1")
  implementation("io.vertx:vertx-web:4.2.1")
  implementation("io.vertx:vertx-mysql-client:4.2.1")
  implementation("io.vertx:vertx-web-graphql:${vertxVersion}")
  testImplementation("io.vertx:vertx-junit5:4.2.1")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

//java {
//  sourceCompatibility = JavaVersion.VERSION_11
//  targetCompatibility = JavaVersion.VERSION_11
//}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
