package remas.example.remasfinalproject.data.Dorm;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "dorms")
public class Dorms implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public long keyId;

    public String city;
    public String address;
    public String zipcode;
    public String description;
    public String rent;
    public String amenities;
    public String photos;
    public String status;

    // Default Constructor
    public Dorms() {
    }

    // Getters and Setters
    public long getKeyId() { return keyId; }
    public void setKeyId(long keyId) { this.keyId = keyId; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getZipcode() { return zipcode; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRent() { return rent; }
    public void setRent(String rent) { this.rent = rent; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public String getPhotos() { return photos; }
    public void setPhotos(String photos) { this.photos = photos; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Dorms{" +
                "keyId=" + keyId +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", rent='" + rent + '\'' +
                '}';
    }

    /**
     * FIXED HELPER METHODS FOR THE ADAPTER
     * These map your database fields (rent, city, etc) to the names used in your UI Adapter.
     */

    public String getPrice() {
        // Returns the rent value. We add "$" for display.
        return rent != null ? "$" + rent : "$0";
    }

    public String getTitle() {
        // Returns the city name as the main title of the card.
        return city != null ? city : "Unknown Location";
    }

    public String getBeds() {
        // Maps amenities string to the "Beds" display area.
        return amenities != null ? amenities : "N/A";
    }

    public String getBaths() {
        // Placeholder as your table doesn't have a specific bath column yet.
        return "1 Bath";
    }

    public String getOwnerName() {
        // Placeholder for the status or a generic owner name.
        return status != null ? status : "Available";
    }
}

