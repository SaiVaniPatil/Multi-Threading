
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 *
 */
public class ThreadExecutor {

    public static void main(String[] args) {
        int count = args.length;
        HashMap<String, String> fileMap = new HashMap<String, String>(); // to sort data based on mean

        ExecutorService taskExecutor = Executors.newFixedThreadPool(count);
        for (String filePath : args) {
            taskExecutor.execute(new FileParser(filePath, fileMap));
        }
        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        try {
            Comparator<Entry<String, String>> valueComparator = new Comparator<Entry<String, String>>() {

                @Override
                public int compare(Entry<String, String> e1, Entry<String, String> e2) {
                    String v1 = e1.getValue();
                    String[] splitted = v1.split("\t");
                    float s0 = Float.valueOf(splitted[0].trim()); //mean
                    double s1 = Double.valueOf(splitted[1].trim()); //sd

                    String v2 = e2.getValue();
                    String[] splitted2 = v2.split("\t");
                    float s2 = Float.valueOf(splitted2[0].trim()); //mean
                    double s3 = Double.valueOf(splitted2[1].trim()); //sd

                    int floatComparevalue = Float.compare(s2, s0);

                    if (floatComparevalue == 0) {
                        int doubleComparevalue = Double.compare(s3, s1);
                        if (doubleComparevalue == 0) {
                            return (e2.getKey().compareTo(e1.getKey()));

                        } else {
                            return doubleComparevalue;
                        }

                    } else {
                        return floatComparevalue;
                    }

                }
            };

            Set<Entry<String, String>> entries = fileMap.entrySet();
            
            List<Entry<String, String>> listOfEntries = new ArrayList<Entry<String, String>>(entries);
           
            Collections.sort(listOfEntries, valueComparator);
            LinkedHashMap<String, String> sortedByValue = new LinkedHashMap<String, String>(listOfEntries.size());

           
            // copying entries from List to Map
            for (Entry<String, String> entry : listOfEntries) {
                sortedByValue.put(entry.getKey(), entry.getValue());              
            }
            File file = new File("Stats.txt");            
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);
           
            for (Map.Entry<String, String> m : sortedByValue.entrySet()) {

                String[] splitted = m.getValue().split("\t");
                String s0 = splitted[0].trim(); //mean
                String s1 = splitted[1].trim(); //sd
                pw.println(m.getKey() + "\t" + s0 + " \t " + s1);
            }

            pw.flush();
            pw.close();
            fos.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

   

}
