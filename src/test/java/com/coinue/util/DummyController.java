package com.coinue.util;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DummyController {
    @FXML
    private Label dummyControllerLabel;

    private boolean initializedByManager = false;

    @FXML
    public void initialize() {
        if (dummyControllerLabel != null) {
            dummyControllerLabel.setText("Controller Initialized");
        }
    }

    public void markAsInitializedByManager() {
        this.initializedByManager = true;
    }

    public boolean isInitializedByManager() {
        return initializedByManager;
    }

    public String getLabelText() {
        return dummyControllerLabel != null ? dummyControllerLabel.getText() : "Label not_found";
    }
} 