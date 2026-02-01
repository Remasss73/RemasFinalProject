package remas.example.remasfinalproject.data.Seeker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) for the Seekers table.
 * This interface defines the database interactions for user/seeker accounts
 * within the Remas application, including authentication and profile management.
 */
@Dao
public interface SeekerQuery {

    /**
     * Inserts a new Seeker into the database.
     *
     * @param seeker The Seeker object to be saved.
     */
    @Insert
    void insert(Seekers seeker);

    /**
     * Inserts multiple Seekers into the database.
     *
     * @param users Varargs list of Seekers to insert.
     */
    @Insert
    void insertAll(Seekers... users);

    /**
     * Retrieves all Seekers currently registered in the database.
     *
     * @return A list of all Seeker records.
     */
    @Query("SELECT * FROM Seekers")
    List<Seekers> getAll();

    /**
     * Loads specific Seekers based on an array of primary key IDs.
     *
     * @param userIds Array of IDs to search for.
     * @return A list of Seekers matching the provided IDs.
     */
    @Query("SELECT * FROM seekers WHERE keyid IN (:userIds)")
    List<Seekers> loadAllByIds(int[] userIds);

    /**
     * Authenticates a user by checking if the email and password match a record.
     * This is used during the Login process.
     *
     * @param myEmail    The email entered by the user.
     * @param myPassword The password entered by the user.
     * @return The matching Seeker object if found, or null if credentials are invalid.
     */
    @Query("SELECT * FROM seekers WHERE email = :myEmail AND password = :myPassword LIMIT 1")
    Seekers checkEmailPassword(String myEmail, String myPassword);

    /**
     * Checks if an email is already registered in the system.
     * This is used during the Registration process to prevent duplicate accounts.
     *
     * @param myEmail The email to verify.
     * @return The Seeker object if the email exists, or null if it is available.
     */
    @Query("SELECT * FROM seekers WHERE email = :myEmail LIMIT 1")
    Seekers checkEmail(String myEmail);

    /**
     * Updates an existing Seeker's information in the database.
     *
     * @param values One or more Seekers to update.
     */
    @Update
    void update(Seekers... values);

    /**
     * Deletes a specific Seeker object from the database.
     *
     * @param user The Seeker object to delete.
     */
    @Delete
    void delete(Seekers user);

    /**
     * Deletes a Seeker record directly using their unique ID.
     *
     * @param id The primary key (keyid) of the Seeker to remove.
     */
    @Query("DELETE FROM seekers WHERE keyid = :id")
    void deleteById(int id);
}
