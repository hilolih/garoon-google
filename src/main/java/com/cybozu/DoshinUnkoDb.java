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

public class DoshinUnkoDb {
	private Connection CON;
	private Statement STMT;
    private ArrayList<String> Columns = new ArrayList<String>();

    DoshinUnkoDb(String url, String account, String pass, String ncode) throws Exception {
        Class.forName("org.postgresql.Driver");

        //PostgreSQLへ接続
        this.CON = DriverManager.getConnection(url, account, pass);
        //SELECT文の実行
        this.STMT = this.CON.createStatement();
        String sql = "select * from daiya where ncode = '" + ncode + "';";
        ResultSet rset = this.STMT.executeQuery(sql);
        //SELECT結果の受け取り
        while(rset.next()){
            String col = rset.getString("d3");
            System.out.println(col);
        }
        HashMap<String,String> map = new HashMap<String,String>();


    }

    public void close() throws Exception{
        this.CON.close();
    }
}

