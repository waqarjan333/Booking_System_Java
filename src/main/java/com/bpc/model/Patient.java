package com.bpc.model;

public class Patient {
    private String id;
    private String name;
    private String address;
    private String phone;

    public Patient(String id, String name, String address, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}