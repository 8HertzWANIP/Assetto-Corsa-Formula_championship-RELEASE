package com.ac;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.ac.fileparsing.fileReader;
import com.ac.lib.aeroPart;



/**
 * JavaFX App test
 */
public class App extends Application {
    protected static String currentVersion = "ALPHA-v0.2.1";

    fileReader fParser = new fileReader();
    ArrayList<aeroPart> aeroParts = fParser.getAeroParts();
    public static Scene scene;
    public static Stage loadedStage;
    public static String loadedProfile = "";
    public static String defaultWindow = "landingPage";
    private final String environment = "PROD";


    @Override
    public void start(Stage stage) throws IOException {
        String url = "https://api.github.com/repos/8HertzWANIP/Assetto-Corsa-Formula_championship-RELEASE/releases/latest";
        Boolean appUpToDate = null;

        // Call github API to get release version of public repo
    	try {
            HttpResponse response;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet getConnection = new HttpGet(url);
            try {
                response = httpClient.execute(getConnection);
                String JSONString = EntityUtils.toString(response.getEntity(),
                        "UTF-8");

                Gson gson = new Gson();
                JsonObject js = gson.fromJson(JSONString, JsonObject.class);
                if (js.get("tag_name") != null) {
                    appUpToDate = js.get("tag_name").toString().contains(currentVersion);
                }
    
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Check for new version on gitHub.
        if (!appUpToDate && !environment.equals("DEV") && !environment.equals("EA")) {
            URL urlLand = new File("cardev/src/main/resources/com/ac/newVersionWindow.fxml").toURI().toURL();
            scene = new Scene(FXMLLoader.load(urlLand), 640, 250);
        } else {
            URL urlLand = new File("cardev/src/main/resources/com/ac/" + defaultWindow + ".fxml").toURI().toURL();
            scene = new Scene(FXMLLoader.load(urlLand), 1280, 720);
        }
        loadedStage = stage;
        loadedStage.setScene(scene);
        loadedStage.setTitle("Assetto Corsa: Formula Championship - " + currentVersion);
        loadedStage.setResizable(false);
        loadedStage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        URL url = new File("cardev/src/main/resources/com/ac/" + fxml + ".fxml").toURI().toURL();
        scene.setRoot(FXMLLoader.load(url));
    }

    public static void printLoadedProfile() {
        System.out.println(loadedProfile);
    }

    public static void main(String[] args){
        launch();
    }

}