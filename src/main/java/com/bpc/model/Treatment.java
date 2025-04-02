package com.bpc.model;

public class Treatment {
    private String name;
    private String expertiseRequired;

    public Treatment(String name, String expertiseRequired) {
        this.name = name;
        this.expertiseRequired = expertiseRequired;
    }

    public String getName() { return name; }
    public String getExpertiseRequired() { return expertiseRequired; }
}