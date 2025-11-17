package remas.example.remasfinalproject.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import remas.example.remasfinalproject.data.MyTasksTable.MyTask;
import remas.example.remasfinalproject.data.MyTasksTable.MyTaskQuery;
import remas.example.remasfinalproject.data.Seeker.SeekerQuery;
import remas.example.remasfinalproject.data.Seeker.Seekers;


@Database(entities = {Seekers.class, MyTask.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase db;

    public abstract SeekerQuery getSeekersQuery();
    public abstract MyTaskQuery getMyTaskQuery();

    public static AppDatabase getDB(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context,
                            AppDatabase.class,
                            "remasDataBase")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return db;
    }
}



