package com.example.group4;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    public int id;

    public String name;
    public String sex;
    public int age;
}
