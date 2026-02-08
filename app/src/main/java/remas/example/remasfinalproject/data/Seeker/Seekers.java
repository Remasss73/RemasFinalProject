package remas.example.remasfinalproject.data.Seeker;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import remas.example.remasfinalproject.SignUp;

@Entity (tableName = "seekers")
public class Seekers {
    /**
     * Gets the KeyId.
     * @return the KeyId
     */
        @PrimaryKey(autoGenerate = true)
        public long KeyId;
        @ColumnInfo(name = "full_Name")

        public String fullName;
        public int age;
        @ColumnInfo(name = "city")
        public String city;

        public String email;
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


    public class Seeker {
        private String userId; // معرف فريد للمستخدم(يمكن أن يكون فارغًا في البداية)
        private String name;
        private String age;
        private String city;
        private String email;
        private String password;


        // دالة إنشاء افتراضية (مطلوبة بواسطة Firebase)
        public Seeker() {}


        public Seeker(String userId, String name, String age, String city, String email, String password) {
            this.userId = userId;
            this.name = name;
            this.age = age;
            this.city= city;
            this.email = email;
            this.password = password;
        }


        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }


        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getAge() { return age; }
        public void setAge(String age) { this.age = age; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }


        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
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

