package com.bpc.service;

import com.bpc.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClinicSystemTest {
    private ClinicSystem clinic;
    private Physiotherapist physio1;
    private Physiotherapist physio2;
    private Patient patient1;
    private Patient patient2;
    private LocalDateTime currentDateTime;

    @BeforeEach
    void setUp() {
        currentDateTime = LocalDateTime.of(2025, 3, 23, 0, 0);
        clinic = new ClinicSystem(currentDateTime);
        physio1 = new Physiotherapist("P1", "Dr. Smith", "1 Test St", "555-0000");
        physio1.addExpertise("Physiotherapy");
        physio1.addTreatment(new Treatment("Massage", "Physiotherapy"));
        physio2 = new Physiotherapist("P2", "Dr. Jones", "2 Test St", "555-0001");
        physio2.addExpertise("Osteopathy");
        physio2.addTreatment(new Treatment("Acupuncture", "Osteopathy"));
        clinic.addPhysiotherapist(physio1);
        clinic.addPhysiotherapist(physio2);
        patient1 = new Patient("PT1", "Test Patient 1", "3 Test St", "555-0002");
        patient2 = new Patient("PT2", "Test Patient 2", "4 Test St", "555-0003");
        clinic.addPatient(patient1);
        clinic.addPatient(patient2);
        clinic.getTimetable().addSlot(new Appointment("A1", LocalDateTime.of(2025, 3, 24, 9, 0), physio1, physio1.getTreatments().get(0)));
        clinic.getTimetable().addSlot(new Appointment("A2", LocalDateTime.of(2025, 3, 25, 9, 0), physio1, physio1.getTreatments().get(0)));
        clinic.getTimetable().addSlot(new Appointment("A3", LocalDateTime.of(2025, 3, 26, 9, 0), physio2, physio2.getTreatments().get(0)));
        clinic.getTimetable().addSlot(new Appointment("A4", LocalDateTime.of(2025, 3, 22, 9, 0), physio1, physio1.getTreatments().get(0))); // Past appointment
    }

    @Test
    void testAddPatient() {
        assertEquals(2, clinic.getPatients().size());
        Patient newPatient = new Patient("PT3", "New Patient", "5 Test St", "555-0004");
        clinic.addPatient(newPatient);
        assertEquals(3, clinic.getPatients().size());
    }

    @Test
    void testAddPatientDuplicateId() {
        assertThrows(IllegalArgumentException.class, () -> {
            clinic.addPatient(new Patient("PT1", "Duplicate Patient", "6 Test St", "555-0005"));
        });
    }

    @Test
    void testRemovePatient() {
        clinic.removePatient("PT1");
        assertEquals(1, clinic.getPatients().size());
    }

    @Test
    void testBookByExpertise() {
        Appointment booked = clinic.bookByExpertise("Physiotherapy", "A1", patient1);
        assertEquals("booked", booked.getStatus());
        assertEquals(patient1, booked.getPatient());
    }

    @Test
    void testBookByExpertisePastAppointment() {
        assertThrows(IllegalStateException.class, () -> {
            clinic.bookByExpertise("Physiotherapy", "A4", patient1);
        });
    }

    @Test
    void testBookByExpertiseInvalidSlot() {
        assertThrows(IllegalArgumentException.class, () -> {
            clinic.bookByExpertise("Physiotherapy", "A999", patient1);
        });
    }

    @Test
    void testBookByPhysiotherapist() {
        Appointment booked = clinic.bookByPhysiotherapist("P1", "A1", patient1);
        assertEquals("booked", booked.getStatus());
        assertEquals(patient1, booked.getPatient());
    }

    @Test
    void testBookByPhysiotherapistWrongPhysio() {
        assertThrows(IllegalArgumentException.class, () -> {
            clinic.bookByPhysiotherapist("P2", "A1", patient2);
        });
    }

    @Test
    void testBookNonExistentAppointment() {
        assertThrows(IllegalArgumentException.class, () -> {
            clinic.bookByPhysiotherapist("P1", "A999", patient1);
        });
    }

    @Test
    void testCancelAppointment() {
        clinic.bookByExpertise("Physiotherapy", "A1", patient1);
        clinic.cancelAppointment("A1", patient1);
        Appointment cancelled = clinic.getTimetable().findById("A1");
        assertEquals("cancelled", cancelled.getStatus());
        assertNull(cancelled.getPatient());
    }

    @Test
    void testCancelAppointmentNotBooked() {
        assertThrows(IllegalStateException.class, () -> {
            clinic.cancelAppointment("A1", patient1);
        });
    }

    @Test
    void testAttendAppointment() {
        clinic.bookByExpertise("Physiotherapy", "A1", patient1);
        Appointment appt = clinic.getTimetable().findById("A1");
        appt.attend();
        assertEquals("attended", appt.getStatus());
    }

    @Test
    void testAttendNonBookedAppointment() {
        Appointment appt = clinic.getTimetable().findById("A1");
        assertThrows(IllegalStateException.class, appt::attend);
    }

    @Test
    void testRescheduleAppointment() {
        clinic.bookByExpertise("Physiotherapy", "A1", patient1);
        clinic.rescheduleAppointment("A1", "A2", patient1);
        Appointment oldSlot = clinic.getTimetable().findById("A1");
        Appointment newSlot = clinic.getTimetable().findById("A2");
        assertEquals("available", oldSlot.getStatus());
        assertNull(oldSlot.getPatient());
        assertEquals("booked", newSlot.getStatus());
        assertEquals(patient1, newSlot.getPatient());
    }

    @Test
    void testRescheduleToPastAppointment() {
        clinic.bookByExpertise("Physiotherapy", "A1", patient1);
        assertThrows(IllegalStateException.class, () -> {
            clinic.rescheduleAppointment("A1", "A4", patient1);
        });
    }

    @Test
    void testRescheduleToNonAvailableSlot() {
        clinic.bookByExpertise("Physiotherapy", "A1", patient1);
        clinic.bookByExpertise("Physiotherapy", "A2", patient2);
        assertThrows(IllegalStateException.class, () -> {
            clinic.rescheduleAppointment("A1", "A2", patient1);
        });
    }

    @Test
    void testRescheduleWrongExpertise() {
        clinic.bookByExpertise("Physiotherapy", "A1", patient1);
        assertThrows(IllegalArgumentException.class, () -> {
            clinic.rescheduleAppointment("A1", "A3", patient1); // A3 is Osteopathy
        });
    }

    @Test
    void testGetAvailableByExpertise() {
        List<Appointment> slots = clinic.getTimetable().getAvailableSlotsByExpertise("Physiotherapy");
        assertEquals(3, slots.size());
        assertTrue(slots.stream().anyMatch(a -> a.getId().equals("A1")));
    }

    @Test
    void testGetAvailableByPhysiotherapist() {
        List<Appointment> slots = clinic.getTimetable().getAvailableSlotsByPhysiotherapist("P1");
        assertEquals(3, slots.size());
        assertTrue(slots.stream().anyMatch(a -> a.getId().equals("A1")));
    }

    @Test
    void testAppointmentStatusTransitions() {
        Appointment appt = clinic.getTimetable().findById("A1");
        appt.book(patient1, currentDateTime);
        assertEquals("booked", appt.getStatus());
        appt.cancel();
        assertEquals("cancelled", appt.getStatus());
        assertThrows(IllegalStateException.class, appt::attend);
    }

    @Test
    void testBookAlreadyBookedAppointment() {
        clinic.bookByExpertise("Physiotherapy", "A1", patient1);
        assertThrows(IllegalArgumentException.class, () -> {
            clinic.bookByExpertise("Physiotherapy", "A1", patient2);
        });
    }

    @Test
    void testReportGenerationStatusSummary() {
        clinic.bookByExpertise("Physiotherapy", "A1", patient1);
        clinic.getTimetable().findById("A1").attend();
        clinic.bookByPhysiotherapist("P1", "A2", patient2);
        ReportGenerator report = new ReportGenerator(clinic);
        report.generateReport(); // Manual verification of status summary
    }

    @Test
    void testAddOverlappingSlot() {
        assertThrows(IllegalArgumentException.class, () -> {
            clinic.getTimetable().addSlot(new Appointment("A5", LocalDateTime.of(2025, 3, 24, 9, 0), physio1, physio1.getTreatments().get(0)));
        });
    }

    @Test
    void testGetPatientAppointments() {
        clinic.bookByExpertise("Physiotherapy", "A1", patient1);
        clinic.bookByExpertise("Physiotherapy", "A2", patient1);
        clinic.getTimetable().findById("A1").attend();
        clinic.cancelAppointment("A2", patient1);

        List<Appointment> appointments = clinic.getPatientAppointments("PT1");
        assertEquals(1, appointments.size());
        assertEquals("A1", appointments.get(0).getId());
        assertEquals("attended", appointments.get(0).getStatus());

        List<Appointment> noAppointments = clinic.getPatientAppointments("PT2");
        assertTrue(noAppointments.isEmpty());
    }
}