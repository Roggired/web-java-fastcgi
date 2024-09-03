package ru.itmo.se.web.fastcgi;

import java.util.Arrays;
import java.util.Properties;

public class SimpleFormUrlencodedParser {
    public Properties parseInput(String input) {
        var props = new Properties();
        Arrays.stream(input.split("&"))
                .forEach(keyValue -> props.setProperty(keyValue.split("=")[0], keyValue.split("=")[1]));
        return props;
    }
}
