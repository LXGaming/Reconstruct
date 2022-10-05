# Reconstruct

[![License](https://img.shields.io/github/license/LXGaming/Reconstruct?label=License&cacheSeconds=86400)](https://github.com/LXGaming/Reconstruct/blob/main/LICENSE)
![Maven Central](https://img.shields.io/maven-central/v/io.github.lxgaming/reconstruct-common?label=Maven%20Central)

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
Reconstruct is licensed under the [Apache 2.0](https://github.com/LXGaming/Reconstruct/blob/main/LICENSE) license.