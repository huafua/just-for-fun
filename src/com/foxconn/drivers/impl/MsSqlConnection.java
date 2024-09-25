package com.foxconn.drivers.impl;

public class MsSqlConnection implements Connection{

    @Override
    public String query() {
        return "This is MsSqlConnection created by MsSqlDriver";
    }
    
}
