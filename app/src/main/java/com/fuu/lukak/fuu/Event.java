package com.fuu.lukak.fuu;

import com.google.gson.annotations.SerializedName;

import java.util.*;

class All {
    //Tak morm napisat ker ne znamo vracat arrayev JESENOVC
    private List<Event> Year1;
    private List<Event> Year2;
    private List<Event> Year3;
    private List<Event> Year4;
    private List<Event> Year5;

    @SerializedName("1")
    public List<Event> getYear1() {
        return Year1;
    }

    @SerializedName("1")
    public void setYear1(List<Event> value) {
        this.Year1 = value;
    }

    @SerializedName("2")
    public List<Event> getYear2() {
        return Year2;
    }

    @SerializedName("2")
    public void setYear2(List<Event> value) {
        this.Year2 = value;
    }

    @SerializedName("3")
    public List<Event> getYear3() {
        return Year3;
    }

    @SerializedName("3")
    public void setYear3(List<Event> value) {
        this.Year3 = value;
    }

    @SerializedName("4")
    public List<Event> getYear4() {
        return Year4;
    }

    @SerializedName("4")
    public void setYear4(List<Event> value) {
        this.Year4 = value;
    }

    @SerializedName("5")
    public List<Event> getYear5() {
        return Year5;
    }

    @SerializedName("5")
    public void setYear5(List<Event> value) {
        this.Year5 = value;
    }
}

public class Event {
    @SerializedName("DayOfWeek")
    public int dayOfWeek;

    @SerializedName("BeginWeek")
    public int beginWeek;

    @SerializedName("EndWeek")
    public int endWeek;

    @SerializedName("StartTime")
    public String startTime;

    @SerializedName("Duration")
    public int duration;

    @SerializedName("Type")
    public String type;

    @SerializedName("Course")
    public String course;

    @SerializedName("Room")
    public String room;

    @SerializedName("Professor")
    public String professor;

    @SerializedName("Group")
    public Group group;

}

class Group {
    @SerializedName("Field")
    public String field;
    @SerializedName("Year")
    public long year;
    @SerializedName("Type")
    public String type;
    @SerializedName("SubGroup")
    public String subGroup;

}
