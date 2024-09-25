package com.foxconn.server.reader;

import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleBytesReader implements BytesReader {

    @Override
    public byte[] read(String filepath) {
        try {
            return Files.readAllBytes(Paths.get(filepath));
        } catch (Exception e) {
            return "<h1>Not found</h1>".getBytes();
        }
    }

}
