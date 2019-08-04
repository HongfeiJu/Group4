package com.example.group4;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {User.class, UserData.class}, version = 1)
@TypeConverters(DataConverter.class)
abstract class PatientDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract UserDataDao userDataDao();
}
