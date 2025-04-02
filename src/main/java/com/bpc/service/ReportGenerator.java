package com.bpc.service;

import com.bpc.model.Appointment;
import com.bpc.model.Physiotherapist;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ReportGenerator {
    private ClinicSystem clinicSystem;

    public ReportGenerator(ClinicSystem clinicSystem) {
        this.clinicSystem = clinicSystem;
    }

    public void generateReport() {
        System.out.println("=== BPC Appointment Report ===");
        List<Appointment> appointments = clinicSystem.getTimetable().getAllAppointments();

        Map<Physiotherapist, List<Appointment>> byPhysio = new TreeMap<>(
                Comparator.comparing(Physiotherapist::getName)
        );
        byPhysio.putAll(appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getPhysiotherapist)));

        byPhysio.forEach((physio, appts) -> {
            System.out.println("\nPhysiotherapist: " + physio.getName());
            appts.forEach(a -> System.out.println(
                    "ID: " + a.getId() +
                            ", Treatment: " + a.getTreatment().getName() +
                            ", Patient: " + (a.getPatient() != null ? a.getPatient().getName() : "N/A") +
                            ", Time: " + a.getStartTime() +
                            ", Status: " + a.getStatus()));
        });

        System.out.println("\n=== Physiotherapists by Attended Appointments (Descending) ===");
        clinicSystem.getPhysiotherapists().stream()
                .sorted(Comparator.comparingLong(p -> -appointments.stream()
                        .filter(a -> a.getPhysiotherapist().equals(p) && a.getStatus().equals("attended"))
                        .count()))
                .forEach(p -> System.out.println(p.getName() + ": " +
                        appointments.stream()
                                .filter(a -> a.getPhysiotherapist().equals(p) && a.getStatus().equals("attended"))
                                .count() + " attended"));

        System.out.println("\n=== Appointment Status Summary ===");
        long total = appointments.size();
        long available = appointments.stream().filter(a -> a.getStatus().equals("available")).count();
        long booked = appointments.stream().filter(a -> a.getStatus().equals("booked")).count();
        long attended = appointments.stream().filter(a -> a.getStatus().equals("attended")).count();
        long cancelled = appointments.stream().filter(a -> a.getStatus().equals("cancelled")).count();
        System.out.println("Total Appointments: " + total);
        System.out.println("Available: " + available);
        System.out.println("Booked: " + booked);
        System.out.println("Attended: " + attended);
        System.out.println("Cancelled: " + cancelled);
        // Verify counts
        if (available + booked + attended + cancelled != total) {
            System.out.println("Warning: Status counts do not add up to total appointments!");
        }
    }
}