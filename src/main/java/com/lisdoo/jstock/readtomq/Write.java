package com.lisdoo.jstock.readtomq;

import com.alibaba.fastjson.JSONArray;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Write {

    String path;
    String fileName;
    BufferedOutputStream bos;

    public Write(String path, String fileName) throws FileNotFoundException {
        File file = new File(path, fileName);
        if (file.exists()) file.delete();
        bos = new BufferedOutputStream(new FileOutputStream(file));
    }

    public void put(String s) throws IOException {
        bos.write("callback({\"Data\":[[".getBytes(StandardCharsets.UTF_8));
        bos.write(s.getBytes(StandardCharsets.UTF_8));
        bos.write("]]});\r\n".getBytes(StandardCharsets.UTF_8));
    }

    public void put(JSONArray o) throws IOException {
        put(o.toString());
    }

    public void close() throws IOException {
        bos.flush();
        bos.close();
    }
}
