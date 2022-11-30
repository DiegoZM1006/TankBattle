package com.example.tankbattle.model;

import java.io.*;
import java.nio.charset.StandardCharsets;
// import com.google.gson.Gson;
import com.google.gson.Gson;

public class FileUtil {

    public static String readFile() {
        String output="";
        File f = new File("data.txt");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line;
            while((line = reader.readLine()) != null){
                output += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
    }


    public static void fillDb(String text) {
        File file = new File("data.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(text.getBytes(StandardCharsets.UTF_8));
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
