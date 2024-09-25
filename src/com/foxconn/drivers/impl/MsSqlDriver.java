package com.foxconn.drivers.impl;

import com.foxconn.drivers.Driver;

public class MsSqlDriver implements Driver {
    @Override
    public Connection getConnection(String protocol) {
        if (protocol.startsWith("mssql")) {
             return new MsSqlConnection();
        }
        throw new RuntimeException("Not adaptive to mssql");
    }

    static {
        DriverManager.register(new MsSqlDriver());
    }
}