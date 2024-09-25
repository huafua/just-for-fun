package com.foxconn.drivers.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.foxconn.drivers.Driver;
import com.foxconn.drivers.utils.MyServiceLoader;

public class DriverManager {
    private static List<Driver> drivers = new ArrayList<Driver>();

    public static void register(Driver driver) {
        if (driver != null)
            drivers.add(driver);
    }

    public static Connection getConnection(String protocol) {
        for (Driver driver : drivers) {
            try {
                return driver.getConnection(protocol);
            } catch (Exception e) {
                // TODO
            }
        }
        throw new RuntimeException("No driver found for " + protocol);
    }

    private static void initializeDrivers() {
        Iterator<Driver> myServiceLoader = MyServiceLoader.load(Driver.class).iterator();
        while (myServiceLoader.hasNext()) {
            myServiceLoader.next();
        }
    }

    static {
        initializeDrivers();
    }
}