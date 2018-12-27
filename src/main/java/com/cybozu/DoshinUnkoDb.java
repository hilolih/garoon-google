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

import java.util.ArrayList;
import java.util.HashMap;

import java.time.LocalDateTime;

/*
* DoshinUnkoDb
* devsrvにあるunkodbデータベースからデータを取得する
*
*/
public class DoshinUnkoDb {
	private Connection CON;
	private Statement STMT;
    private ArrayList<String> Columns = new ArrayList<String>();
    private String Sql;
    private LocalDateTime ThisMonth, NextMonth;

    DoshinUnkoDb(String url, String account, String pass, String ncode) throws Exception {
        Class.forName("org.postgresql.Driver");

        //PostgreSQLへ接続
        this.CON = DriverManager.getConnection(url, account, pass);
        this.STMT = this.CON.createStatement();

        // unkodbには、先月、今月、来月のダイヤがあるので、今月、来月のみデータを取得するSQL
        // を作成する
        LocalDateTime d = LocalDateTime.now();
        this.ThisMonth = d;
        this.NextMonth = d.plusMonths(1);
        String t = String.valueOf( this.ThisMonth.getMonthValue() );
        String n = String.valueOf( this.NextMonth.getMonthValue() );

        this.Sql = "select * from daiya where ncode = '" 
            + ncode + "' and month IN ('" + t + "', '" + n + "');";

        // System.out.println(this.Sql);

        // 取得したい列の名前を追加...
        
        this.Columns.add("d1");
        this.Columns.add("d2");
        this.Columns.add("d3");
        this.Columns.add("d4");
        this.Columns.add("d5");
        this.Columns.add("d6");
        this.Columns.add("d7");
        this.Columns.add("d8");
        this.Columns.add("d9");
        this.Columns.add("d10");
        this.Columns.add("d11");
        this.Columns.add("d12");
        this.Columns.add("d13");
        this.Columns.add("d14");
        this.Columns.add("d15");
        this.Columns.add("d16");
        this.Columns.add("d17");
        this.Columns.add("d18");
        this.Columns.add("d19");
        this.Columns.add("d20");
        this.Columns.add("d21");
        this.Columns.add("d22");
        this.Columns.add("d23");
        this.Columns.add("d24");
        this.Columns.add("d25");
        this.Columns.add("d26");
        this.Columns.add("d27");
        this.Columns.add("d28");
        this.Columns.add("d29");
        this.Columns.add("d30");
        this.Columns.add("d31");
    }

    /*
     * selectの実行
     */
    public void selectDb() throws Exception {
        ResultSet rset = this.STMT.executeQuery(this.Sql);
        //SELECT結果の受け取り
        while(rset.next()){
            String col = rset.getString("month");
            System.out.println("---- " + col + " ----" );
            for (String s: this.Columns) {
                col = rset.getString(s);
                System.out.println("* " + col);
            }
        }
        HashMap<String,String> map = new HashMap<String,String>();
    }

    public void close() throws Exception {
        this.CON.close();
    }
}

