package remas.example.remasfinalproject.data;

import android.content.Context;

import androidx.room.RoomDatabase;

import remas.example.remasfinalproject.data.MyTasksTable.MyTask;
import remas.example.remasfinalproject.data.MyTasksTable.MyTaskQuery;
import remas.example.remasfinalproject.data.Seeker.Seekers;

public class AppDatabase
{
    @AppDatabase(entities = {Seekers.class, MyTask.class}, version = 1)
    public abstract class AppDatabase extends RoomDatabase{
        private static AppDatabase db;
        public abstract SeekersQuery getSeekersQuery();
        public abstract MyTaskQuery getMyTaskQuery();
        public static AppDataBase getDB(Context context)
        {
            if(db==null)
            {
                db= Room.databaseBuilder
            }
        }
    }
}
