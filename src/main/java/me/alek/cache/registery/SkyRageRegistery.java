package me.alek.cache.registery;

import me.alek.cache.Container;
import me.alek.cache.Registery;

import java.util.Arrays;
import java.util.List;

public class SkyRageRegistery extends Registery<String> {

    public SkyRageRegistery(Container<String> container) {
        super(container);
    }
    @Override
    public List<String> getElements() {
        return Arrays.asList(
                "META-INF/gradle/org/apache/commons/local-info.hdm",
                "META-INF/gradle/io/netty/netty-locals.netd",
                "META-INF/maven/org/apache/logging/log4j/Log4j-events.dtd",
                "META-INF/gradle/org/apache/logging/log4j/Log4j-events.dtd",
                "META-INF/gradle/org.json/json/json.xsd",
                "META-INF/maven/org/apache/commons/api-catch.dir",
                "META-INF/maven/org/apache/commons/local-dir.hum",
                "META-INF/maven/org/apache/commons/local-info.hdm",
                "META-INF/maven/com/google/code/gson/gson/maven.data",
                "META-INF/gradle/com.google.code.gson/gson/maven.data",
                "META-INF/maven/org.json/json/gson.xsd"
        );
    }
}
