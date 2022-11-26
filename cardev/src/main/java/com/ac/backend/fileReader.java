package com.ac.backend;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class fileReader {
    

    public static boolean emptyLine = false;
    public static boolean gotWing = false;
    private static String[] filteredObjects = {"THROTTLE"};

    static ArrayList<aeroPart> aeroParts = new ArrayList<aeroPart>();

    public ArrayList<aeroPart> getAeroParts() {
        return aeroParts;
    }

    public static void setAeroParts(ArrayList<aeroPart> aeroParts) {
        fileReader.aeroParts = aeroParts;
    }

    public static void clearAeroParts() {
        fileReader.aeroParts = new ArrayList<aeroPart>();
    }

    public static boolean isEmptyLine() {
        return emptyLine;
    }

    public static void setEmptyLine(boolean emptyLine) {
        fileReader.emptyLine = emptyLine;
    }

    public static boolean isGotWing() {
        return gotWing;
    }

    public static void setGotWing(boolean gotWing) {
        fileReader.gotWing = gotWing;
    }
    
    public static void readFile(String file)
    {
        try{
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(file);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   {
                System.out.println (empty_filter(strLine));
                // Print the content on the console
            }
            //Close the input stream
            in.close();
        }
        catch (Exception e){
            //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    public static void fillAeroParts(String file)
    {
        aeroPart part = new aeroPart(0, "", 0f, 0f, 0);
        try{
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(file);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   {
                if (strLine.contains("NAME"))
                {
                    part.id = aeroParts.size();
                    System.out.println ("------------------------------");
                    System.out.println(strLine);
                    part.setlabel(strLine.replace("NAME=", ""));

                }
                if (strLine.contains("CL_GAIN") && !strLine.contains("YAW"))
                {
                    System.out.println(strLine);
                    final Pattern pattern = Pattern.compile("([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[eE]([+-]?\\d+))?");
                    // Match regex against strLine
                    final Matcher matcher = pattern.matcher(strLine);
                    // System.out.println ("PART downforce = ["+matcher.find()+"]");
                    if (matcher.find()){
                        part.downforce = Float.parseFloat(matcher.group(0));
                    }
                }
                if (strLine.contains("CD_GAIN"))
                {
                    System.out.println(strLine);
                    final Pattern pattern = Pattern.compile("([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[eE]([+-]?\\d+))?");
                    // Match regex against strLine
                    final Matcher matcher = pattern.matcher(strLine);
                    // System.out.println ("PART drag = ["+matcher.find()+"]");
                    if (matcher.find()){
                        part.drag = Float.parseFloat(matcher.group(0));
                    }
                }
                if (strLine.contains("ANGLE"))
                {
                    System.out.println(strLine);
                    final Pattern pattern = Pattern.compile("[0-9]+");
                    // Match regex against strLine
                    final Matcher matcher = pattern.matcher(strLine);
                    // System.out.println ("PART angle = ["+matcher.find()+"]");
                    if (matcher.find()){
                        part.angle = Integer.parseInt(matcher.group(0));
                    }

                    // Filter out parts that have no downforce and drag value.
                    ArrayList<String> partFilter = new ArrayList<>();
                    for (String label : filteredObjects) {
                        partFilter.add(label);
                    }
                    if (part.downforce == 0.0f && part.drag == 0.0f || partFilter.contains(part.getlabel())) {
                        //Do nothing.
                    } else {
                        aeroParts.add(part);
                    }
                    part = new aeroPart(0, "", 0f, 0f, 0);
                }
            }
            //Close the input stream
            in.close();
        }
        catch (Exception e){
            //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static String empty_filter(String line){
        if(line.length() > 1)
        {
            return line;
        }
        else if (line.length() == 0 && !emptyLine)
        {
            emptyLine = true;
            return line;
        }
        else
        {
            emptyLine = false;
            return "";
        }
    }
    
    public static carConfig fillCarStats(String file)
    {
        carConfig car = new carConfig(0, 0, 0, 0);
        try{
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(file);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            boolean readFuelInfo = false;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   {
                if (readFuelInfo) {
                    System.out.println(strLine);
                    if (strLine.contains("MAX_FUEL=")) {
                        String s = strLine.replace("MAX_FUEL=", "");
                        System.out.println("setMaxFuel: [" + s + "]");
                        car.setMaxFuel(Integer.parseInt(s));
                        readFuelInfo = false;
                    } else if (strLine.contains("FUEL=")) {
                        String s = strLine.replace("FUEL=", "");
                        System.out.println("setFuel: [" + s + "]");
                        car.setFuel(Integer.parseInt(s));
                    }
                }
                if (strLine.contains("[FUEL]")) {
                    readFuelInfo = true;
                }
            }
            //Close the input stream
            in.close();
        }
        catch (Exception e){
            //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return car;
    }
}
