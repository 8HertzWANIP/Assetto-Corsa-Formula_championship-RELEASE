module com.ac.backend {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires java.desktop;
    requires com.google.gson;
	requires org.apache.httpcomponents.httpclient;
	requires org.apache.httpcomponents.httpcore;
	requires java.net.http;

    opens com.ac to javafx.fxml;
    opens com.ac.backend to javafx.fxml, com.google.gson;
    exports com.ac.backend to javafx.fxml, com.google.gson;
    exports com.ac;
}
