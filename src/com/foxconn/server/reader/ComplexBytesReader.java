package com.foxconn.server.reader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class ComplexBytesReader implements BytesReader {
    private final static int BUFFER_SIZE = 1024;

    @Override
    public byte[] read(String filename) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                InputStream inputStream = new FileInputStream(filename)) {
            int length = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
                buffer = new byte[BUFFER_SIZE];
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }   
    }
}
