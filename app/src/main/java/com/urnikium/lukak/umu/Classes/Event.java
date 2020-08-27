package com.urnikium.lukak.umu.Classes;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {
    @SerializedName("DayOfWeek")
    private int dayOfWeek;

    @SerializedName("BeginWeek")
    private int beginWeek;

    @SerializedName("EndWeek")
    private int endWeek;

    @SerializedName("StartTime")
    private String startTime;

    private String endTime;

    @SerializedName("Duration")
    private int duration;

    @SerializedName("Type")
    private String type;

    @SerializedName("Course")
    private String course;

    @SerializedName("Room")
    private String room;

    @SerializedName("Professor")
    private String professor;

    @SerializedName("Group")
    private Group group;

}

