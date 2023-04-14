# JWebAssembly Maven Plugin
Maven Plugin (i.e., Mojo) for the [JWebAssembly compiler](https://github.com/i-net-software/JWebAssembly). Retrieves the
compiler from Maven (Central or local cache), and executes the compiler.

```mermaid
sequenceDiagram
    Plugin ->> Maven Project: get JWebAssembly compiler version
    Maven Project ->> Plugin: 
    
    Plugin ->> Maven Cache/Central: get JWebAssembly compiler artifact
    Maven Cache/Central ->> Plugin: 
    
    Plugin ->> Maven Project: get code and dependencies
    Maven Project ->> Plugin: 
   
   Plugin ->> Maven Project: get properties for JWebAssembly compiler
   Maven Project ->> Plugin: 
   
   Plugin ->> JWebAssembly Compiler: pass code, dependencies and properties
   JWebAssembly Compiler ->> Plugin: 
   
   Plugin ->> JWebAssembly Compiler: Execute JWebAssembly compiler
   JWebAssembly Compiler ->> Plugin: 
```

## Usage
The ```<configuration>``` tag can be omitted as the plugin provides defaults for all configuration options. The _goal_
to configure is _compile_.
```xml
<build>
    <plugins>
        <plugin>
            <groupId>io.schram.webassembly.maven</groupId>
            <artifactId>jwebassembly-maven</artifactId>
            <executions>
                <execution>
                    <id>java-to-wasm</id>
                    <goals>
                        <goal>compile</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Output format
By default, the output format of the compiler is binary, but the compiler can also be configured to output
_WebAssembly Text (WAT)_ format.
```xml
<configuration>
    <format>text</format>
</configuration>
```

### Compiler version
By default, the plugin will use version 0.4 of the [JWebAssembly compiler](https://github.com/i-net-software/JWebAssembly)
, but this can be overridden.
```xml
<configuration>
    <compiler>
        <version>0.3</version>
    </compiler>
</configuration>
```

### Properties
Properties can be passed to the [JWebAssembly compiler](https://github.com/i-net-software/JWebAssembly)
```xml
<configuration>
    <properties>
        <IgnoreNative>true</IgnoreNative>
    </properties>
</configuration>
```