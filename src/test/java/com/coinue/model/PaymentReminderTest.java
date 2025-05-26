package com.coinue.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PaymentReminderTest {

    private static final double DELTA = 0.001; // For double comparisons

    @Test
    void primaryConstructor_shouldInitializeAllFields() {
        LocalDate dueDate = LocalDate.of(2024, 12, 31);
        PaymentReminder reminder = new PaymentReminder("Credit Card", 100.0, dueDate, "/custom/icon.png");

        assertEquals("Credit Card", reminder.getPlatform());
        assertEquals(100.0, reminder.getAmount(), DELTA);
        assertEquals(dueDate, reminder.getDueDate());
        assertEquals("/custom/icon.png", reminder.getIconPath());
    }

    @Test
    void secondaryConstructor_shouldInitializeWithDefaultIconPath() {
        LocalDate dueDate = LocalDate.of(2025, 1, 15);
        PaymentReminder reminder = new PaymentReminder("Loan", 500.50, dueDate);

        assertEquals("Loan", reminder.getPlatform());
        assertEquals(500.50, reminder.getAmount(), DELTA);
        assertEquals(dueDate, reminder.getDueDate());
        assertEquals("/images/icons/credit_card.png", reminder.getIconPath(), "Icon path should default.");
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        LocalDate dueDate = LocalDate.now().plusDays(10);
        PaymentReminder reminder = new PaymentReminder("TestPlatform", 0, dueDate, "initial/path.png");

        // Test getters (already tested by constructors, but good for completeness)
        assertEquals("TestPlatform", reminder.getPlatform());
        assertEquals(0, reminder.getAmount(), DELTA);
        assertEquals(dueDate, reminder.getDueDate());
        assertEquals("initial/path.png", reminder.getIconPath());

        reminder.setIconPath("new/path.png");
        assertEquals("new/path.png", reminder.getIconPath());
    }

    @Test
    void getDaysUntilDue_futureDate_shouldReturnPositiveDays() {
        LocalDate currentDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 1, 11);
        PaymentReminder reminder = new PaymentReminder("Test", 10, dueDate);
        assertEquals(10, reminder.getDaysUntilDue(currentDate));
    }

    @Test
    void getDaysUntilDue_sameDate_shouldReturnZeroDays() {
        LocalDate currentDate = LocalDate.of(2024, 3, 15);
        LocalDate dueDate = LocalDate.of(2024, 3, 15);
        PaymentReminder reminder = new PaymentReminder("Test", 10, dueDate);
        assertEquals(0, reminder.getDaysUntilDue(currentDate));
    }

    @Test
    void getDaysUntilDue_pastDate_shouldReturnNegativeDays() {
        LocalDate currentDate = LocalDate.of(2024, 6, 20);
        LocalDate dueDate = LocalDate.of(2024, 6, 10);
        PaymentReminder reminder = new PaymentReminder("Test", 10, dueDate);
        assertEquals(-10, reminder.getDaysUntilDue(currentDate));
    }

    @Test
    void needsReminder_dueDateFarFuture_shouldReturnFalse() {
        LocalDate currentDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 1, 15); // 14 days away
        PaymentReminder reminder = new PaymentReminder("Test", 10, dueDate);
        assertFalse(reminder.needsReminder(currentDate));
    }

    @Test
    void needsReminder_dueDateExactly7Days_shouldReturnTrue() {
        LocalDate currentDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 1, 8); // 7 days away
        PaymentReminder reminder = new PaymentReminder("Test", 10, dueDate);
        assertTrue(reminder.needsReminder(currentDate));
    }
    
    @Test
    void needsReminder_dueDateExactly8Days_shouldReturnFalse() {
        LocalDate currentDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 1, 9); // 8 days away
        PaymentReminder reminder = new PaymentReminder("Test", 10, dueDate);
        assertFalse(reminder.needsReminder(currentDate));
    }

    @Test
    void needsReminder_dueDateWithin7Days_shouldReturnTrue() {
        LocalDate currentDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 1, 5); // 4 days away
        PaymentReminder reminder = new PaymentReminder("Test", 10, dueDate);
        assertTrue(reminder.needsReminder(currentDate));
    }

    @Test
    void needsReminder_dueDateIsToday_shouldReturnTrue() {
        LocalDate currentDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 1, 1); // 0 days away
        PaymentReminder reminder = new PaymentReminder("Test", 10, dueDate);
        assertTrue(reminder.needsReminder(currentDate));
    }
    
    @Test
    void needsReminder_dueDateIsTomorrow_shouldReturnTrue() {
        LocalDate currentDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 1, 2); // 1 day away
        PaymentReminder reminder = new PaymentReminder("Test", 10, dueDate);
        assertTrue(reminder.needsReminder(currentDate));
    }

    @Test
    void needsReminder_dueDateInPast_shouldReturnFalse() {
        LocalDate currentDate = LocalDate.of(2024, 1, 10);
        LocalDate dueDate = LocalDate.of(2024, 1, 1); // 9 days past
        PaymentReminder reminder = new PaymentReminder("Test", 10, dueDate);
        assertFalse(reminder.needsReminder(currentDate));
    }
} 