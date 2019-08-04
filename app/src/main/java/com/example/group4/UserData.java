package com.example.group4;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;

@Entity(foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "id", childColumns = "userId"))
@TypeConverters(DataConverter.class)
public class UserData {
    @PrimaryKey
    public int timeStamp;

    public int userId;

    public double[] x_values;
    public double[] y_values;
    public double[] z_values;
}