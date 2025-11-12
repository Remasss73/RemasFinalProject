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

        public long getKeyId() {
            return KeyId;
        }

        public void setKeyId(long keyId) {
            KeyId = keyId;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "MyUser{" +
                    "KeyId=" + KeyId +
                    ", fullName='" + fullName + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }
    }
