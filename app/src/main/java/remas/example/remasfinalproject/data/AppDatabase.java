package remas.example.remasfinalproject.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import remas.example.remasfinalproject.data.Dorm.DormQuery;
import remas.example.remasfinalproject.data.Dorm.Dorms;
import remas.example.remasfinalproject.data.Seeker.SeekerQuery;
import remas.example.remasfinalproject.data.Seeker.Seekers;


@Database(entities = {Seekers.class, Dorms.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase db;

    public abstract SeekerQuery getSeekersQuery();
    public abstract DormQuery getDormQuery();

    /**
     * Returns the single instance of the application's database.
     * Creates the database if it doesn't exist yet.
     *
     * @param context The application context needed to create the database
     * @return The AppDatabase instance for this application
     */
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
