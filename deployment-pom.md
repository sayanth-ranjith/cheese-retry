## Deployment pom for future reference

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>4.0.0</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>io.github.sayanth-ranjith</groupId>
	<artifactId>cheese-retry-core</artifactId>
	<version>0.0.12</version>
	<name>Cheese Retry Core</name>
	<description>
		Spring Boot AOP-based retry starter with pluggable strategies and annotations.
	</description>

	<url>https://github.com/sayanth-ranjith/cheese-retry</url>
	<licenses>
		<license>
			<name>Apache License, Version 2.0</name>
			<url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>
		</license>
	</licenses>
	<developers>
		<developer>
			<id>sayanth-ranjith</id>
			<name>Sayanth Ranjith</name>
		</developer>
	</developers>
	<scm>
		<connection>scm:git:https://github.com/sayanth-ranjith/cheese-retry.git</connection>
		<developerConnection>scm:git:ssh://github.com:sayanth-ranjith/cheese-retry.git</developerConnection>
		<url>https://github.com/sayanth-ranjith/cheese-retry</url>
	</scm>
	<distributionManagement>
		<repository>
			<id>github</id>
			<name>GitHub Packages</name>
			<url>https://maven.pkg.github.com/sayanth-ranjith/cheese-retry</url>
		</repository>
	</distributionManagement>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-aspectj</artifactId>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<source>21</source>
					<target>21</target>
					<annotationProcessorPaths>
						<path>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
							<version>1.18.30</version>
						</path>
					</annotationProcessorPaths>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-source-plugin</artifactId>
				<version>3.3.1</version>
				<executions>
					<execution>
						<id>attach-sources</id>
						<phase>package</phase>
						<goals>
							<goal>jar-no-fork</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-javadoc-plugin</artifactId>
				<version>3.12.0</version>
				<configuration>
					<author>true</author>
					<quiet>true</quiet>
					<doclint>none</doclint>
				</configuration>
				<executions>
					<execution>
						<id>attach-javadocs</id>
						<phase>package</phase>
						<goals>
							<goal>jar</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>

</project>


```
