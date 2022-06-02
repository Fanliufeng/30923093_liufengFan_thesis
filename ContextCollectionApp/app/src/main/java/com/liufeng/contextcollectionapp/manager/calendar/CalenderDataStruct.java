package com.liufeng.contextcollectionapp.manager.calendar;


/**
 * 日历日程数据结构
 */
public class CalenderDataStruct {
    String eventTitle = "";
    String startTime = "";
    String endTime = "";
    String description = "";
    String location = "";
    String week = "";

    public CalenderDataStruct(String eventTitle, String startTime, String endTime, String description, String location,String week) {
        this.eventTitle = eventTitle;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.location = location;
        this.week = week;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    @Override
    public String toString() {
        String CalendarInfo = eventTitle + ", " + description;
        CalendarInfo = CalendarInfo.replaceAll("[^a-zA-Z0-9]"," ");
        return CalendarInfo;

        //return "Event Title: " + eventTitle + '\n' +
          //      "Start Time: " + startTime + '\n' +
            //    "End Time: " + endTime + '\n' +
              //  "Description: " + description + '\n' +
                //"Location: " + location + '\n';
    }
}
