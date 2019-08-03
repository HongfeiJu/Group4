package com.example.group4;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.jjoe64.graphview.series.Series;

@Entity(foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "id", childColumns = "user_id"))
public class UserData {
    @PrimaryKey
    public int time_stamp;

    float[] x_values;
    float[] y_values;
    float[] z_values;
}