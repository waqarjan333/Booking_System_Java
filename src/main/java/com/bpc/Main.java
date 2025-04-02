package com.bpc;

import com.bpc.model.*;
import com.bpc.service.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final LocalDateTime CURRENT_DATE_TIME = LocalDateTime.of(2025, 3, 23, 0, 0);
    private static final ClinicSystem clinic = new ClinicSystem(CURRENT_DATE_TIME);
    private static final ReportGenerator reportGenerator = new ReportGenerator(clinic);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Boost Physio Clinic Booking System!");
        System.out.print("Choose mode (1: Self-Run Demo, 2: Menu-Based): ");
        int choice = scanner.nextInt();

        if (choice == 1) {
            runSelfDemo();
        } else if (choice == 2) {
            runMenu(scanner);
        } else {
            System.out.println("Invalid choice. Exiting.");
        }
        scanner.close();
    }

    private static void runSelfDemo() {
        System.out.println("=== Starting Self-Run Demo ===");
        initializeData();

        System.out.println("\n1. Adding Patients:");
        System.out.println("Added 10 patients.");

        System.out.println("\n2. Available Slots by Expertise (Physiotherapy):");
        clinic.getTimetable().getAvailableSlotsByExpertise("Physiotherapy").forEach(a ->
                System.out.println("ID: " + a.getId() + ", Time: " + a.getStartTime() + ", Treatment: " + a.getTreatment().getName()));

        System.out.println("\n3. Booking by Expertise:");
        clinic.bookByExpertise("Physiotherapy", "A1", clinic.getPatients().get(0)); // Alice with Dr. Smith
        clinic.bookByExpertise("Physiotherapy", "A4", clinic.getPatients().get(1)); // Bob with Dr. Lee
        clinic.bookByExpertise("Physiotherapy", "A6", clinic.getPatients().get(2)); // Charlie with Dr. Smith
        System.out.println("Booked A1 for Alice, A4 for Bob, and A6 for Charlie.");

        System.out.println("\n4. Available Slots by Physiotherapist (P3):");
        clinic.getTimetable().getAvailableSlotsByPhysiotherapist("P3").forEach(a ->
                System.out.println("ID: " + a.getId() + ", Time: " + a.getStartTime() + ", Treatment: " + a.getTreatment().getName()));

        System.out.println("\n5. Booking by Physiotherapist:");
        clinic.bookByPhysiotherapist("P3", "A8", clinic.getPatients().get(3)); // Diana with Dr. Lee
        clinic.bookByPhysiotherapist("P3", "A12", clinic.getPatients().get(4)); // Eve with Dr. Lee
        clinic.bookByPhysiotherapist("P2", "A3", clinic.getPatients().get(5)); // Frank with Dr. Jones
        clinic.bookByPhysiotherapist("P4", "A18", clinic.getPatients().get(6)); // Grace with Dr. Brown
        System.out.println("Booked A8 for Diana and A12 for Eve with Dr. Lee, A3 for Frank with Dr. Jones, and A18 for Grace with Dr. Brown.");

        System.out.println("\n6. Rescheduling Appointment:");
        clinic.rescheduleAppointment("A12", "A16", clinic.getPatients().get(4)); // Reschedule Eve from A12 to A16
        System.out.println("Rescheduled Eve's appointment from A12 to A16 with Dr. Lee.");

        System.out.println("\n7. Marking Appointments as Attended:");
        clinic.getTimetable().findById("A1").attend();
        clinic.getTimetable().findById("A4").attend();
        clinic.getTimetable().findById("A16").attend(); // Eve's rescheduled appointment
        clinic.getTimetable().findById("A3").attend();
        System.out.println("A1, A4, A16, and A3 marked as attended.");

        System.out.println("\n8. Cancelling Appointments:");
        clinic.cancelAppointment("A6", clinic.getPatients().get(2)); // Charlie
        clinic.cancelAppointment("A8", clinic.getPatients().get(3)); // Diana
        clinic.cancelAppointment("A18", clinic.getPatients().get(6)); // Grace
        System.out.println("A6, A8, and A18 cancelled.");

        // New Step: Display Patient Appointments
        System.out.println("\n9. Displaying Patient Appointments:");
        String[] patientIds = {"PT1", "PT2", "PT5", "PT6"}; // Alice, Bob, Eve, Frank
        for (String patientId : patientIds) {
            Patient patient = clinic.getPatients().stream()
                    .filter(p -> p.getId().equals(patientId))
                    .findFirst()
                    .orElse(null);
            if (patient != null) {
                System.out.println("Appointments for " + patient.getName() + " (" + patientId + "):");
                List<Appointment> appointments = clinic.getPatientAppointments(patientId);
                if (appointments.isEmpty()) {
                    System.out.println("No upcoming appointments.");
                } else {
                    appointments.forEach(a ->
                            System.out.println("ID: " + a.getId() + ", Time: " + a.getStartTime() +
                                    ", Treatment: " + a.getTreatment().getName() +
                                    ", Physiotherapist: " + a.getPhysiotherapist().getName() +
                                    ", Status: " + a.getStatus()));
                }
            }
        }

        System.out.println("\n10. Generating Report:");
        reportGenerator.generateReport();

        System.out.println("=== Self-Run Demo Complete ===");
    }

    private static void runMenu(Scanner scanner) {
        initializeData();
        while (true) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. Add Patient");
            System.out.println("2. Remove Patient");
            System.out.println("3. View Available Slots by Expertise");
            System.out.println("4. View Available Slots by Physiotherapist");
            System.out.println("5. Book by Expertise");
            System.out.println("6. Book by Physiotherapist");
            System.out.println("7. Reschedule Appointment");
            System.out.println("8. Cancel Appointment");
            System.out.println("9. Mark as Attended");
            System.out.println("10. View All Patients");
            System.out.println("11. View All Appointments");
            System.out.println("12. Generate Report");
            System.out.println("13. Exit");
            System.out.println("14. View Patient Appointments"); // New option
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter Patient ID: ");
                        String id = scanner.nextLine();
                        if (clinic.getPatients().stream().anyMatch(p -> p.getId().equals(id))) {
                            throw new IllegalArgumentException("Patient ID " + id + " already exists.");
                        }
                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine();
                        if (name.trim().isEmpty()) {
                            throw new IllegalArgumentException("Name cannot be empty.");
                        }
                        System.out.print("Enter Address: ");
                        String address = scanner.nextLine();
                        System.out.print("Enter Phone: ");
                        String phone = scanner.nextLine();
                        clinic.addPatient(new Patient(id, name, address, phone));
                        System.out.println("Patient added.");
                        break;
                    case 2:
                        System.out.print("Enter Patient ID to remove: ");
                        String removeId = scanner.nextLine();
                        if (clinic.getPatients().stream().noneMatch(p -> p.getId().equals(removeId))) {
                            throw new IllegalArgumentException("Patient ID " + removeId + " not found.");
                        }
                        clinic.removePatient(removeId);
                        System.out.println("Patient removed.");
                        break;
                    case 3:
                        System.out.print("Enter Expertise (e.g., Physiotherapy): ");
                        String expertiseView = scanner.nextLine();
                        List<Appointment> expertiseSlotsView = clinic.getTimetable().getAvailableSlotsByExpertise(expertiseView);
                        if (expertiseSlotsView.isEmpty()) {
                            System.out.println("No available slots for expertise: " + expertiseView);
                        } else {
                            expertiseSlotsView.forEach(a ->
                                    System.out.println("ID: " + a.getId() + ", Time: " + a.getStartTime() +
                                            ", Treatment: " + a.getTreatment().getName() +
                                            ", Physiotherapist: " + a.getPhysiotherapist().getName()));
                        }
                        break;
                    case 4:
                        System.out.print("Enter Physiotherapist ID (e.g., P1): ");
                        String physioIdView = scanner.nextLine();
                        if (clinic.getPhysiotherapists().stream().noneMatch(p -> p.getId().equals(physioIdView))) {
                            throw new IllegalArgumentException("Physiotherapist ID " + physioIdView + " not found.");
                        }
                        List<Appointment> physioSlotsView = clinic.getTimetable().getAvailableSlotsByPhysiotherapist(physioIdView);
                        if (physioSlotsView.isEmpty()) {
                            System.out.println("No available slots for physiotherapist: " + physioIdView);
                        } else {
                            physioSlotsView.forEach(a ->
                                    System.out.println("ID: " + a.getId() + ", Time: " + a.getStartTime() +
                                            ", Treatment: " + a.getTreatment().getName()));
                        }
                        break;
                    case 5:
                        System.out.print("Enter Expertise (e.g., Physiotherapy): ");
                        String expertise = scanner.nextLine();
                        List<Appointment> expertiseSlots = clinic.getTimetable().getAvailableSlotsByExpertise(expertise);
                        if (expertiseSlots.isEmpty()) {
                            System.out.println("No available slots for expertise: " + expertise);
                            break;
                        }
                        expertiseSlots.forEach(a ->
                                System.out.println("ID: " + a.getId() + ", Time: " + a.getStartTime() +
                                        ", Treatment: " + a.getTreatment().getName() +
                                        ", Physiotherapist: " + a.getPhysiotherapist().getName()));
                        System.out.print("Enter Appointment ID: ");
                        String apptId1 = scanner.nextLine();
                        if (expertiseSlots.stream().noneMatch(a -> a.getId().equals(apptId1))) {
                            throw new IllegalArgumentException("Appointment ID " + apptId1 + " not found or not available for expertise " + expertise + ".");
                        }
                        System.out.print("Enter Patient ID: ");
                        String patientId1 = scanner.nextLine();
                        Patient patient1 = clinic.getPatients().stream()
                                .filter(p -> p.getId().equals(patientId1))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Patient ID " + patientId1 + " not found."));
                        clinic.bookByExpertise(expertise, apptId1, patient1);
                        System.out.println("Appointment booked.");
                        break;
                    case 6:
                        System.out.print("Enter Physiotherapist ID (e.g., P1): ");
                        String physioId = scanner.nextLine();
                        if (clinic.getPhysiotherapists().stream().noneMatch(p -> p.getId().equals(physioId))) {
                            throw new IllegalArgumentException("Physiotherapist ID " + physioId + " not found.");
                        }
                        List<Appointment> physioSlots = clinic.getTimetable().getAvailableSlotsByPhysiotherapist(physioId);
                        if (physioSlots.isEmpty()) {
                            System.out.println("No available slots for physiotherapist: " + physioId);
                            break;
                        }
                        physioSlots.forEach(a ->
                                System.out.println("ID: " + a.getId() + ", Time: " + a.getStartTime() +
                                        ", Treatment: " + a.getTreatment().getName()));
                        System.out.print("Enter Appointment ID: ");
                        String apptId2 = scanner.nextLine();
                        if (physioSlots.stream().noneMatch(a -> a.getId().equals(apptId2))) {
                            throw new IllegalArgumentException("Appointment ID " + apptId2 + " not found or not available for physiotherapist " + physioId + ".");
                        }
                        System.out.print("Enter Patient ID: ");
                        String patientId2 = scanner.nextLine();
                        Patient patient2 = clinic.getPatients().stream()
                                .filter(p -> p.getId().equals(patientId2))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Patient ID " + patientId2 + " not found."));
                        clinic.bookByPhysiotherapist(physioId, apptId2, patient2);
                        System.out.println("Appointment booked.");
                        break;
                    case 7:
                        System.out.print("Enter Current Appointment ID: ");
                        String oldApptId = scanner.nextLine();
                        Appointment oldAppt = clinic.getTimetable().findById(oldApptId);
                        if (oldAppt == null) {
                            throw new IllegalArgumentException("Appointment ID " + oldApptId + " not found.");
                        }
                        System.out.print("Enter Patient ID: ");
                        String reschedulePatientId = scanner.nextLine();
                        Patient reschedulePatient = clinic.getPatients().stream()
                                .filter(p -> p.getId().equals(reschedulePatientId))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Patient ID " + reschedulePatientId + " not found."));
                        String requiredExpertise = oldAppt.getTreatment().getExpertiseRequired();
                        List<Appointment> availableSlots = clinic.getTimetable().getAvailableSlotsByExpertise(requiredExpertise);
                        if (availableSlots.isEmpty()) {
                            System.out.println("No available slots for expertise: " + requiredExpertise);
                            break;
                        }
                        availableSlots.forEach(a ->
                                System.out.println("ID: " + a.getId() + ", Time: " + a.getStartTime() +
                                        ", Treatment: " + a.getTreatment().getName() +
                                        ", Physiotherapist: " + a.getPhysiotherapist().getName()));
                        System.out.print("Enter New Appointment ID: ");
                        String newApptId = scanner.nextLine();
                        clinic.rescheduleAppointment(oldApptId, newApptId, reschedulePatient);
                        System.out.println("Appointment rescheduled from " + oldApptId + " to " + newApptId + ".");
                        break;
                    case 8:
                        System.out.print("Enter Appointment ID: ");
                        String cancelId = scanner.nextLine();
                        Appointment cancelAppt = clinic.getTimetable().findById(cancelId);
                        if (cancelAppt == null) {
                            throw new IllegalArgumentException("Appointment ID " + cancelId + " not found.");
                        }
                        System.out.print("Enter Patient ID: ");
                        String cancelPatientId = scanner.nextLine();
                        Patient cancelPatient = clinic.getPatients().stream()
                                .filter(p -> p.getId().equals(cancelPatientId))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Patient ID " + cancelPatientId + " not found."));
                        clinic.cancelAppointment(cancelId, cancelPatient);
                        System.out.println("Appointment cancelled.");
                        break;
                    case 9:
                        System.out.print("Enter Appointment ID to mark as attended: ");
                        String attendId = scanner.nextLine();
                        Appointment appt = clinic.getTimetable().findById(attendId);
                        if (appt == null) {
                            throw new IllegalArgumentException("Appointment ID " + attendId + " not found.");
                        }
                        appt.attend();
                        System.out.println("Appointment marked as attended.");
                        break;
                    case 10:
                        System.out.println("=== All Patients ===");
                        clinic.getPatients().forEach(p -> System.out.println("ID: " + p.getId() + ", Name: " + p.getName()));
                        break;
                    case 11:
                        System.out.println("=== All Appointments ===");
                        clinic.getTimetable().getAllAppointments().forEach(a ->
                                System.out.println("ID: " + a.getId() + ", Physio: " + a.getPhysiotherapist().getName() +
                                        ", Treatment: " + a.getTreatment().getName() + ", Time: " + a.getStartTime() +
                                        ", Status: " + a.getStatus()));
                        break;
                    case 12:
                        reportGenerator.generateReport();
                        break;
                    case 13:
                        System.out.println("Exiting...");
                        return;
                    case 14: // New option: View Patient Appointments
                        System.out.print("Enter Patient ID: ");
                        String patientIdView = scanner.nextLine();
                        Patient patientView = clinic.getPatients().stream()
                                .filter(p -> p.getId().equals(patientIdView))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Patient ID " + patientIdView + " not found."));
                        System.out.println("Appointments for " + patientView.getName() + " (" + patientIdView + "):");
                        List<Appointment> patientAppointments = clinic.getPatientAppointments(patientIdView);
                        if (patientAppointments.isEmpty()) {
                            System.out.println("No upcoming appointments.");
                        } else {
                            patientAppointments.forEach(a ->
                                    System.out.println("ID: " + a.getId() + ", Time: " + a.getStartTime() +
                                            ", Treatment: " + a.getTreatment().getName() +
                                            ", Physiotherapist: " + a.getPhysiotherapist().getName() +
                                            ", Status: " + a.getStatus()));
                        }
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void initializeData() {
        // 4 Physiotherapists
        Physiotherapist p1 = new Physiotherapist("P1", "Dr. Smith", "123 Main St", "555-0101");
        p1.addExpertise("Physiotherapy");
        p1.addExpertise("Rehabilitation");
        p1.addTreatment(new Treatment("Massage", "Physiotherapy"));
        p1.addTreatment(new Treatment("Pool Rehabilitation", "Rehabilitation"));

        Physiotherapist p2 = new Physiotherapist("P2", "Dr. Jones", "456 Oak St", "555-0102");
        p2.addExpertise("Osteopathy");
        p2.addTreatment(new Treatment("Acupuncture", "Osteopathy"));

        Physiotherapist p3 = new Physiotherapist("P3", "Dr. Lee", "789 Pine St", "555-0103");
        p3.addExpertise("Physiotherapy");
        p3.addTreatment(new Treatment("Neural Mobilisation", "Physiotherapy"));

        Physiotherapist p4 = new Physiotherapist("P4", "Dr. Brown", "321 Elm St", "555-0104");
        p4.addExpertise("Rehabilitation");
        p4.addTreatment(new Treatment("Mobilisation of the Spine", "Rehabilitation"));

        clinic.addPhysiotherapist(p1);
        clinic.addPhysiotherapist(p2);
        clinic.addPhysiotherapist(p3);
        clinic.addPhysiotherapist(p4);

        // 10 Patients
        clinic.addPatient(new Patient("PT1", "Alice Brown", "789 Pine St", "555-0201"));
        clinic.addPatient(new Patient("PT2", "Bob White", "321 Elm St", "555-0202"));
        clinic.addPatient(new Patient("PT3", "Charlie Green", "654 Maple St", "555-0203"));
        clinic.addPatient(new Patient("PT4", "Diana Blue", "987 Cedar St", "555-0204"));
        clinic.addPatient(new Patient("PT5", "Eve Black", "123 Birch St", "555-0205"));
        clinic.addPatient(new Patient("PT6", "Frank Red", "456 Spruce St", "555-0206"));
        clinic.addPatient(new Patient("PT7", "Grace Yellow", "789 Willow St", "555-0207"));
        clinic.addPatient(new Patient("PT8", "Henry Orange", "321 Ash St", "555-0208"));
        clinic.addPatient(new Patient("PT9", "Ivy Purple", "654 Oak St", "555-0209"));
        clinic.addPatient(new Patient("PT10", "Jack Gray", "987 Pine St", "555-0210"));

        // 4-Week Timetable (March 24 - April 20, 2025)
        Timetable timetable = clinic.getTimetable();
        // Week 1
        timetable.addSlot(new Appointment("A1", LocalDateTime.of(2025, 3, 24, 9, 0), p1, p1.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A2", LocalDateTime.of(2025, 3, 24, 10, 0), p1, p1.getTreatments().get(1)));
        timetable.addSlot(new Appointment("A3", LocalDateTime.of(2025, 3, 24, 11, 0), p2, p2.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A4", LocalDateTime.of(2025, 3, 26, 9, 0), p3, p3.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A5", LocalDateTime.of(2025, 3, 27, 9, 0), p4, p4.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A18", LocalDateTime.of(2025, 3, 27, 14, 0), p4, p4.getTreatments().get(0)));
        // Week 2
        timetable.addSlot(new Appointment("A6", LocalDateTime.of(2025, 3, 31, 9, 0), p1, p1.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A7", LocalDateTime.of(2025, 4, 1, 9, 0), p2, p2.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A8", LocalDateTime.of(2025, 4, 2, 9, 0), p3, p3.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A9", LocalDateTime.of(2025, 4, 3, 9, 0), p4, p4.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A19", LocalDateTime.of(2025, 4, 3, 14, 0), p4, p4.getTreatments().get(0)));
        // Week 3
        timetable.addSlot(new Appointment("A10", LocalDateTime.of(2025, 4, 7, 9, 0), p1, p1.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A11", LocalDateTime.of(2025, 4, 8, 9, 0), p2, p2.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A12", LocalDateTime.of(2025, 4, 9, 9, 0), p3, p3.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A13", LocalDateTime.of(2025, 4, 10, 9, 0), p4, p4.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A20", LocalDateTime.of(2025, 4, 10, 14, 0), p4, p4.getTreatments().get(0)));
        // Week 4
        timetable.addSlot(new Appointment("A14", LocalDateTime.of(2025, 4, 14, 9, 0), p1, p1.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A15", LocalDateTime.of(2025, 4, 15, 9, 0), p2, p2.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A16", LocalDateTime.of(2025, 4, 16, 9, 0), p3, p3.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A17", LocalDateTime.of(2025, 4, 17, 9, 0), p4, p4.getTreatments().get(0)));
        timetable.addSlot(new Appointment("A21", LocalDateTime.of(2025, 4, 17, 14, 0), p4, p4.getTreatments().get(0)));
    }
}