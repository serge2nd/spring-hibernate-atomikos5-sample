### Project initialization scripts
There are Gradle [init scripts][100] for the project in this folder.

You should copy the scripts to the `$GRADLE_USER_HOME/init.d/` directory
or manually add `-I init.d/0.gradle -I init.d/1.gradle ...` to each Gradle command.

See the information about each script goals in that script itself:
- [`00-buildscript.gradle`][200]

[100]: https://docs.gradle.org/current/userguide/init_scripts.html
[200]: 00-buildscript.gradle
