package com.foxconn.drivers.impl;

import com.foxconn.drivers.Driver;

public class MySqlDriver implements Driver {
    @Override
    public Connection getConnection(String protocol) {
        if (protocol.startsWith("mysql")) {
            return new MySqlConnection();
        }
        throw new RuntimeException("Not adaptive to mysql");
    }

    static {
        DriverManager.register(new MySqlDriver());
    }
}