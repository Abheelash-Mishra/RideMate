package org.example.services.admin.impl;

import org.example.database.Database;
import org.example.services.admin.AdminServiceInterface;

public class AdminServiceRestImpl implements AdminServiceInterface {
    private final Database db;

    public AdminServiceRestImpl(Database db) {
        this.db = db;
    }

    @Override
    public void removeDriver(String driverID) {
        System.out.println("REMOVING DRIVER_" + driverID);
    }

    @Override
    public void listNDriverDetails(int N) {
        System.out.println("LISTING DRIVERS");
    }

    @Override
    public void getDriverEarnings(String driverID) {
        System.out.println("GETTING DRIVER EARNINGS");
    }
}
