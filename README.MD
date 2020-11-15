# spring-hibernate-atomikos5-sample [![Build Status](https://travis-ci.org/serge2nd/spring-hibernate-atomikos5-sample.svg?branch=master)](https://travis-ci.org/serge2nd/spring-hibernate-atomikos5-sample)
A demo Spring configuration consisting of the following parts:
- A simple JPA persistence unit with Hibernate ORM/Validator
- JTA transaction management (Atomikos)

## Requirements
- JDK >=11 to build, JRE >=11 to run

## Build & Run
**Note:** see the information in the [`init.d/README.MD`][100] before executing any Gradle tasks.

Run the following to build the fat JAR (with dependencies):
```shell script
./gradlew clean build -x test
```
Remove the last option if the tests must be run during the build.

Add `testJar` to the end if you also want to build the tests JAR.

The default tests (on in-memory db) or any custom tests can be run by executing something like
```shell script
java --enable-preview -cp fat.jar:tests.jar org.junit.platform.console.ConsoleLauncher -p ru.serge2nd
```
Run the `org.junit.platform.console.ConsoleLauncher` without arguments to see the available options.

[100]: init.d/README.MD
