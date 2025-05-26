package com.coinue.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private static final double DELTA = 0.001; // For double comparisons

    @Test
    void defaultConstructor_shouldCreateEmptyCategory() {
        Category category = new Category();
        assertNull(category.getName(), "Default constructor name should be null.");
        assertEquals(0.0, category.getBudget(), DELTA, "Default constructor budget should be 0.0.");
        assertNull(category.getDescription(), "Default constructor description should be null.");
    }

    @Test
    void constructorWithNameAndBudget_shouldInitializeCorrectly() {
        Category category = new Category("Food", 150.0);
        assertEquals("Food", category.getName());
        assertEquals(150.0, category.getBudget(), DELTA);
        assertNull(category.getDescription(), "Description should be null when not provided.");
    }

    @Test
    void constructorWithAllFields_shouldInitializeCorrectly() {
        Category category = new Category("Utilities", 200.0, "Monthly bills");
        assertEquals("Utilities", category.getName());
        assertEquals(200.0, category.getBudget(), DELTA);
        assertEquals("Monthly bills", category.getDescription());
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        Category category = new Category();

        category.setName("Transport");
        assertEquals("Transport", category.getName());

        category.setBudget(75.50);
        assertEquals(75.50, category.getBudget(), DELTA);

        category.setDescription("Public transport and fuel");
        assertEquals("Public transport and fuel", category.getDescription());
    }

    @Test
    void toString_shouldReturnCorrectFormat() {
        Category category1 = new Category("Shopping", 300.0, "Clothes and electronics");
        String expected1 = "Category{name='Shopping', budget=300.00, description='Clothes and electronics'}";
        assertEquals(expected1, category1.toString());

        Category category2 = new Category("Entertainment", 100.0);
        // Description is null
        String expected2 = "Category{name='Entertainment', budget=100.00, description='null'}";
        assertEquals(expected2, category2.toString());

        Category category3 = new Category();
        String expected3 = "Category{name='null', budget=0.00, description='null'}";
        assertEquals(expected3, category3.toString());
    }

    @Test
    void equals_reflexivity() {
        Category category = new Category("Test", 10.0);
        assertTrue(category.equals(category), "Category should be equal to itself.");
    }

    @Test
    void equals_symmetry() {
        Category category1 = new Category("Health", 50.0);
        Category category2 = new Category("Health", 50.0);
        Category category3 = new Category("Health", 100.0); // Same name, different budget

        assertTrue(category1.equals(category2), "Categories with same name should be equal.");
        assertTrue(category2.equals(category1), "Equality should be symmetric.");

        // Current equals only checks name, so budget difference doesn't matter for equals
        assertTrue(category1.equals(category3), "Categories with same name but different budget should be equal by current logic.");
        assertTrue(category3.equals(category1), "Symmetry for same name, different budget.");
    }

    @Test
    void equals_withNull_shouldReturnFalse() {
        Category category = new Category("Gifts", 20.0);
        assertFalse(category.equals(null), "Category should not be equal to null.");
    }

    @Test
    void equals_withDifferentClass_shouldReturnFalse() {
        Category category = new Category("Education", 500.0);
        String otherObject = "Education";
        assertFalse(category.equals(otherObject), "Category should not be equal to an object of a different class.");
    }

    @Test
    void equals_sameName_shouldReturnTrue() {
        Category category1 = new Category("Books", 30.0, "Fiction");
        Category category2 = new Category("Books", 40.0, "Non-fiction");
        assertTrue(category1.equals(category2), "Categories with the same name should be equal.");
    }

    @Test
    void equals_differentName_shouldReturnFalse() {
        Category category1 = new Category("Groceries", 200.0);
        Category category2 = new Category("Dining Out", 200.0);
        assertFalse(category1.equals(category2), "Categories with different names should not be equal.");
    }

    @Test
    void equals_oneNameNull_shouldReturnFalse() {
        Category category1 = new Category(null, 10.0);
        Category category2 = new Category("NameNotNull", 10.0);
        assertFalse(category1.equals(category2), "Category with null name should not be equal to category with non-null name.");
        assertFalse(category2.equals(category1), "Symmetry for one name null.");
    }

    @Test
    void equals_bothNamesNull_shouldReturnTrueIfConsideredEqual() {
        // Current implementation: name.equals(category.name) will NPE if this.name is null.
        // Let's test current behavior and then suggest a fix if needed.
        Category category1 = new Category(null, 10.0);
        Category category2 = new Category(null, 20.0);
        // As per current equals(): if (name != null && name.equals(category.name))
        // This means if name is null, it returns false. So, two categories with null names are NOT equal.
        assertFalse(category1.equals(category2), "Two categories with null names should NOT be equal with current implementation.");
    }
    
    @Test
    void equals_nameWithDifferentCase_shouldReturnFalse() {
        Category category1 = new Category("Travel", 1000.0);
        Category category2 = new Category("travel", 1000.0);
        assertFalse(category1.equals(category2), "Category names are case-sensitive in equals.");
    }


    @Test
    void hashCode_consistencyWithEquals() {
        Category category1 = new Category("Subscriptions", 25.0);
        Category category2 = new Category("Subscriptions", 25.0);
        Category category3 = new Category("Subscriptions", 50.0); // Different budget, same name

        assertTrue(category1.equals(category2));
        assertEquals(category1.hashCode(), category2.hashCode(), "Equal objects must have equal hashCodes.");

        assertTrue(category1.equals(category3)); // Based on current name-only equals
        assertEquals(category1.hashCode(), category3.hashCode(), "Objects equal by name-only logic must have equal hashCodes.");
    }

    @Test
    void hashCode_forDifferentNames() {
        Category category1 = new Category("Sports", 60.0);
        Category category2 = new Category("Hobbies", 60.0);
        assertNotEquals(category1.hashCode(), category2.hashCode(), "Objects with different names should ideally have different hashCodes (though not strictly required if not equal).");
    }

    @Test
    void hashCode_withNullName_shouldReturnZero() {
        Category category = new Category(null, 10.0);
        assertEquals(0, category.hashCode(), "hashCode for null name should be 0 as per current implementation.");
    }
} 