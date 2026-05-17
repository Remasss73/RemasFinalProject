package remas.example.remasfinalproject;

import java.util.List;

/**
 * This class represents a single listing (like an apartment).
 * It's a "Data Model" used to store information from Firebase.
 */
public class ListingItem {
    // Fields matching your database structure
    private String listingId;
    private String title;
    private String price;
    private String location;
    private String city;
    private String areaName; // Neighborhood name
    private String description;
    private String imageUrl;
    private String userId;
    private String status;
    private int bedrooms;
    private int bathrooms;
    private int propertySize; // Size in m²
    private long timestamp;
    private List<String> amenities;

    /**
     * Empty constructor required by Firebase.
     */
    public ListingItem() {
    }

    // Getters and Setters - allow reading and writing data safely
    public String getListingId() { return listingId; }
    public void setListingId(String listingId) { this.listingId = listingId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getPropertySize() { return propertySize; }
    public void setPropertySize(int propertySize) { this.propertySize = propertySize; }

    public int getBedrooms() { return bedrooms; }
    public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }

    public int getBathrooms() { return bathrooms; }
    public void setBathrooms(int bathrooms) { this.bathrooms = bathrooms; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    public char[] getSize() {
        return getSize();
    }
}
