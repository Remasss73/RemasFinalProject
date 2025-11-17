package remas.example.remasfinalproject.data.Seeker;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "Seekers")
public class Seekers {


        @PrimaryKey(autoGenerate = true)
        public long KeyId;
        @ColumnInfo(name = "full_Name")
        public String fullName;
        public int age;
        @ColumnInfo(name = "city")
        public String city;
        public String email;
        public String password;

/* <<<<<<<<<<<<<<  ✨ Windsurf Command ⭐ >>>>>>>>>>>>>>>> */
        /**
         * Gets the KeyId.
         * @return the KeyId
         */
/* <<<<<<<<<<  150b1c9e-27da-4ff4-bf00-906f3d03154d  >>>>>>>>>>> */
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

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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
                    ", age=" + age +
                    ", city='" + city + '\'' +
                    ", email='" + email + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

