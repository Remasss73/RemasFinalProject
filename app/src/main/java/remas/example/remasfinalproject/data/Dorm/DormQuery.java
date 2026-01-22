package remas.example.remasfinalproject.data.Dorm;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface DormQuery
{
   
        /**
         * Insert a Seeker into the database.
         * @param seeker Seeker to insert.
         */
        @Insert
        void insert(Dorms seeker);
        /**
         * Get all Dorms in the database.
         * @return List of Dorms.
         */
        @Query("SELECT * FROM Dorms")
        List<Dorms> getAll();
        /**
         * Load all Dorms by their user Ids.
         * @param userIds User IDs to load.
         * @return List of Dorms.
         */
        @Query("SELECT * FROM Dorms WHERE keyid IN (:userIds)")
        List<Dorms> loadAllByIds(int[] userIds);
        public interface MyLister {

            @Query("SELECT * FROM Dorms WHERE keyid IN (:userIds)")
            List<Dorms> loadAllByIds(int[] userIds);




            /**
             * Insert multiple Dorms into the database.
             * @param users Dorms to insert.
             */
            @Insert
            void insertAll(Dorms... users);

            /**
             * Deletes a Seeker from the database.
             * @param user Seeker to delete.
             */
            @Delete
            void delete(Dorms user);

            /**
             * Deletes a Seeker with the given ID from the database.
             * @param id The ID of the Seeker to delete.
             */
            @Query("Delete From Dorms WHERE keyid=:id ")
            void delete(int id);


            @Insert
            // void insert(Dorms myUser);

            /**
             * Updates multiple Dorms in the database.
             * @param values Dorms to update.
             */
            @Update
            void update(Dorms... values);
        }
    }




