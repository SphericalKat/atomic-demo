import java.net.URI

plugins {
    id("java-library")
    id ("maven-publish")
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

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}