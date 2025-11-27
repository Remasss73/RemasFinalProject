package remas.example.remasfinalproject.data.Seeker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Entity
@Dao
public interface SeekerQuery {
        /**
         * Insert a Seeker into the database.
         * @param seeker Seeker to insert.
         */
@Insert
    void insert(Seekers seeker);
/**
 * Get all Seekers in the database.
 * @return List of Seekers.
 */
@Query("SELECT * FROM Seekers")
List<Seekers> getAll();
/**
 * Load all Seekers by their user Ids.
 * @param userIds User IDs to load.
 * @return List of Seekers.
 */
@Query("SELECT * FROM seekers WHERE keyid IN (:userIds)")
List<Seekers> loadAllByIds(int[] userIds);
    public interface MyLister {

        @Query("SELECT * FROM seekers WHERE keyid IN (:userIds)")
        List<Seekers> loadAllByIds(int[] userIds);

        /**
         * Checks if a Seeker with the given email and password exists.
         * @param myEmail The email to check.
         * @param myPassword The password to check.
         * @return The Seeker with the given email and password, or null if none exists.
         */
        @Query("SELECT * FROM seekers WHERE email = :myEmail AND password = :myPassword LIMIT 1")
        Seekers checkEmailPassword(String myEmail, String myPassword);

        /**
         * Checks if a Seeker with the given email exists.
         * @param myEmail The email to check.
         * @return The Seeker with the given email, or null if none exists.
         */
        @Query("SELECT * FROM seekers WHERE email = :myEmail LIMIT 1")
        Seekers checkEmail(String myEmail);

        /**
         * Insert multiple Seekers into the database.
         * @param users Seekers to insert.
         */
        @Insert
        void insertAll(Seekers... users);

        /**
         * Deletes a Seeker from the database.
         * @param user Seeker to delete.
         */
        @Delete
        void delete(Seekers user);

        /**
         * Deletes a Seeker with the given ID from the database.
         * @param id The ID of the Seeker to delete.
         */
        @Query("Delete From seekers WHERE keyid=:id ")
        void delete(int id);


        @Insert
       // void insert(Seekers myUser);

        /**
         * Updates multiple Seekers in the database.
         * @param values Seekers to update.
         */
        @Update
        void update(Seekers... values);
    }
}

