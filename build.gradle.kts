plugins {
	java
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "7.0.2"
	id("checkstyle")
	id("jacoco")
}

group = "edu.aseca"
version = "0.1.2"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

	compileOnly("org.projectlombok:lombok")
	testRuntimeOnly("com.h2database:h2")
	implementation("org.postgresql:postgresql")

	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:postgresql:1.19.1")
	testImplementation("org.testcontainers:junit-jupiter:1.19.1")
}

tasks.register("printVersion") {
	doLast {
		println(project.version)
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

jacoco {
	toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)

	reports {
		xml.required.set(true)
		html.required.set(true)
		html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
	}

	doLast {
		println("HTML report generated: ${reports.html.outputLocation.get().asFile}/index.html")
	}
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				counter = "LINE"
				minimum = "0.80".toBigDecimal()
			}
		}
	}
}

tasks.check {
	dependsOn(tasks.jacocoTestCoverageVerification)
}

spotless {
	java {
		target(
				fileTree(".") {
					include("**/*.java")
					exclude("**/build/**", "**/build-*/**")
				}
		)
		eclipse()
	}
}

checkstyle {
	toolVersion = "10.3.3"
	configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
}

tasks.withType<Checkstyle>().configureEach {
	ignoreFailures = false
	maxWarnings = 0
}