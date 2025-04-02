package com.bpc.service;

import com.bpc.model.Appointment;
import com.bpc.model.Patient;
import com.bpc.model.Physiotherapist;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClinicSystem {
    private List<Physiotherapist> physiotherapists;
    private List<Patient> patients;
    private Timetable timetable;
    private LocalDateTime currentDateTime;

    public ClinicSystem(LocalDateTime currentDateTime) {
        this.physiotherapists = new ArrayList<>();
        this.patients = new ArrayList<>();
        this.timetable = new Timetable();
        this.currentDateTime = currentDateTime;
    }

    public void addPhysiotherapist(Physiotherapist physio) {
        if (!physiotherapists.stream().anyMatch(p -> p.getId().equals(physio.getId()))) {
            physiotherapists.add(physio);
        }
    }

    public void addPatient(Patient patient) {
        if (!patients.stream().anyMatch(p -> p.getId().equals(patient.getId()))) {
            patients.add(patient);
        } else {
            throw new IllegalArgumentException("Patient ID " + patient.getId() + " already exists.");
        }
    }

    public void removePatient(String patientId) {
        patients.removeIf(p -> p.getId().equals(patientId));
    }

    public Appointment bookByExpertise(String expertise, String appointmentId, Patient patient) {
        List<Appointment> slots = timetable.getAvailableSlotsByExpertise(expertise);
        Appointment slot = slots.stream()
                .filter(s -> s.getId().equals(appointmentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Appointment " + appointmentId + " not found for expertise " + expertise + "."));
        slot.book(patient, currentDateTime);
        return slot;
    }

    public Appointment bookByPhysiotherapist(String physioId, String appointmentId, Patient patient) {
        List<Appointment> slots = timetable.getAvailableSlotsByPhysiotherapist(physioId);
        Appointment slot = slots.stream()
                .filter(s -> s.getId().equals(appointmentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Appointment " + appointmentId + " not found or not available for physiotherapist " + physioId + "."));
        if (!slot.getPhysiotherapist().getId().equals(physioId)) {
            throw new IllegalArgumentException("Appointment " + appointmentId + " does not belong to physiotherapist " + physioId + ".");
        }
        slot.book(patient, currentDateTime);
        return slot;
    }

    public void cancelAppointment(String appointmentId, Patient patient) {
        Appointment slot = timetable.findById(appointmentId);
        if (slot == null || (slot.getPatient() != null && !slot.getPatient().getId().equals(patient.getId()))) {
            throw new IllegalArgumentException("Appointment " + appointmentId + " not found or not booked by patient " + patient.getId() + ".");
        }
        slot.cancel();
    }

    public void rescheduleAppointment(String oldAppointmentId, String newAppointmentId, Patient patient) {
        Appointment oldSlot = timetable.findById(oldAppointmentId);
        if (oldSlot == null || (oldSlot.getPatient() != null && !oldSlot.getPatient().getId().equals(patient.getId()))) {
            throw new IllegalArgumentException("Appointment " + oldAppointmentId + " not found or not booked by patient " + patient.getId() + ".");
        }
        if (!oldSlot.getStatus().equals("booked")) {
            throw new IllegalStateException("Only booked appointments can be rescheduled.");
        }

        Appointment newSlot = timetable.findById(newAppointmentId);
        if (newSlot == null) {
            throw new IllegalArgumentException("New appointment " + newAppointmentId + " not found.");
        }
        if (!newSlot.getStatus().equals("available")) {
            throw new IllegalStateException("New appointment " + newAppointmentId + " is not available.");
        }
        if (!newSlot.getTreatment().getExpertiseRequired().equals(oldSlot.getTreatment().getExpertiseRequired())) {
            throw new IllegalArgumentException("New appointment must have the same expertise as the old one.");
        }

        oldSlot.cancel();
        oldSlot.resetForReschedule();
        newSlot.book(patient, currentDateTime);
    }

    // New method to get a patient's upcoming appointments
    public List<Appointment> getPatientAppointments(String patientId) {
        return timetable.getAllAppointments().stream()
                .filter(a -> a.getPatient() != null && a.getPatient().getId().equals(patientId))
                .filter(a -> a.getStatus().equals("booked") || a.getStatus().equals("attended"))
                .sorted((a1, a2) -> a1.getStartTime().compareTo(a2.getStartTime()))
                .collect(Collectors.toList());
    }

    public Timetable getTimetable() { return timetable; }
    public List<Physiotherapist> getPhysiotherapists() { return physiotherapists; }
    public List<Patient> getPatients() { return patients; }
}