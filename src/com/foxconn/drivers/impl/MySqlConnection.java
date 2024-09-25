package com.foxconn.drivers.impl;

public class MySqlConnection implements Connection{

    @Override
    public String query() {
        return "This is MySqlConnection created by MySqlDriver";
    }
    
}
