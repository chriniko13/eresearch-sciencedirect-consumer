package com.eresearch.elsevier.sciencedirect.consumer.core;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileSupport {

    public static String getResource(String name) {
        try {
            URI uri = FileSupport.class.getClassLoader().getResource(name).toURI();
            Path path = Paths.get(uri);
            return Files.lines(path, Charset.forName("UTF-8")).collect(Collectors.joining());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
