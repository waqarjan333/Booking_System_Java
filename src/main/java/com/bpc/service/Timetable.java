package com.bpc.service;

import com.bpc.model.Appointment;
import com.bpc.model.Physiotherapist;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Timetable {
    private List<Appointment> slots;

    public Timetable() {
        this.slots = new ArrayList<>();
    }

    public void addSlot(Appointment slot) {
        boolean overlaps = slots.stream()
                .filter(s -> s.getPhysiotherapist().equals(slot.getPhysiotherapist()))
                .anyMatch(s -> s.getStartTime().equals(slot.getStartTime()));
        if (overlaps) {
            throw new IllegalArgumentException("Cannot add slot: Physiotherapist " + slot.getPhysiotherapist().getId() +
                    " already has an appointment at " + slot.getStartTime());
        }
        slots.add(slot);
    }

    public List<Appointment> getAvailableSlotsByExpertise(String expertise) {
        return slots.stream()
                .filter(slot -> slot.getStatus().equals("available"))
                .filter(slot -> slot.getTreatment().getExpertiseRequired().equals(expertise))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAvailableSlotsByPhysiotherapist(String physioId) {
        return slots.stream()
                .filter(slot -> slot.getStatus().equals("available"))
                .filter(slot -> slot.getPhysiotherapist().getId().equals(physioId))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(slots);
    }

    public Appointment findById(String id) {
        return slots.stream()
                .filter(slot -> slot.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}