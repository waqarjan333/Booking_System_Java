package com.bpc.model;

import java.time.LocalDateTime;

public class Appointment {
    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Physiotherapist physiotherapist;
    private Patient patient;
    private Treatment treatment;
    private String status;

    public Appointment(String id, LocalDateTime startTime, Physiotherapist physiotherapist, Treatment treatment) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = startTime.plusHours(1);
        this.physiotherapist = physiotherapist;
        this.treatment = treatment;
        this.status = "available";
    }

    public void book(Patient patient, LocalDateTime currentDateTime) {
        if (startTime.isBefore(currentDateTime)) {
            throw new IllegalStateException("Cannot book an appointment in the past. Appointment time: " + startTime);
        }
        if (status.equals("available")) {
            this.patient = patient;
            this.status = "booked";
        } else {
            throw new IllegalStateException("Appointment is not available.");
        }
    }

    public void cancel() {
        if (status.equals("booked")) {
            this.patient = null;
            this.status = "cancelled";
        } else {
            throw new IllegalStateException("Only booked appointments can be cancelled.");
        }
    }

    // New method to reset the appointment to "available" after rescheduling
    public void resetForReschedule() {
        if (status.equals("cancelled") || status.equals("booked")) {
            this.patient = null;
            this.status = "available";
        } else {
            throw new IllegalStateException("Appointment cannot be reset to available.");
        }
    }

    public void attend() {
        if (status.equals("booked")) {
            this.status = "attended";
        } else {
            throw new IllegalStateException("Only booked appointments can be attended.");
        }
    }

    public String getId() { return id; }
    public LocalDateTime getStartTime() { return startTime; }
    public Physiotherapist getPhysiotherapist() { return physiotherapist; }
    public Patient getPatient() { return patient; }
    public Treatment getTreatment() { return treatment; }
    public String getStatus() { return status; }
}