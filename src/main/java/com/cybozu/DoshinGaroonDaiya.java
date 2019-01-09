/*
* garoon-google
* Copyright (c) 2015 Cybozu
*
* Licensed under the MIT License
*/
package com.cybozu;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.cybozu.garoon3.schedule.Span;
import com.cybozu.garoon3.schedule.Member;
import com.cybozu.garoon3.schedule.MemberType;
import com.cybozu.garoon3.schedule.ScheduleAddEvents;
import com.cybozu.garoon3.schedule.ScheduleModifyEvents;

/*
* DoshinGaroonDaiya
* Garoonに運行WEBのダイヤを登録する
*
*/
public class DoshinGaroonDaiya {

    private List<com.cybozu.garoon3.schedule.Event> GaroonSchedules;
    private String Keyword = "--- From Unkou Web ---";
    private ScheduleAddEvents AddEvents;
    private ScheduleModifyEvents ModifyEvents;

    DoshinGaroonDaiya () {
        this.GaroonSchedules = new ArrayList<com.cybozu.garoon3.schedule.Event>();
        this.AddEvents = new ScheduleAddEvents();
        this.ModifyEvents = new ScheduleModifyEvents();
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
        // 日付が一致するスケジュールを取得
        List<com.cybozu.garoon3.schedule.Event> list = this.getSameDateSchedule(date);

        if ( list.size() != 1 ) {
            System.err.println("[!] 同日に２つ以上の運行WEBダイヤがあります。処理をスキップします: " + date);
            // 同じイベントがあるようにみせて処理をスキップさせる
            return false;
        }

        // 要素数１つのリストに対し、タイトルが一致している場合はtrue、
        // 同じであればfalseを返す
        // System.out.println(list.stream().noneMatch(ev -> { return ev.getDetail().equals(daiya);}));
        // System.out.println( date );
        // list.stream().forEach( (ev) ->{ 
        //     Span span = ev.getSpans().get(0);
        //     Date start = span.getStart();
        //     System.out.println(start);
        // });
        return list.stream().noneMatch(ev -> {
            return ( ev.getDetail().equals(daiya) && this.compareDateAndHours(date, ev) );
        });
    }

    public void addNewEvent(Date date, String daiya, String garoonId, String garoonUsername) {
        com.cybozu.garoon3.schedule.Event event = new com.cybozu.garoon3.schedule.Event();

        event.setDetail(daiya);
        // スケジュールのタイトル左にあるタグ
        event.setPlan( this.getPlanTag(daiya) );
        // 時刻(終日がうまく動かないので、はじめと終わりの時間を一緒にしている）
        event.setSpans(this.daiyaSpans(date));
        //  "--- From Unkou Web ---" をメモ欄に
        event.setDescription(this.Keyword);

        event.setMembers( this.loginMember(garoonId, garoonUsername) );
        // System.out.println("----------------------------------------------------------------");
        // this.dump(event);
        this.AddEvents.addEvent( event );
    }

    public void addUpdateEvent(Date date, String daiya) {
        // 日付が一致するスケジュールを取得
        List<com.cybozu.garoon3.schedule.Event> list = this.getSameDateSchedule(date);
        // 要素数１つのリストに対し、ダイヤを更新して更新用リストに格納する
        list.forEach( ev -> {
            // this.dump(ev);
            // System.out.println("----------------------------------------------------------------");
            ev.setDetail( daiya );
            // スケジュールのタイトル左にあるタグ
            ev.setPlan( this.getPlanTag(daiya) );
            // 時刻(終日がうまく動かないので、はじめと終わりの時間を一緒にしている）
            ev.setSpans(this.daiyaSpans(date));
            
            this.dump(ev);
            this.ModifyEvents.addModifyEvent( ev );
        });

    }

    public ScheduleAddEvents getAddEvents() {
        return this.AddEvents;
    }

    public ScheduleModifyEvents getModifyEvents() {
        return this.ModifyEvents;
    }

