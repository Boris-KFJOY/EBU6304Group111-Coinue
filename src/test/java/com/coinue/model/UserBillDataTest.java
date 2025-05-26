package com.coinue.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserBillDataTest {

    private UserBillData userBillData;
    private static final double DELTA = 0.0001; // Standard delta for double comparisons

    @BeforeEach
    void setUp() {
        userBillData = new UserBillData();
    }

    @Test
    void defaultConstructor_initializesFieldsCorrectly() {
        assertEquals(7500.00, userBillData.getCreditLimit(), DELTA);
        assertNotNull(userBillData.getBillRecords(), "BillRecords list should be initialized.");
        assertTrue(userBillData.getBillRecords().isEmpty(), "BillRecords list should be empty initially.");
        assertEquals(LocalDate.now(), userBillData.getCreatedDate(), "CreatedDate should be today.");
        assertEquals(LocalDate.now(), userBillData.getUpdatedDate(), "UpdatedDate should be today.");
        assertNull(userBillData.getLastImportedFile(), "LastImportedFile should be null initially.");
    }

    // --- BillRecord Inner Class Tests ---
    @Test
    void billRecordDefaultConstructor_initializesFieldsToNullOrZero() {
        UserBillData.BillRecord record = new UserBillData.BillRecord();
        assertNull(record.getDate());
        assertNull(record.getDescription());
        assertEquals(0.0, record.getAmount(), DELTA);
        assertNull(record.getStatus());
    }

    @Test
    void billRecordParameterizedConstructor_setsFieldsCorrectly() {
        LocalDate date = LocalDate.of(2023, 1, 15);
        String description = "Electricity Bill";
        double amount = 75.50;
        String status = "Paid";
        UserBillData.BillRecord record = new UserBillData.BillRecord(date, description, amount, status);

        assertEquals(date, record.getDate());
        assertEquals(description, record.getDescription());
        assertEquals(amount, record.getAmount(), DELTA);
        assertEquals(status, record.getStatus());
    }

    @Test
    void billRecordGettersAndSetters_workCorrectly() {
        UserBillData.BillRecord record = new UserBillData.BillRecord();
        LocalDate date = LocalDate.of(2024, 5, 1);
        record.setDate(date);
        assertEquals(date, record.getDate());

        String description = "Internet Service";
        record.setDescription(description);
        assertEquals(description, record.getDescription());

        double amount = 59.99;
        record.setAmount(amount);
        assertEquals(amount, record.getAmount(), DELTA);

        String status = "Pending";
        record.setStatus(status);
        assertEquals(status, record.getStatus());
    }

    // --- UserBillData Method Tests ---
    @Test
    void addBillRecord_addsRecordAndUpdatesDate() {
        UserBillData.BillRecord record = new UserBillData.BillRecord(LocalDate.now(), "Water Bill", 30.0, "Paid");
        LocalDate initialUpdateDate = userBillData.getUpdatedDate(); // Could be same as createdDate if no other ops
        
        userBillData.addBillRecord(record);
        
        assertEquals(1, userBillData.getBillRecords().size(), "Should have one record after adding.");
        assertTrue(userBillData.getBillRecords().contains(record), "The added record should be in the list.");
        assertEquals(LocalDate.now(), userBillData.getUpdatedDate(), "UpdatedDate should be refreshed to today.");
        // Ensure it changed if initialUpdateDate was from a different day (unlikely in this test sequence)
        // or ensure it's at least not before the initial if test is super fast.
        assertFalse(userBillData.getUpdatedDate().isBefore(initialUpdateDate), "UpdatedDate should not be before its initial value.");
    }

    @Test
    void clearBillRecords_removesAllRecordsAndUpdatesDate() {
        userBillData.addBillRecord(new UserBillData.BillRecord(LocalDate.now(), "Test1", 10.0, "Test"));
        userBillData.addBillRecord(new UserBillData.BillRecord(LocalDate.now(), "Test2", 20.0, "Test"));
        assertFalse(userBillData.getBillRecords().isEmpty(), "List should not be empty before clear.");

        userBillData.clearBillRecords();

        assertTrue(userBillData.getBillRecords().isEmpty(), "List should be empty after clear.");
        assertEquals(LocalDate.now(), userBillData.getUpdatedDate(), "UpdatedDate should be refreshed.");
    }

    @Test
    void setBillRecords_replacesListWithCopyAndUpdatesDate() {
        List<UserBillData.BillRecord> externalList = new ArrayList<>();
        UserBillData.BillRecord record1 = new UserBillData.BillRecord(LocalDate.now(), "Groceries", 100.0, "Paid");
        UserBillData.BillRecord record2 = new UserBillData.BillRecord(LocalDate.now(), "Gas", 50.0, "Paid");
        externalList.add(record1);
        externalList.add(record2);

        userBillData.setBillRecords(externalList);

        assertEquals(2, userBillData.getBillRecords().size(), "Internal list size should match external list.");
        assertTrue(userBillData.getBillRecords().contains(record1), "Record1 should be in internal list.");

        // Modify the external list to test defensive copy
        externalList.add(new UserBillData.BillRecord(LocalDate.now(), "Coffee", 5.0, "Paid"));
        assertEquals(2, userBillData.getBillRecords().size(), "Internal list size should not change when external list is modified.");
        
        assertEquals(LocalDate.now(), userBillData.getUpdatedDate(), "UpdatedDate should be refreshed.");
    }

    @Test
    void getTotalRepaymentAmount_calculatesSumCorrectly() {
        userBillData.addBillRecord(new UserBillData.BillRecord(LocalDate.now(), "R1", 10.0, "P"));
        userBillData.addBillRecord(new UserBillData.BillRecord(LocalDate.now(), "R2", 20.5, "P"));
        userBillData.addBillRecord(new UserBillData.BillRecord(LocalDate.now(), "R3", 30.0, "P"));
        assertEquals(60.5, userBillData.getTotalRepaymentAmount(), DELTA);
    }

    @Test
    void getTotalRepaymentAmount_emptyList_returnsZero() {
        assertEquals(0.0, userBillData.getTotalRepaymentAmount(), DELTA);
    }

    @Test
    void getCreditUsagePercentage_calculatesCorrectly() {
        userBillData.setCreditLimit(1000.0);
        userBillData.addBillRecord(new UserBillData.BillRecord(LocalDate.now(), "R1", 250.0, "P")); // 25% usage
        assertEquals(25.0, userBillData.getCreditUsagePercentage(), DELTA);
    }

    @Test
    void getCreditUsagePercentage_zeroCreditLimit_returnsZero() {
        userBillData.setCreditLimit(0.0);
        userBillData.addBillRecord(new UserBillData.BillRecord(LocalDate.now(), "R1", 50.0, "P"));
        assertEquals(0.0, userBillData.getCreditUsagePercentage(), DELTA);
    }

    @Test
    void getCreditUsagePercentage_negativeCreditLimit_returnsZero() {
        userBillData.setCreditLimit(-100.0);
        userBillData.addBillRecord(new UserBillData.BillRecord(LocalDate.now(), "R1", 50.0, "P"));
        assertEquals(0.0, userBillData.getCreditUsagePercentage(), DELTA);
    }
    
    @Test
    void getCreditUsagePercentage_zeroRepayment_returnsZero() {
        userBillData.setCreditLimit(1000.0);
        assertEquals(0.0, userBillData.getCreditUsagePercentage(), DELTA);
    }

    @Test
    void getRemainingCredit_calculatesCorrectly() {
        userBillData.setCreditLimit(1000.0);
        userBillData.addBillRecord(new UserBillData.BillRecord(LocalDate.now(), "R1", 300.0, "P"));
        assertEquals(700.0, userBillData.getRemainingCredit(), DELTA);
    }

    @Test
    void getRemainingCredit_repaymentExceedsLimit_returnsZero() {
        userBillData.setCreditLimit(500.0);
        userBillData.addBillRecord(new UserBillData.BillRecord(LocalDate.now(), "R1", 600.0, "P"));
        assertEquals(0.0, userBillData.getRemainingCredit(), DELTA);
    }

    @Test
    void getRemainingCredit_repaymentEqualsLimit_returnsZero() {
        userBillData.setCreditLimit(500.0);
        userBillData.addBillRecord(new UserBillData.BillRecord(LocalDate.now(), "R1", 500.0, "P"));
        assertEquals(0.0, userBillData.getRemainingCredit(), DELTA);
    }

    @Test
    void getRemainingCredit_zeroRepayment_returnsFullCreditLimit() {
        userBillData.setCreditLimit(1234.56);
        assertEquals(1234.56, userBillData.getRemainingCredit(), DELTA);
    }

    // --- Getters and Setters for UserBillData Fields ---
    @Test
    void setCreditLimit_updatesValueAndRefreshesUpdatedDate() {
        LocalDate initialUpdatedDate = userBillData.getUpdatedDate();
        userBillData.setCreditLimit(5000.0);
        assertEquals(5000.0, userBillData.getCreditLimit(), DELTA);
        assertEquals(LocalDate.now(), userBillData.getUpdatedDate(), "UpdatedDate should be refreshed.");
        if (initialUpdatedDate.isEqual(LocalDate.now())) {
             assertTrue(userBillData.getUpdatedDate().isEqual(initialUpdatedDate) || userBillData.getUpdatedDate().isAfter(initialUpdatedDate));
        } else {
            assertNotEquals(initialUpdatedDate, userBillData.getUpdatedDate(), "UpdatedDate should change.");
        }
    }

    @Test
    void setLastImportedFile_updatesValueAndRefreshesUpdatedDate() {
        String fileName = "bills_2023_final.csv";
        userBillData.setLastImportedFile(fileName);
        assertEquals(fileName, userBillData.getLastImportedFile());
        assertEquals(LocalDate.now(), userBillData.getUpdatedDate(), "UpdatedDate should be refreshed.");
    }

    @Test
    void setCreatedDate_updatesValueWithoutChangingUpdatedDate() {
        LocalDate newCreatedDate = LocalDate.of(2000, 1, 1);
        LocalDate originalUpdatedDate = userBillData.getUpdatedDate(); // Capture before change
        
        userBillData.setCreatedDate(newCreatedDate);
        assertEquals(newCreatedDate, userBillData.getCreatedDate());
        assertEquals(originalUpdatedDate, userBillData.getUpdatedDate(), "UpdatedDate should NOT change when only CreatedDate is set.");
    }

    @Test
    void setUpdatedDate_updatesValue() {
        LocalDate newUpdatedDate = LocalDate.of(2020, 2, 20);
        userBillData.setUpdatedDate(newUpdatedDate);
        assertEquals(newUpdatedDate, userBillData.getUpdatedDate());
    }

    @Test
    void toString_returnsCorrectFormatAndContent() {
        userBillData.setCreditLimit(1200.75);
        userBillData.addBillRecord(new UserBillData.BillRecord(LocalDate.of(2023,1,1), "Bill Alpha", 100.50, "Paid"));
        userBillData.addBillRecord(new UserBillData.BillRecord(LocalDate.of(2023,1,2), "Bill Beta", 200.25, "Unpaid"));
        // Total Repayment = 300.75. Usage = (300.75 / 1200.75) * 100 = 25.047% approx

        LocalDate fixedUpdateDate = LocalDate.of(2023, 3, 15);
        userBillData.setUpdatedDate(fixedUpdateDate); // Control this for stable toString test

        String expectedTotalRepayment = String.format("%.2f", 300.75);
        String expectedUsagePercentage = String.format("%.2f", (300.75 / 1200.75) * 100.0);

        String expectedString = "UserBillData{" +
                                "creditLimit=1200.75" +
                                ", billRecordsCount=2" +
                                ", totalRepayment=" + expectedTotalRepayment +
                                ", usagePercentage=" + expectedUsagePercentage + "%" +
                                ", lastUpdated=" + fixedUpdateDate +
                                '}';
        assertEquals(expectedString, userBillData.toString());
    }
} 