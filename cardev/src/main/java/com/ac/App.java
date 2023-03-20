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

import com.ac.fileparsing.fileReader;
import com.ac.lib.aeroPart;
import com.ac.lib.seasonData;



/**
 * JavaFX App test
 */
public class App extends Application {
    
    fileReader fParser = new fileReader();
    ArrayList<aeroPart> aeroParts = fParser.getAeroParts();
    public static Scene scene;
    public static Stage loadedStage;
    public static String loadedProfile = "";
    public static String defaultWindow = "landingPage";
    public final String environment = "PROD";
    public static seasonData seasonData = null;
    protected static String currentVersion = "ALPHA-v0.2.6";


    @Override
    public void start(Stage stage) throws IOException {
        if (!environment.equals("PROD")) {
            currentVersion = currentVersion + " - " + environment;
        }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/newVersionWindow.fxml"));
            scene = new Scene(loader.load(), 640, 250);
            
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/landingPage.fxml"));
            scene = new Scene(loader.load(), 1280, 720);
        }
        loadedStage = stage;
        loadedStage.setScene(scene);
        loadedStage.setTitle("Assetto Corsa: Formula Championship - " + currentVersion);
        loadedStage.setResizable(false);
        loadedStage.show();
    }

    protected static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    public static void setRoot(FXMLLoader loader) throws IOException {
        scene.setRoot(loader.load());
    }

    public static void printLoadedProfile() {
        System.out.println(loadedProfile);
    }

    public static void main(String[] args){
        launch();
    }

    public static void setSeasonData() {
        seasonData = new seasonData();
        seasonData.loadSeasonData();
    }
}