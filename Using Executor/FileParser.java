
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 *
 */
public class FileParser extends Thread {

    String filePath;
    ArrayList listOfNums = new ArrayList();
    float mean = 0;
    double sd = 0;
    HashMap<String, String> map;

    public FileParser(String fileName, HashMap treeMap) {
        this.filePath = fileName;
        this.map = treeMap;
    }

    @Override
    public void run() {

        Scanner scan = null;
        try {

            scan = new Scanner(new File(filePath));

            while (scan.hasNext()) {

                String input = scan.next();               
                try {
                    float value = Float.valueOf(input);
                    listOfNums.add(value);
                    mean = mean + value;
                } catch (Exception e) {
                    System.err.println("This is not a float value : " + input);                   
                    continue;
                }

            }
            scan.close();
            mean = mean / (listOfNums.size());
            sd = calculateDeviation(listOfNums, mean);
            //System.out.println("mean:" + mean);
            //System.out.println("standard drviation" + sd);
            updateMap();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    private double calculateDeviation(ArrayList listOfNums, float mean) {
        double sdLocal = 0;
        for (int i = 0; i < listOfNums.size(); i++) {
            float output = (float) listOfNums.get(i) - mean;
            sdLocal = Math.pow(output, 2) + sdLocal;

        }
        sdLocal = sdLocal / listOfNums.size();
        sdLocal = Math.sqrt(sdLocal);
        return sdLocal;
    }

    private synchronized void updateMap() {
        String value = this.mean + "\t" + this.sd;
        this.map.put(this.filePath, value);
    }

}
