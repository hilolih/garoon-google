/*
* garoon-google
* Copyright (c) 2015 Cybozu
*
* Licensed under the MIT License
*/
package com.cybozu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
* DoshinGaroonDaiya
* Garoonに運行WEBのダイヤを登録する
*
*/
public class DoshinGaroonDaiya {

    private List<com.cybozu.garoon3.schedule.Event> GaroonSchedules;
    private String Keyword = "--- From Unkou Web ---";

    DoshinGaroonDaiya () {
        GaroonSchedules = new ArrayList<com.cybozu.garoon3.schedule.Event>();
    }

    public void add(com.cybozu.garoon3.schedule.Event event) {
        this.GaroonSchedules.add(event);
    }

    /*
     * event内部をみてもし運行WEBから登録されているダイヤだったらArrayListに加える
     * 
     */
    public void add_if_unkoweb_daiya(com.cybozu.garoon3.schedule.Event event) {
        if ( event.getDescription().indexOf(this.Keyword) >= 0 ) {
            this.GaroonSchedules.add(event);
        }
    }

    public List<com.cybozu.garoon3.schedule.Event> getGaroonSchedules() {
        return this.GaroonSchedules;
    }

}
