package com.foxconn.drivers;

import com.foxconn.drivers.impl.Connection;

public interface Driver {
    Connection getConnection(String protocol);
}