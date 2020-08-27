package com.urnikium.lukak.umu.Classes;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Group {
    @SerializedName("Field")
    private String field;
    @SerializedName("Year")
    private long year;
    @SerializedName("Type")
    private String type;
    @SerializedName("SubGroup")
    private String subGroup;

}
