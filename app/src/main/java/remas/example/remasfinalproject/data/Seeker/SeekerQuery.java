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
@Insert
    void insert(Seekers seeker);

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
       // void insert(Seekers myUser);

        @Update
        void update(Seekers... values);
    }
}