    /*
     * Memberを作成する
     */
    private List<Member> loginMember(String id, String username) {
        Member m = new Member( MemberType.USER, Integer.parseInt(id), 0, username );
        List<Member> members = new ArrayList<Member>();
        members.add(m);
        return members;
    }

    /*
     * スケジュールの期間をセットする
     * ※ はじめと終わりの時間を一緒にしている
     *    8時間足すと夜勤が見づらくなるので。
     */
    private List<Span> daiyaSpans(Date date) {
        // Date -> LocalDateTime
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        // LocalDateTime -> Date(９時間戻す）
        Date d = Date.from(ldt.minusHours(9).atZone(ZoneId.systemDefault()).toInstant());

        List<Span> ss = new ArrayList<Span>();
        Span s = new Span();
        s.setStart( d );
        s.setEnd( d );

        ss.add(s);
        return ss;
    }

    /*
     * Eventの日付と運行WEBダイヤの日付を比較
     */
    private Boolean compareDate(Date date, com.cybozu.garoon3.schedule.Event ev) {
        if (date == null) {
            return false;
        }
        Span span = ev.getSpans().get(0);
        if (span == null) {
            return false;
        }
        // 運行WEBのダイヤ日時から時分秒を削ったもの
        LocalDateTime ldt_devsrv = 
            LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS);
        // garoonのダイヤ日時から時分秒を削ったもの
        LocalDateTime ldt_garoon = 
            LocalDateTime.ofInstant(span.getStart().toInstant(), ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS);
        return ldt_devsrv.compareTo( ldt_garoon ) == 0;
    }

    /*
     * Eventの日付と運行WEBダイヤの日付を比較(時刻まで）
     */
    private Boolean compareDateAndHours(Date date, com.cybozu.garoon3.schedule.Event ev) {
        if (date == null) {
            return false;
        }
        Span span = ev.getSpans().get(0);
        if (span == null) {
            return false;
        }
        return date.compareTo(span.getStart()) == 0;
    }

    /*
     * 日付が一致するスケジュールを取得
     */
    private List<com.cybozu.garoon3.schedule.Event> getSameDateSchedule(Date date) {
        return this.GaroonSchedules.stream()
           .filter(ev -> { return this.compareDate(date, ev); })
           .collect(Collectors.toList());
    }

    /*
     * ダイヤの種類の応じてタグ名を変更する
     */
    private String getPlanTag(String daiya) {
        // スケジュールのタイトル左にあるタグ
        if ( daiya.equals("【休】") ) {
            return "休み";
        } else if ( daiya.indexOf("00") >= 0 ) {
            // 8:00などの00にマッチしたら当番
            return "当番";
        } else {
            // その他、Fや組合、出張など
            return "出勤";
        }
    }
    /*
     *
     */
    private void dump(com.cybozu.garoon3.schedule.Event event) {
        Span span = event.getSpans().get(0);
        Date start = span.getStart();
        Date end = span.getEnd();

        System.out.println( "予定ID( Id ）: " + event.getId() );
        System.out.println( "タイトル( title ）: " + event.getDetail() );
        System.out.println( "タグ( plan ）: " + event.getPlan() );
        System.out.println( "メモ( description ）: " + event.getDescription() );
        System.out.println( "タイムゾーン( timezone ）: " + event.getTimezone() );
        System.out.println( "期間( Span ）: " + start + " to " + end );
        System.out.println( "終日( Allday ）: " + event.isAllDay() );
        System.out.println( "開始のみ( StartOnly ）: " + event.isStartOnly() );
        event.getMembers().forEach( member -> {
            System.out.println( "メンバーID : " + member.getID() );
            System.out.println( "メンバーName : " + member.getName() );
            System.out.println( "メンバーType : " + member.getType() );
            System.out.println( "順序？ : " + Integer.toString(member.getOrder()) );
        });
    }

}

