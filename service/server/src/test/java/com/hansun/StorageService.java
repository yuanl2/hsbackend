package com.hansun;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StorageService {

    public String saveResult(List<DeviceData> resultList, String file) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
//            for (DeviceData data :
//                    resultList) {
                String result = objectMapper.writeValueAsString(resultList);
//                JSONObject jsonObject = new JSONObject(result);
//                bw.write(jsonObject.toString());
                bw.write(result);
                bw.flush();
//            }

        } catch (Exception e) {

        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    public List<DeviceData> getResult(String file) {
        BufferedReader br = null;
        FileReader fr = null;
        List<DeviceData> resultList = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            fr = new FileReader(file);
            if (fr == null) {
                return null;
            }
            br = new BufferedReader(fr);
            if (br == null) {
                return null;
            }
            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {

                DeviceData result = objectMapper.readValue(sCurrentLine, DeviceData.class);
                resultList.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }


}
