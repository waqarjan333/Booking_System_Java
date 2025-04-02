package com.bpc.model;

import java.util.ArrayList;
import java.util.List;

public class Physiotherapist {
    private String id;
    private String name;
    private String address;
    private String phone;
    private List<String> expertise;
    private List<Treatment> treatments;

    public Physiotherapist(String id, String name, String address, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.expertise = new ArrayList<>();
        this.treatments = new ArrayList<>();
    }

    public void addExpertise(String expertise) {
        if (!this.expertise.contains(expertise)) this.expertise.add(expertise);
    }

    public void addTreatment(Treatment treatment) {
        if (expertise.contains(treatment.getExpertiseRequired())) treatments.add(treatment);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getExpertise() { return expertise; }
    public List<Treatment> getTreatments() { return treatments; }
}