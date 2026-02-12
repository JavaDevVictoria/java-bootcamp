plugins {
	java
	application
	id("org.springframework.boot") version "4.0.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.wcc.bootcamp.java"
version = "0.0.1-SNAPSHOT"
description = "Java Bootcamp "

application {
	mainClass.set("com.wcc.bootcamp.java.mentorship.MentorshipMatcherApp")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
	sourceSets {
		main {
			java {
				srcDirs("src/main/java", "participants/victoria/project/src/main/java")
			}
		}
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
