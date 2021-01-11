package com.lisdoo.jstock.cli;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class StockList {

    String line;
    boolean isFolder;
    String folderOrFile;
    Date date;

    @Override
    public String toString() {
        return "StockList{" +
                "folderOrFile='" + folderOrFile + '\'' +
                ", date=" + date +
                '}';
    }
}
