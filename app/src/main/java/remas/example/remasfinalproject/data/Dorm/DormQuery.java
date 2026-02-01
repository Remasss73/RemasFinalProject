package remas.example.remasfinalproject.data.Dorm;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) for the Dorms table.
 * This interface defines the database operations for managing dormitory listings
 * in the Remas application, including adding, retrieving, and deleting properties.
 */
@Dao
public interface DormQuery {

    /**
     * Inserts a new Dorm listing into the database.
     *
     * @param dorm The Dorms object containing the property details to be saved.
     */
    @Insert
    void insert(Dorms dorm);

    /**
     * Inserts multiple Dorm listings into the database at once.
     *
     * @param dorms Varargs list of Dorms objects to insert.
     */
    @Insert
    void insertAll(Dorms... dorms);

    /**
     * Retrieves all Dorm listings currently stored in the database.
     *
     * @return A List of all Dorms objects found in the "dorms" table.
     */
    @Query("SELECT * FROM dorms")
    List<Dorms> getAll();

    /**
     * Loads specific Dorm listings based on an array of primary key IDs.
     *
     * @param dormIds Array of unique IDs (keyId) to search for.
     * @return A List of Dorms matching the provided IDs.
     */
    @Query("SELECT * FROM dorms WHERE keyid IN (:dormIds)")
    List<Dorms> loadAllByIds(int[] dormIds);

    /**
     * Updates the information of one or more existing Dorm listings.
     *
     * @param values The Dorms objects with updated data to be saved.
     */
    @Update
    void update(Dorms... values);

    /**
     * Deletes a specific Dorm listing object from the database.
     *
     * @param dorm The Dorms object to be removed.
     */
    @Delete
    void delete(Dorms dorm);

    /**
     * Deletes a specific Dorm listing from the database using its unique ID.
     *
     * @param id The primary key ID (keyid) of the property to remove.
     */
    @Query("DELETE FROM dorms WHERE keyid = :id")
    void deleteById(int id);
}




