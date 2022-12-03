package com.ac;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.ac.backend.aeroPart;
import com.ac.backend.fileReader;



/**
 * JavaFX App
 */
public class App extends Application {
    protected static String currentVersion = "ALPHA-v0.1.1";

    fileReader fParser = new fileReader();
    ArrayList<aeroPart> aeroParts = fParser.getAeroParts();
    public static Scene scene;
    public static Stage loadedStage;
    public static String loadedProfile = "";


    @Override
    public void start(Stage stage) throws IOException {
        String url = "https://api.github.com/repos/8HertzWANIP/Assetto-Corsa-Formula_championship-RELEASE/releases/latest";
        Boolean appUpToDate = true;
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
        if (!appUpToDate) {
            scene = new Scene(loadFXML("newVersionWindow"), 640, 250);
        } else {
            scene = new Scene(loadFXML("landingPage"), 1280, 720);
        }
        loadedStage = stage;
        loadedStage.setScene(scene);
        loadedStage.setTitle("Assetto Corsa: Formula Championship - " + currentVersion);
        loadedStage.setResizable(false);
        loadedStage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    protected static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void printLoadedProfile() {
        System.out.println(loadedProfile);
    }

    public static void main(String[] args){
        launch();
    }

}