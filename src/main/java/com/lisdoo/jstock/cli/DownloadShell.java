package com.lisdoo.jstock.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class DownloadShell {

    public static void main(String[] args) {

        String fileOrFolder = "";
    }

    public static void bypy(String fileOrFolder) {

        File path = new File("./temp");

        Process process;
        try {
            process = Runtime.getRuntime().exec(String.format("bypy download /jstocklog/%s .", fileOrFolder), null, path);//查看我的 .bash_history里面的grep 命令使用历史记录
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int exitValue = process.waitFor();
            while((line = reader.readLine())!= null){
                System.out.println(line);
            }
            if (exitValue == 0){
                System.out.println( "successfully executed the linux command");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void tar(String fileOrFolder) {

        File path = new File("./temp");

        Process process;
        try {
            process = Runtime.getRuntime().exec(String.format("cat ./stocksinfo* | tar xj", fileOrFolder), null, path);//查看我的 .bash_history里面的grep 命令使用历史记录
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int exitValue = process.waitFor();
            while((line = reader.readLine())!= null){
                System.out.println(line);
            }
            if (exitValue == 0){
                System.out.println( "successfully executed the linux command");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
