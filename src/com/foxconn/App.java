package com.foxconn;

import com.foxconn.server.StaticServer;
import com.foxconn.server.reader.ComplexBytesReader;

public class App {

    private static void startServer() {
        new StaticServer.ServerBuilder()
                .baseDir("public")
                .reader(ComplexBytesReader.class)
                .build()
                .listen(9087);
    }

    public static void main(String[] args) {       
        startServer();       
    }
}
