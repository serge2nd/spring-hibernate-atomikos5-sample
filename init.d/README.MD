### Project initialization scripts
This folder contains Gradle [init scripts][00] for the project.

You should copy the scripts to the `$GRADLE_USER_HOME/init.d/` directory
or manually add `-I init.d/0.gradle -I init.d/1.gradle ...` to each Gradle command.

See the information about each script goals in that script itself:
- [`00-buildscript.gradle`][000]

[00]: https://docs.gradle.org/current/userguide/init_scripts.html
[000]: 00-buildscript.gradle
