package remas.example.remasfinalproject.data.MyUserTable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class UsersTable {
    @Entity
    public class MyUser {
        @PrimaryKey(autoGenerate = true)
        public long KeyId;
        @ColumnInfo(name = "full_Name")
        public String fullName;
        public String email;
        public String phone;
        public String password;

        public MyUser() {
        }
    }
}
