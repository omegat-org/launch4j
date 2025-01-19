# The Launch4j (forked) project

Cross-platform Java executable wrapper for creating lightweight Windows native EXEs. Provides advanced JRE search, application startup configuration and better user experience.

This is the forked version that has changes;

- Migrate to a Gradle build system
- Drop files not published to Maven Central
- Dependencies updates
- Tweak for minor code

## Features

- Launch4j wraps jars in Windows native executables and allows to run them like a regular Windows program.
- It's possible to wrap applications on Windows, Linux and Mac OS X.
-   Also creates launchers for jars and class files without wrapping
- Supports executable jars and dynamic classpath resolution using environment variables and wildcards.
- Doesn't extract the jar from the executable.
- Native pre-JRE splash screen in BMP format shown until the Java application starts.
- Initial priority and single application instance features.
- Works with a bundled JRE or searches for newest Sun or IBM JRE / JDK in given version range and type (64-bit or 32-bit).
- Supports GUI and console apps.
- Supports Windows application manifests.
- Allows setting the initial/max heap size also dynamically in percent of free memory.
- JVM options: set system properties, tweak the garbage collection...
- Runtime JVM options from an .l4j.ini file.
- Runtime command line switches to change the compiled options.
- Access to environment variables, the registry and executable file path through system properties.
- Set environment variables.
- Ability to restart the application based on exit code.
- Custom version information shown by Windows Explorer.
- Digital signing of the executable with sign4j.
- Supports Windows Security Features of the Windows 8 certification kit.
- GUI and command line interface.
- Build integration through an Ant task and a Maven Plugin.
- Lightweight: 35 KB!
- The wrapped program works on Windows only, but Launch4j works on Windows, Linux and Mac OS X.

## Maven Central

- group id: "org.omegat"
- artivact id: "launch4j"
- flavors: "core", "workdir-linux", "workdir-linux-x64", "workdir-linux-aarch64", "workdir-macosx-x86", "workdir-win32"

## Copyright and License

Copyright (c) 2004, 2021 Grzegorz Kowal
Copyright (c) 2025 Hiroshi Miura
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
   this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
   may be used to endorse or promote products derived from this software without
   specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.