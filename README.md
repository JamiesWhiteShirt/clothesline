# Clothesline
[![Build Status](https://travis-ci.com/JamiesWhiteShirt/clothesline.svg?branch=master)](https://travis-ci.com/JamiesWhiteShirt/clothesline)
[![CurseForge Downloads](http://cf.way2muchnoise.eu/clothesline.svg)](https://minecraft.curseforge.com/projects/clothesline)
[![Maven Repository](https://img.shields.io/maven-metadata/v/https/maven.jamieswhiteshirt.com/libs-release/com/jamieswhiteshirt/clothesline/maven-metadata.xml.svg)](https://maven.jamieswhiteshirt.com/libs-release/com/jamieswhiteshirt/clothesline/)

A seamless laundry experience that is definitely not an item transport mod.

- [clothesline-hooks](https://github.com/JamiesWhiteShirt/clothesline-hooks): Core mod component bundled with Clothesline.
- [rtree-3i-lite](https://github.com/JamiesWhiteShirt/rtree-3i-lite): Spatial indexing library used by Clothesline.

## Developing Clothesline

To get started, refer to the [MinecraftForge documentation](https://mcforge.readthedocs.io/en/latest/gettingstarted/).

## Usage

To use this mod in your workspace, add the following to your `build.gradle`:

```groovy
repositories {
    maven {url "https://oss.sonatype.org/content/repositories/snapshots"}
    maven {url "https://maven.jamieswhiteshirt.com/libs-release/"}
}

dependencies {
    deobfRuntimeOnly "com.jamieswhiteshirt:clothesline-hooks:<CLOTHESLINE_HOOKS_VERSION>"
    deobfCompile "com.jamieswhiteshirt:clothesline:<CLOTHESLINE_VERSION>"
}
```

Clothesline has an API, but it is currently unstable and with limited functionality.
The API is located in the `com.jamieswhiteshirt.clothesline.api` package.

To get started, get the network manager of a world using the `INetworkManager` [capability](https://mcforge.readthedocs.io/en/latest/datastorage/capabilities/).
Example:

```java
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import com.jamieswhiteshirt.clothesline.api.INetworkManager;

class Example {
    @CapabilityInject(INetworkManager.class)
    public static final Capability<INetworkManager> NETWORK_MANAGER_CAPABILITY = null;
    
    void example(World world) {
        INetworkManager manager = world.getCapability(NETWORK_MANAGER_CAPABILITY, null);
        if (manager != null) {
            /* ... */
        }
    }
}
```
