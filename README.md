# Reconstruct

[![License](https://lxgaming.github.io/badges/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
![Maven Central](https://img.shields.io/maven-central/v/io.github.lxgaming/reconstruct-common)

## Usage
```
// Usage
java -jar <Reconstruct File Name> -jar <Obfuscated jar> -mapping <ProGuard mappings> -output <Deobfuscated jar file> -exclude <Excluded Packages>

// Example - Client
java -jar reconstruct-cli.jar -jar client.jar -mapping client.txt -output client-deobf.jar

// Example - Server
java -jar reconstruct-cli.jar -jar server.jar -mapping server.txt -output server-deobf.jar -exclude "com.google.,com.mojang.,io.netty.,it.unimi.dsi.fastutil.,javax.,joptsimple.,org.apache."
```

## Library
Repository: `mavenCentral()`
<br>
Dependency: `io.github.lxgaming:reconstruct-common:VERSION`

## License
Reconstruct is licensed under the [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0) license.