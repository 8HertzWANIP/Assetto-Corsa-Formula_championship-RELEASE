package com.ac.fileparsing;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;

import com.ac.App;
import com.ac.lib.aeroAPI;
import com.ac.lib.aeroPart;
import com.ac.lib.aeroPartInventory;
import com.ac.lib.carConfig;
import com.ac.seasons.newSeason.seasonSettings;
import com.ac.seasons.newSeason.teamSetup;

public class fileWriter extends App {
    public static void readFile(String filePath) {
        try {
            File iniObj = new File(filePath);
            Scanner myReader = new Scanner(iniObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }

    public static void writeFile(String filePath) {
        try {
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write("fileName testing");
            myWriter.close();
            System.out.println("Succesfully wrote to file.");
        } catch (Exception e) {
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }

    public static void copyToFile(String sourceFile, String targetFile) {
        File myObj = new File(targetFile);
        
        try {
            //Reader
            File sourceIniFile = new File(sourceFile);
            Scanner myReader = new Scanner(sourceIniFile);
            
            //Writer
            File targetIniFile = new File(targetFile);
            FileWriter myWriter = new FileWriter(targetIniFile);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                myWriter.write(data);
                myWriter.write(System.getProperty( "line.separator" ));
            }

            myWriter.close();
            myReader.close();
            System.out.println("Succesfully wrote to file.");
        } catch (Exception e) {
            System.out.println("An error occured");
            e.printStackTrace();
        } finally {
            if (myObj.exists()) {
                System.out.println("File name: " + myObj.getName());
                System.out.println("Absolute path: " + myObj.getAbsolutePath());
                System.out.println("Writeable: " + myObj.canWrite());
                System.out.println("Readable " + myObj.canRead());
                System.out.println("File size in bytes " + myObj.length());
            } else {
                System.out.println("The file does not exist.");
            }
        }
    }

    public static void writeAeroStats(String sourceFile, String targetFile, ArrayList<aeroPart> aeroParts) {
        File myObj = new File(targetFile);
        int partIndex = 0;
        boolean overwriting = false;
        String name = "NAME=" + aeroParts.get(partIndex).getlabel();
        String cl_gain = "CL_GAIN=" + aeroParts.get(partIndex).downforce;
        String cd_gain = "CD_GAIN=" + aeroParts.get(partIndex).drag;
        String angle = "ANGLE=" + aeroParts.get(partIndex).angle;
        
        try {
            //Reader
            File sourceIniFile = new File(sourceFile);
            Scanner myReader = new Scanner(sourceIniFile);
            
            //Writer
            File targetIniFile = new File(targetFile);
            if (targetIniFile.createNewFile()) {
                System.out.println(targetFile + " File created");
            } else {
                System.out.println(targetFile + " File already exists");
            }
            FileWriter myWriter = new FileWriter(targetIniFile);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                name = "NAME=" + aeroParts.get(partIndex).getlabel();

                if (data.contains(name) || overwriting) {
                    // System.out.println("Writing Wing Element: [" + aeroParts.get(partIndex).getlabel() + "]");
                    overwriting = true;
                    cl_gain = "CL_GAIN=" + aeroParts.get(partIndex).downforce;
                    cd_gain = "CD_GAIN=" + aeroParts.get(partIndex).drag;
                    angle = "ANGLE=" + aeroParts.get(partIndex).angle;

                    if (data.contains("CL_GAIN=")) {
                        myWriter.write(cl_gain);
                        myWriter.write(System.getProperty( "line.separator" ));
                        // System.out.println("Write to file [" + targetFile + "]: [" + cl_gain + "]");
                    }
                    else if (data.contains("CD_GAIN=")) {
                        myWriter.write(cd_gain);
                        myWriter.write(System.getProperty( "line.separator" ));
                        // System.out.println("Write to file [" + targetFile + "]: [" + cd_gain + "]");
                    }
                    else if (data.contains("ANGLE=")) {
                        myWriter.write(angle);
                        myWriter.write(System.getProperty( "line.separator" ));
                        // System.out.println("Write to file [" + targetFile + "]: [" + angle + "]");
                        if (partIndex < aeroParts.size() - 1) {
                            partIndex++;
                        }
                        overwriting = false;
                    }
                    else {
                        myWriter.write(data);
                        myWriter.write(System.getProperty( "line.separator" ));
                        // System.out.println("Write to file [" + targetFile + "]: [" + data + "]");
                    }

                } else {
                    overwriting = false;
                    myWriter.write(data);
                    myWriter.write(System.getProperty( "line.separator" ));
                    // System.out.println("Write to file [" + targetFile + "]: [" + data + "]");
                }
            }
            myWriter.close();
            myReader.close();
            System.out.println("Succesfully wrote to file.");
        } catch (Exception e) {
            System.out.println("An error occured");
            e.printStackTrace();
        } finally {
            if (myObj.exists()) {
                System.out.println("File name: " + myObj.getName());
                System.out.println("Absolute path: " + myObj.getAbsolutePath());
                System.out.println("Writeable: " + myObj.canWrite());
                System.out.println("Readable " + myObj.canRead());
                System.out.println("File size in bytes " + myObj.length());
                System.out.println("Overwritting file...");
                copyToFile(targetFile, sourceFile);
            } else {
                System.out.println("The file does not exist.");
            }
        }
    }

    public static void writeNewAeroStats(teamSetup team, aeroPartInventory[] inventory, seasonSettings season) {
        aeroPartInventory[] filteredInventory = aeroAPI.filterNewAeroStats(inventory, season);
        String aeroIni = team.getCarFolder() + "\\data\\aero.ini";
        String copyToIni = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile + "\\copyToIni.ini";
        File myObj = new File(copyToIni);
        int partIndex = 0;
        boolean overwriting = false;
        aeroPartInventory.part targetPart = filteredInventory[partIndex].installed;
        String name = "NAME=" + filteredInventory[partIndex].part_name;
        String cl_gain = "CL_GAIN=" + targetPart.filteredDownforce;
        String cd_gain = "CD_GAIN=" + targetPart.filteredDrag;
        String angle = "ANGLE=" + filteredInventory[partIndex].angle;
        
        try {
            //Reader
            File sourceIniFile = new File(aeroIni);
            Scanner myReader = new Scanner(sourceIniFile);
            
            //Writer
            File targetIniFile = new File(copyToIni);
            if (targetIniFile.createNewFile()) {
                System.out.println(copyToIni + " File created");
            } else {
                System.out.println(copyToIni + " File already exists");
            }
            FileWriter myWriter = new FileWriter(targetIniFile);

            while (myReader.hasNextLine()) {
                targetPart = filteredInventory[partIndex].installed;
                String data = myReader.nextLine();
                // System.out.println(data);
                name = "NAME=" + filteredInventory[partIndex].part_name;

                if (data.contains(name) || overwriting) {
                    overwriting = true;
                    cl_gain = "CL_GAIN=" + targetPart.filteredDownforce;
                    cd_gain = "CD_GAIN=" + targetPart.filteredDrag;
                    angle = "ANGLE=" + filteredInventory[partIndex].angle;

                    if (data.contains("CL_GAIN=")) {
                        myWriter.write(cl_gain);
                        myWriter.write(System.getProperty( "line.separator" ));
                        // System.out.println("Write to file: [" + cl_gain + "]");
                    }
                    else if (data.contains("CD_GAIN=")) {
                        myWriter.write(cd_gain);
                        myWriter.write(System.getProperty( "line.separator" ));
                        // System.out.println("Write to file: [" + cd_gain + "]");
                    }
                    else if (data.contains("ANGLE=")) {
                        myWriter.write(angle);
                        myWriter.write(System.getProperty( "line.separator" ));
                        if (partIndex < filteredInventory.length - 1) {
                            // System.out.println("Write to file: [" + angle + "]");
                            partIndex++;
                        }
                        overwriting = false;
                    }
                    else {
                        myWriter.write(data);
                        myWriter.write(System.getProperty( "line.separator" ));
                        // System.out.println("Write to file [" + copyToIni + "]: [" + data + "]");
                    }

                } else {
                    overwriting = false;
                    myWriter.write(data);
                    myWriter.write(System.getProperty( "line.separator" ));
                    // System.out.println("Write to file [" + copyToIni + "]: [" + data + "]");
                }
            }
            myWriter.close();
            myReader.close();
            System.out.println("Succesfully wrote to file.");
        } catch (Exception e) {
            System.out.println("An error occured");
            e.printStackTrace();
        } finally {
            if (myObj.exists()) {
                System.out.println("File name: " + myObj.getName());
                System.out.println("Absolute path: " + myObj.getAbsolutePath());
                System.out.println("Writeable: " + myObj.canWrite());
                System.out.println("Readable " + myObj.canRead());
                System.out.println("File size in bytes " + myObj.length());
                System.out.println("Overwritting file...");
                copyToFile(copyToIni, aeroIni);
            } else {
                System.out.println("The file does not exist.");
            }
        }
    }

    public static void writeCarStats(teamSetup team, aeroPartInventory[] inventory, carConfig car) {

        String copyToIni = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Ac Cardev save data\\" + App.loadedProfile + "\\copyToIni.ini";
        String carIni = team.getCarFolder() + "\\data\\car.ini";
        File myObj = new File(carIni);
        boolean overwriting = false;
        
        try {
            //Reader
            File sourceIniFile = new File(carIni);
            Scanner myReader = new Scanner(sourceIniFile);
            
            //Writer
            File targetIniFile = new File(copyToIni);
            if (targetIniFile.createNewFile()) {
                System.out.println(copyToIni + " File created");
            } else {
                System.out.println(copyToIni + " File already exists");
            }
            FileWriter myWriter = new FileWriter(targetIniFile);
            String fuel = "";

            while (myReader.hasNextLine()) {
                String max_fuel = "MAX_FUEL=" + car.getMaxFuel();
                String data = myReader.nextLine();

                if (data.contains("[FUEL]") || overwriting) {
                    // System.out.println("Writing Wing Element: [" + aeroParts.get(partIndex).getlabel() + "]");
                    overwriting = true;

                    if (data.contains("FUEL=") && fuel.equals("")) {
                        fuel = "FUEL=" + car.getFuel();
                        myWriter.write(fuel);
                        myWriter.write(System.getProperty( "line.separator" ));
                        System.out.println("Write fuel to file [" + carIni + "]: [" + fuel + "]");
                    }
                    else if (data.contains("MAX_FUEL=") && !fuel.equals("")) {
                        myWriter.write(max_fuel);
                        myWriter.write(System.getProperty( "line.separator" ));
                        System.out.println("Write max_fuel to file [" + carIni + "]: [" + max_fuel + "]");
                        overwriting = false;
                    }
                    else {
                        myWriter.write(data);
                        myWriter.write(System.getProperty( "line.separator" ));
                        // System.out.println("Write to file [" + carIni + "]: [" + data + "]");
                    }

                } else {
                    overwriting = false;
                    myWriter.write(data);
                    myWriter.write(System.getProperty( "line.separator" ));
                    // System.out.println("Write to file [" + carIni + "]: [" + data + "]");
                }
            }
            myWriter.close();
            myReader.close();
            System.out.println("Succesfully wrote to file.");
        } catch (Exception e) {
            System.out.println("An error occured");
            e.printStackTrace();
        } finally {
            if (myObj.exists()) {
                System.out.println("File name: " + myObj.getName());
                System.out.println("Absolute path: " + myObj.getAbsolutePath());
                System.out.println("Writeable: " + myObj.canWrite());
                System.out.println("Readable " + myObj.canRead());
                System.out.println("File size in bytes " + myObj.length());
                System.out.println("Overwritting file...");
                copyToFile(copyToIni, carIni);
            } else {
                System.out.println("The file does not exist.");
            }
        }
    }

    public static void writeCarStats(String sourceFile, String targetFile, carConfig car) {
        File myObj = new File(targetFile);
        boolean overwriting = false;
        
        try {
            //Reader
            File sourceIniFile = new File(sourceFile);
            Scanner myReader = new Scanner(sourceIniFile);
            
            //Writer
            File targetIniFile = new File(targetFile);
            FileWriter myWriter = new FileWriter(targetIniFile);
            String fuel = "";

            while (myReader.hasNextLine()) {
                String max_fuel = "MAX_FUEL=" + car.getMaxFuel();
                String data = myReader.nextLine();

                if (data.contains("[FUEL]") || overwriting) {
                    // System.out.println("Writing Wing Element: [" + aeroParts.get(partIndex).getlabel() + "]");
                    overwriting = true;

                    if (data.contains("FUEL=") && fuel.equals("")) {
                        fuel = "FUEL=" + car.getFuel();
                        myWriter.write(fuel);
                        myWriter.write(System.getProperty( "line.separator" ));
                        System.out.println("Write fuel to file [" + targetFile + "]: [" + fuel + "]");
                    }
                    else if (data.contains("MAX_FUEL=") && !fuel.equals("")) {
                        myWriter.write(max_fuel);
                        myWriter.write(System.getProperty( "line.separator" ));
                        System.out.println("Write max_fuel to file [" + targetFile + "]: [" + max_fuel + "]");
                        overwriting = false;
                    }
                    else {
                        myWriter.write(data);
                        myWriter.write(System.getProperty( "line.separator" ));
                        // System.out.println("Write to file [" + targetFile + "]: [" + data + "]");
                    }

                } else {
                    overwriting = false;
                    myWriter.write(data);
                    myWriter.write(System.getProperty( "line.separator" ));
                    // System.out.println("Write to file [" + targetFile + "]: [" + data + "]");
                }
            }
            myWriter.close();
            myReader.close();
            System.out.println("Succesfully wrote to file.");
        } catch (Exception e) {
            System.out.println("An error occured");
            e.printStackTrace();
        } finally {
            if (myObj.exists()) {
                System.out.println("File name: " + myObj.getName());
                System.out.println("Absolute path: " + myObj.getAbsolutePath());
                System.out.println("Writeable: " + myObj.canWrite());
                System.out.println("Readable " + myObj.canRead());
                System.out.println("File size in bytes " + myObj.length());
                System.out.println("Overwritting file...");
                copyToFile(targetFile, sourceFile);
            } else {
                System.out.println("The file does not exist.");
            }
        }
    }

    public static void createFile(String filePath) {
        try {
            File testFile = new File(filePath);

            if (testFile.createNewFile()) {
                System.out.println("Fiile created: [" + testFile.getName() + "]");
            } else {
                System.out.println("File already exists.");
            }
        } catch (Exception e) {
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }
}
