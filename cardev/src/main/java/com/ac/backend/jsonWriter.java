package com.ac.backend;

import com.ac.App;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
 
public class jsonWriter extends App
{
    public static void saveSeasonSettings(seasonSettings season) {
         
        String path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + season.profileName + "\\season_settings.json";
        // System.out.println(path);
        File directory = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\"+season.profileName);
        if (! directory.exists()){
            directory.mkdirs();
        }
 
        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
            Gson gson = new Gson();
            String jsonString = gson.toJson(season);
            out.write(jsonString);
        }  catch (Exception e) {
            System.out.println("An error occured");
            e.printStackTrace();
        } finally {
            if (directory.exists()) {
                System.out.println("File name: " + directory.getName());
                System.out.println("Absolute path: " + directory.getAbsolutePath());
                System.out.println("Writeable: " + directory.canWrite());
                System.out.println("Readable " + directory.canRead());
                System.out.println("File size in bytes " + directory.length());
            } else {
                System.out.println("The file does not exist.");
            }
        }
    }

    public void saveTeam(teamSetup team) {
         
        String path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile + "\\" + team.teamName + "\\team.json";
        System.out.println(path);
        File directory = new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + loadedProfile + "\\" + team.teamName);
        if (! directory.exists()){
            directory.mkdirs();
        }
 
        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
            Gson gson = new Gson();
            String jsonString = gson.toJson(team);
            out.write(jsonString);
        }  catch (Exception e) {
            System.out.println("An error occured");
            e.printStackTrace();
        } finally {
            if (directory.exists()) {
                System.out.println("File name: " + directory.getName());
                System.out.println("Absolute path: " + directory.getAbsolutePath());
                System.out.println("Writeable: " + directory.canWrite());
                System.out.println("Readable " + directory.canRead());
                System.out.println("File size in bytes " + directory.length());
            } else {
                System.out.println("The file does not exist.");
            }
        }
    }
}