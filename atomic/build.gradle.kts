plugins {
    id("java-library")
    id ("maven-publish")
    kotlin("jvm") version "1.7.22"
    id("com.squareup.wire") version ("4.6.0")
}

group = "jp.co.goalist"
version = project.version

publishing {
    publications {
        create<MavenPublication>("atomic") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/SphericalKat/atomic-demo")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

repositories {
    mavenCentral()
}


wire {
   kotlin {
       boxOneOfsMinSize = 10
   }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(kotlin("stdlib-jdk8"))

    // AMQP client
    implementation("com.rabbitmq:amqp-client:5.17.0")

    // Logging
    implementation("org.apache.logging.log4j:log4j-api:2.7")
    implementation("org.apache.logging.log4j:log4j-core:2.12.4")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.7")

    // jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")

    // protobuf
    implementation("com.google.protobuf:protobuf-kotlin:3.22.4")

}

tasks.test {
    useJUnitPlatform()
}


kotlin {
    jvmToolchain(17)
}