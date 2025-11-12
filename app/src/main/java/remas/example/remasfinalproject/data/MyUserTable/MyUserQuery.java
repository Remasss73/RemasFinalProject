package remas.example.remasfinalproject.data.MyUserTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

public interface MyUserQuery {
    List<UsersTable> getAll();

    List<UsersTable> loadAllByIds(int[] userIds);


    UsersTable checkEmailPassword(String myEmail, String myPassword);


    UsersTable checkEmail(String myEmail);


    void insertAll(UsersTable... users);

    void delete(UsersTable user);

    void delete(int id);

    void insert(UsersTable myUser);

    void update(UsersTable... values);

    @Dao
    public interface MyUserQuery1 {
        @Query("SELECT * FROM MyUser")
        List<UsersTable> getAll();

        @Query("SELECT * FROM MyUser WHERE keyid IN (:userIds)")
        List<UsersTable> loadAllByIds(int[] userIds);

        @Query("SELECT * FROM MyUser WHERE email = :myEmail AND password = :myPassword LIMIT 1")
        UsersTable checkEmailPassword(String myEmail, String myPassword);

        @Query("SELECT * FROM MyUser WHERE email = :myEmail LIMIT 1")
        UsersTable checkEmail(String myEmail);

        @Insert
        void insertAll(UsersTable... users);

        @Delete
        void delete(UsersTable user);

        @Query("Delete From MyUser WHERE keyid=:id ")
        void delete(int id);

        @Insert
        void insert(UsersTable myUser);

        @Update
        void update(UsersTable... values);
    }
}

