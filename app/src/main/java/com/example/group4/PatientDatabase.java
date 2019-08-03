package com.example.group4;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, UserData.class}, version = 1)
abstract class PatientDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract UserDataDao userDataDao();
}
