module cs113.trolley {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens cs113.trolley to javafx.fxml;
    exports cs113.trolley;
}