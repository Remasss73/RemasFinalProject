package remas.example.remasfinalproject.data.Seeker;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Seekers Model Class
 *
 * This class acts as both a Room Entity (Local DB) and a Firebase Model (Cloud DB).
 *
 * FIX EXPLANATION:
 * We use @Exclude on getKeyId() so Firebase ignores the local auto-incrementing
 * ID. This prevents the "conflicting case sensitivity" crash.
 */
@IgnoreExtraProperties
@Entity(tableName = "seekers")
public class Seekers {

    @PrimaryKey(autoGenerate = true)
    private long keyId;

    @ColumnInfo(name = "full_Name")
    private String fullName;

    private int age;

    @ColumnInfo(name = "city")
    private String city;

    private String email;
    private String password;
    private String userId;

    /**
     * Required empty constructor for Firebase DataSnapshot.getValue(Seekers.class)
     */
    public Seekers() {
    }

    // --- GETTERS AND SETTERS ---

    /**
     * @Exclude prevents Firebase from trying to map this field.
     * This stops the "keyid" case sensitivity crash.
     */
    @Exclude
    public long getKeyId() {
        return keyId;
    }

    /**
     * @Exclude prevents Firebase from trying to map this field.
     */
    @Exclude
    public void setKeyId(long keyId) {
        this.keyId = keyId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Seeker{" +
                "keyId=" + keyId +
                ", fullName='" + fullName + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}

