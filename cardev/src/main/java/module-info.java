module com.ac {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires javafx.base;
    requires javafx.swing;
    requires java.desktop;
    requires com.google.gson;
	requires org.apache.httpcomponents.httpclient;
	requires org.apache.httpcomponents.httpcore;
	requires java.net.http;

    opens com.ac to javafx.fxml;
    opens com.ac.menus to javafx.fxml, javafx.graphics;
    opens com.ac.menus.uiObjects to javafx.fxml, javafx.graphics;
    opens com.ac.menus.facilities to javafx.fxml, com.google.gson;
    opens com.ac.seasons.newSeason to javafx.fxml, javafx.graphics;
    exports com.ac.fileparsing to javafx.fxml, com.google.gson;
    exports com.ac to javafx.fxml, javafx.graphics;
    exports com.ac.lib;
    exports com.ac.AI;
    exports com.ac.seasons.newSeason;
    exports com.ac.menus to javafx.fxml, javafx.graphics;
    exports com.ac.menus.facilities;
    exports com.ac.menus.uiObjects;
}
