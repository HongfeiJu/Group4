package com.example.group4;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

@Dao
@TypeConverters(DataConverter.class)
public interface UserDataDao {
    @Insert
    public void insertUserData(UserData... data);

    @Delete
    public void deleteUserData(UserData... data);

    @Query("SELECT * FROM UserData")
    public UserData[] loadAllUserData();

    @Query("SELECT * FROM UserData WHERE timeStamp = (SELECT max(timeStamp) FROM UserData) AND " +
            "userId = :id")
    public  UserData loadMostRecentUserData(int id);


}
