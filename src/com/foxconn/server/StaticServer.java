package com.foxconn.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.foxconn.drivers.impl.DriverManager;
import com.foxconn.server.reader.BytesReader;
import com.foxconn.server.reader.SimpleBytesReader;

public class StaticServer {
    private String baseDir = ".";
    private final ExecutorService service = Executors.newFixedThreadPool(10);

    private static Map<String, String> MIME_TYPES = new HashMap<String, String>();
    private BytesReader reader = new SimpleBytesReader();

    static {
        MIME_TYPES.put(".html", "text/html;charset=utf-8");
        MIME_TYPES.put(".css", "text/css;charset=utf-8");
        MIME_TYPES.put(".js", "application/javascript;charset=utf-8");
        MIME_TYPES.put(".json", "application/json;charset=utf-8");
    }

    public StaticServer() {
    }

    public StaticServer(String baseDir) {
        if (!Paths.get(baseDir).toFile().exists()) {
            throw new IllegalArgumentException(String.format("Basedir '%s' can't be found", this.baseDir));
        }
        this.baseDir = baseDir;
    }

    private String guessContentType(String filename) {
        String defaultType = "application/octet-stream";
        int index = filename.lastIndexOf(".");
        if (index < 0)
            return "text/html;charset=utf-8";
        String extname = filename.substring(index);
        String type = MIME_TYPES.get(extname);
        return type != null ? type : defaultType;
    }

    private byte[] readFile(String filename) {
        try {
            if ("/".equals(filename) || filename.isEmpty()) {
                return DriverManager.getConnection("mssql").query().getBytes();
            }
            Path filepath = Paths.get(this.baseDir, filename);
            return reader.read(filepath.toString());
        } catch (Exception e) {
            return "<h1>Not found</h1>".getBytes();
        }
    }

    public StaticServer setReader(BytesReader reader) {
        if (reader != null) {
            this.reader = reader;
        }
        return this;
    }

    public <T extends BytesReader> StaticServer setReader(Class<T> clazz) {
        if (clazz == null) {
            return this;
        }
        try {
            this.reader = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return this;
    }

    private Runnable handle(Socket socket) {
        return () -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    OutputStream writer = socket.getOutputStream()) {
                String filename = "/";
                String firstLine = reader.readLine();
                // GET /index.html HTTP/1.1
                String[] parts = firstLine.split(" ");
                if (parts.length > 2) {
                    filename = parts[1].substring(1);
                }
                File fileAbsolutePath = new File(this.baseDir, filename);
                if (fileAbsolutePath.exists() || "/".equals(filename)) {
                    writer.write("HTTP/1.1 200 OK\r\n".getBytes());
                } else {
                    writer.write("HTTP/1.1 404 notfound\r\n".getBytes());
                }
                writer.write(String.format("Content-type:%s\r\n", this.guessContentType(filename)).getBytes());
                writer.write("hello:world\r\n\r\n".getBytes());
                writer.write(StaticServer.this.readFile(filename));
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public void listen(int port) {
        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server running at " + port);
                while (true) {
                    Socket socket = serverSocket.accept();
                    this.service.submit(this.handle(socket));
                }
            } catch (BindException e) {
                port = new Random().nextInt(4444) + 5556;
            } catch (Exception e) {
                break;

            }
        }
    }

    public static class ServerBuilder {
        private String baseDir = ".";
        private Class<? extends BytesReader> clazz = SimpleBytesReader.class;

        public ServerBuilder baseDir(String basedir) {
            this.baseDir = basedir;
            return this;
        }

        public ServerBuilder reader(Class<? extends BytesReader> readerClazz) {
            this.clazz = readerClazz;
            return this;
        }

        public StaticServer build() {
            return new StaticServer(this.baseDir).setReader(this.clazz);
        }

    }
}