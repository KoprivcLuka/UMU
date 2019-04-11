package com.urnikium.lukak.umu.Classes;

import com.google.gson.annotations.SerializedName;

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

