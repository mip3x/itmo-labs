module ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.apache.logging.log4j;
    requires org.apache.commons.lang3;

    opens ui to javafx.fxml;
    exports ui;
    exports main;
    opens main to javafx.fxml;
}