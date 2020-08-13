# Reconstruct

[![Build Status](https://api.travis-ci.com/LXGaming/Reconstruct.svg?branch=master)](https://travis-ci.com/LXGaming/Reconstruct)
[![License](https://lxgaming.github.io/badges/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## Usage
```
// Usage
java -jar <Reconstruct File Name> -jar <Obfuscated jar> -mapping <ProGuard mappings> -output <Deobfuscated jar file> -exclude <Excluded Packages>

// Example - Client
java -jar Reconstruct.jar -jar client.jar -mapping client.txt -output client-deobf.jar

// Example - Server
java -jar Reconstruct.jar -jar server.jar -mapping server.txt -output server-deobf.jar -exclude "com.google.,io.netty.,it.unimi.dsi.fastutil.,javax.,joptsimple.,org.apache."
```

## License
Reconstruct is licensed under the [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0) license.