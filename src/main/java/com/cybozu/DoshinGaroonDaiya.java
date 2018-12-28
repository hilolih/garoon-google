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
import java.util.Date;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.cybozu.garoon3.schedule.Span;

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

    /*
     * 指定した日付にすでに登録した運行WEBダイヤがあるかどうか
     * 
     */
    public Boolean existsGaroonSchedules(Date date){
        return this.GaroonSchedules.stream().anyMatch(ev -> {
            return this.compareDate(date, ev);
        });
    }

    /*
     * すでに登録されているGaroonのイベントと差異があるかどうか
     * 
     */
    public Boolean diffGaroonSchedule(Date date, String daiya){
        List<com.cybozu.garoon3.schedule.Event> list = this.GaroonSchedules.stream()
           .filter(ev -> { return this.compareDate(date, ev); })
           .collect(Collectors.toList());

        if ( list.size() != 1 ) {
            System.err.println("[!] 同日に２つ以上の運行WEBダイヤがあります。処理をスキップします: " + date);
            // 同じイベントがあるようにみせて処理をスキップさせる
            return false;
        }

        return list.stream().noneMatch(ev -> {
            return false;
        });
    }

    private Boolean compareDate(Date date, com.cybozu.garoon3.schedule.Event ev) {
        Span span = ev.getSpans().get(0);
        Date start = span.getStart();
        return date.compareTo( start ) == 0;
    }

}

