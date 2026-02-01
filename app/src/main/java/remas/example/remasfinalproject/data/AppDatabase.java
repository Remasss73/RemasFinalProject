package remas.example.remasfinalproject.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import remas.example.remasfinalproject.data.Dorm.DormQuery;
import remas.example.remasfinalproject.data.Dorm.Dorms;
import remas.example.remasfinalproject.data.Seeker.SeekerQuery;
import remas.example.remasfinalproject.data.Seeker.Seekers;

/**
 * Main Database class for the Remas application.
 * This class serves as the main access point for the Room database,
 * connecting the Seekers and Dorms entities to their respective DAOs.
 */
@Database(entities = {Seekers.class, Dorms.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Singleton instance of the database to prevent multiple instances being open at once.
     */
    private static AppDatabase db;

    /**
     * Provides access to the Data Access Object (DAO) for Seeker-related operations.
     *
     * @return The SeekerQuery DAO implementation.
     */
    public abstract SeekerQuery getSeekersQuery();

    /**
     * Provides access to the Data Access Object (DAO) for Dorm-related operations.
     *
     * @return The DormQuery DAO implementation.
     */
    public abstract DormQuery getDormQuery();

    /**
     * Returns the single instance of the application's database.
     * Uses a synchronized block to ensure thread safety during database creation.
     * Includes fallbackToDestructiveMigration to handle schema changes gracefully
     * and allowMainThreadQueries for simplified background execution during testing.
     *
     * @param context The application context needed to build the database.
     * @return The singleton AppDatabase instance.
     */
    public static AppDatabase getDB(Context context) {
        if (db == null) {
            synchronized (AppDatabase.class) {
                if (db == null) {
                    db = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class,
                                    "remasDataBase")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return db;
    }
}