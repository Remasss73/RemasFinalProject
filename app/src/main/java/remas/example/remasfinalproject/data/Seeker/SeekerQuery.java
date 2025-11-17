package remas.example.remasfinalproject.data.Seeker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface SeekerQuery {
    List<Seekers> getAll();

    List<Seekers> loadAllByIds(int[] userIds);


    Seekers checkEmailPassword(String myFullName,String myEmail, String myPassword);

    Seekers checkAge(Integer myAge);
    Seekers checkCity(String myCity);




    void insertAll(Seekers... users);

    void delete(Seekers user);

    void delete(int id);

    void insert(Seekers myUser);

    void update(Seekers... values);

    @Dao
    public interface MyLister {
        @Query("SELECT * FROM Seekers")
        List<Seekers> getAll();

        @Query("SELECT * FROM seekers WHERE keyid IN (:userIds)")
        List<Seekers> loadAllByIds(int[] userIds);

        @Query("SELECT * FROM seekers WHERE email = :myEmail AND password = :myPassword LIMIT 1")
        Seekers checkEmailPassword(String myEmail, String myPassword);

        @Query("SELECT * FROM seekers WHERE email = :myEmail LIMIT 1")
        Seekers checkEmail(String myEmail);

        @Insert
        void insertAll(Seekers... users);

        @Delete
        void delete(Seekers user);

        @Query("Delete From seekers WHERE keyid=:id ")
        void delete(int id);

        @Insert
        void insert(Seekers myUser);

        @Update
        void update(Seekers... values);
    }
}

