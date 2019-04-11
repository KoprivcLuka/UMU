package com.urnikium.lukak.umu.Classes;

import com.google.gson.annotations.SerializedName;

public class Group {
    @SerializedName("Field")
    public String field;
    @SerializedName("Year")
    public long year;
    @SerializedName("Type")
    public String type;
    @SerializedName("SubGroup")
    public String subGroup;

}
