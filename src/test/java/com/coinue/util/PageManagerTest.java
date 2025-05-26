package com.coinue.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class PageManagerTest {

    private PageManager pageManager;
    private Stage primaryStage;

    // Paths to dummy FXML and CSS files in test resources
    private static final String DUMMY_FXML_PATH = "/view/dummy.fxml";
    private static final String DUMMY_WITH_CONTROLLER_FXML_PATH = "/view/dummy_with_controller.fxml";
    private static final String MAIN_CSS_PATH_FOR_TEST = "/styles/main.css"; // Relative to test resources

    @Start
    public void start(Stage stage) {
        this.primaryStage = stage;
        // Ensure the dummy CSS file exists for the test
        // The actual main.css is in main/resources, but PageManager loads it via getClass().getResource()
        // so for testing PageManager's CSS loading logic, we use one from test/resources
    }

    @BeforeEach
    void setUp() throws Exception {
        // Reset PageManager singleton instance before each test for isolation
        Field instanceField = PageManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        pageManager = PageManager.getInstance();

        // Create a dummy main.css in the expected location if PageManager tries to load it from main resources
        // However, PageManager uses getClass().getResource(), so it should pick up from test/resources if available there.
        // For this test, we ensure src/test/resources/styles/main.css exists.
    }

    @AfterEach
    void tearDown() {
        Platform.runLater(() -> {
            if (primaryStage != null && primaryStage.isShowing()) {
                primaryStage.close();
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void getInstance_shouldReturnSingletonInstance() {
        PageManager instance1 = PageManager.getInstance();
        PageManager instance2 = PageManager.getInstance();
        assertSame(instance1, instance2, "getInstance should always return the same instance.");
    }

    @Test
    void initStage_shouldSetPrimaryStageAndMinDimensions(FxRobot robot) {
        // initStage itself doesn't modify scene graph in a way that needs runLater immediately
        // for its direct operations, but it does call initializePages which loads FXML.
        // For safety and consistency, especially if initializePages becomes more complex:
        Platform.runLater(() -> pageManager.initStage(primaryStage));
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(pageManager.getPrimaryStage(), "Primary stage should be initialized.");
        assertEquals(primaryStage, pageManager.getPrimaryStage(), "Primary stage should be the one provided.");
        assertEquals(800, primaryStage.getMinWidth(), "Min width should be set on the stage.");
        assertEquals(600, primaryStage.getMinHeight(), "Min height should be set on the stage.");
    }

    @Test
    void switchToPage_uninitializedStage_shouldThrowIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> {
            // This call doesn't modify UI yet, it throws exception before that
            pageManager.switchToPage(DUMMY_FXML_PATH);
        }, "Calling switchToPage before initStage should throw IllegalStateException.");
    }

    @Test
    void switchToPage_validFxml_shouldSwitchSceneAndApplyCss(FxRobot robot) throws InterruptedException {
        Platform.runLater(() -> pageManager.initStage(primaryStage));
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> primaryStage.show());
        WaitForAsyncUtils.waitForFxEvents();

        assertDoesNotThrow(() -> {
            Platform.runLater(() -> {
                try {
                    pageManager.switchToPage(DUMMY_FXML_PATH);
                } catch (IOException e) {
                    fail("Should not throw IOException for valid FXML: " + e.getMessage());
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
        });

        assertNotNull(primaryStage.getScene(), "Scene should be set after switching page.");
        assertNotNull(primaryStage.getScene().getRoot(), "Scene root should not be null.");
        assertTrue(primaryStage.getScene().getRoot().getChildrenUnmodifiable().stream()
                   .anyMatch(node -> node instanceof Label && "Dummy Page Content".equals(((Label)node).getText())),
                   "Dummy page content should be loaded.");

        URL cssUrl = getClass().getResource(MAIN_CSS_PATH_FOR_TEST);
        assertNotNull(cssUrl, "Test CSS file main.css should be found in test resources.");
        assertTrue(primaryStage.getScene().getStylesheets().contains(cssUrl.toExternalForm()),
                   "Scene should have the main.css stylesheet from test resources.");
    }

    @Test
    void switchToPage_cssNotFound_shouldContinueWithoutCss(FxRobot robot) throws InterruptedException {
        Platform.runLater(() -> pageManager.initStage(primaryStage));
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> primaryStage.show());
        WaitForAsyncUtils.waitForFxEvents();

        assertDoesNotThrow(() -> {
            Platform.runLater(() -> {
                try {
                    // This will try to load main.css. PageManager should handle its absence.
                    pageManager.switchToPage(DUMMY_FXML_PATH);
                } catch (IOException e) {
                    fail("switchToPage should not throw IOException related to FXML loading if CSS is missing, but proceed: " + e.getMessage());
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
        }, "switchToPage itself should not throw an exception if CSS is not found (it prints a warning).");
    }

    @Test
    void switchToPage_invalidFxml_shouldThrowIOException(FxRobot robot) {
        Platform.runLater(() -> pageManager.initStage(primaryStage));
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> primaryStage.show());
        WaitForAsyncUtils.waitForFxEvents();

        String invalidPath = "/view/non_existent_page.fxml";
        Exception[] thrownException = new Exception[1];

        Platform.runLater(() -> {
            try {
                pageManager.switchToPage(invalidPath);
            } catch (IOException e) {
                thrownException[0] = e;
            } catch (Exception e) { 
                thrownException[0] = e; 
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(thrownException[0], "An Exception should have been thrown and caught.");
        assertTrue(thrownException[0] instanceof IOException, "The thrown exception should be an IOException from PageManager.");
        
        String actualMessage = thrownException[0].getMessage();
        System.out.println("Caught exception message from PageManager: " + actualMessage); 
        
        // Based on the new understanding, the NullPointerException from FXMLLoader.load(null)
        // is caught by the generic `catch (Exception e)` in PageManager.
        String expectedPrefix = "Unexpected error while loading page: " + invalidPath;
        String expectedCauseSubstring = "Location is required."; // From NullPointerException due to FXMLLoader.load(null)
        
        assertTrue(actualMessage != null && actualMessage.startsWith(expectedPrefix), 
                   "Exception message should start with '" + expectedPrefix + "'. Actual: " + actualMessage);
        assertTrue(actualMessage != null && actualMessage.contains(expectedCauseSubstring),
                   "Exception message should also contain the original cause '" + expectedCauseSubstring + "'. Actual: " + actualMessage);
    }

    @Test
    void switchToPage_preservesWindowDimensions(FxRobot robot) throws InterruptedException {
        Platform.runLater(() -> {
            pageManager.initStage(primaryStage);
            primaryStage.setWidth(900);
            primaryStage.setHeight(700);
            primaryStage.show();
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            try {
                pageManager.switchToPage(DUMMY_FXML_PATH);
            } catch (IOException e) {
                fail("switchToPage failed: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(900, primaryStage.getWidth(), "Stage width should be preserved.");
        assertEquals(700, primaryStage.getHeight(), "Stage height should be preserved.");

        Platform.runLater(() -> {
            primaryStage.setWidth(700); // Smaller than minWidth 800
            primaryStage.setHeight(500); // Smaller than minHeight 600
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            try {
                pageManager.switchToPage(DUMMY_FXML_PATH);
            } catch (IOException e) {
                fail("switchToPage failed: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(800, primaryStage.getWidth(), "Stage width should be at least minWidth.");
        assertEquals(600, primaryStage.getHeight(), "Stage height should be at least minHeight.");
    }

    @Test
    void switchToPage_withControllerInitializer_shouldInitializeController(FxRobot robot) throws InterruptedException {
        Platform.runLater(() -> {
            pageManager.initStage(primaryStage);
            primaryStage.show();
        });
        WaitForAsyncUtils.waitForFxEvents();

        final boolean[] initializerCalled = {false};
        final DummyController[] controllerInstance = {null};

        PageManager.ControllerInitializer initializer = controller -> {
            assertNotNull(controller, "Controller passed to initializer should not be null.");
            assertTrue(controller instanceof DummyController, "Controller should be an instance of DummyController.");
            controllerInstance[0] = (DummyController) controller;
            ((DummyController) controller).markAsInitializedByManager();
            initializerCalled[0] = true;
        };

        Platform.runLater(() -> {
            try {
                pageManager.switchToPage(DUMMY_WITH_CONTROLLER_FXML_PATH, initializer);
            } catch (IOException e) {
                fail("switchToPage with controller initializer failed: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(initializerCalled[0], "ControllerInitializer should have been called.");
        assertNotNull(controllerInstance[0], "Controller instance should have been captured.");
        assertTrue(controllerInstance[0].isInitializedByManager(), "Controller should be marked as initialized by manager.");
        assertEquals("Controller Initialized", controllerInstance[0].getLabelText(), "Controller's FXML initialize method should also have run.");

        URL cssUrl = getClass().getResource(MAIN_CSS_PATH_FOR_TEST);
        assertNotNull(cssUrl, "Test CSS file main.css should be found in test resources for controller variant.");
        assertTrue(primaryStage.getScene().getStylesheets().contains(cssUrl.toExternalForm()),
                   "Scene should have the main.css stylesheet for controller variant.");
    }
}
