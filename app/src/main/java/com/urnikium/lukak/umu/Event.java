package com.urnikium.lukak.umu;

import com.google.gson.annotations.SerializedName;

import java.util.*;

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
