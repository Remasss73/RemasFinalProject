package remas.example.remasfinalproject;

import java.util.List;

/**
 * فئة تمثل عنصر العقار (Listing): تستخدم كنموذج بيانات لتخزين معلومات العقارات المسترجعة من Firebase.
 */
public class ListingItem {
    // الحقول التي تطابق هيكلية قاعدة البيانات
    private String listingId;
    private String title;
    private String price;
    private String location;
    private String city;
    private String areaName; // اسم الحي أو المنطقة
    private String description;
    private String imageUrl;
    private String userId;
    private String status;
    private int bedrooms;
    private int bathrooms;
    private int propertySize; // المساحة بالمتر المربع
    private long timestamp;
    private List<String> amenities;

    /**
     * مشيد فارغ ضروري لعمل مكتبة Firebase بشكل صحيح عند تحويل البيانات.
     */
    public ListingItem() {
    }

    /**
     * الحصول على المعرف الفريد للعقار.
     * @return معرف العقار.
     */
    public String getListingId() { return listingId; }

    /**
     * تعيين المعرف الفريد للعقار.
     * @param listingId معرف العقار الجديد.
     */
    public void setListingId(String listingId) { this.listingId = listingId; }

    /**
     * الحصول على عنوان العقار.
     * @return عنوان العقار.
     */
    public String getTitle() { return title; }

    /**
     * تعيين عنوان العقار.
     * @param title العنوان الجديد.
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * الحصول على سعر العقار.
     * @return السعر كنص.
     */
    public String getPrice() { return price; }

    /**
     * تعيين سعر العقار.
     * @param price السعر الجديد.
     */
    public void setPrice(String price) { this.price = price; }

    /**
     * الحصول على الموقع الجغرافي أو اسم المنطقة.
     * @return الموقع.
     */
    public String getLocation() { return location; }

    /**
     * تعيين الموقع الجغرافي.
     * @param location الموقع الجديد.
     */
    public void setLocation(String location) { this.location = location; }

    /**
     * الحصول على مساحة العقار بالمتر المربع.
     * @return المساحة.
     */
    public int getPropertySize() { return propertySize; }

    /**
     * تعيين مساحة العقار.
     * @param propertySize المساحة الجديدة.
     */
    public void setPropertySize(int propertySize) { this.propertySize = propertySize; }

    /**
     * الحصول على عدد غرف النوم.
     * @return عدد الغرف.
     */
    public int getBedrooms() { return bedrooms; }

    /**
     * تعيين عدد غرف النوم.
     * @param bedrooms العدد الجديد.
     */
    public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }

    /**
     * الحصول على عدد دورات المياه.
     * @return عدد دورات المياه.
     */
    public int getBathrooms() { return bathrooms; }

    /**
     * تعيين عدد دورات المياه.
     * @param bathrooms العدد الجديد.
     */
    public void setBathrooms(int bathrooms) { this.bathrooms = bathrooms; }

    /**
     * الحصول على وصف العقار.
     * @return الوصف النصي.
     */
    public String getDescription() { return description; }

    /**
     * تعيين وصف العقار.
     * @param description الوصف الجديد.
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * الحصول على معرف المستخدم الذي قام بنشر العقار.
     * @return معرف المستخدم.
     */
    public String getUserId() { return userId; }

    /**
     * تعيين معرف المستخدم الناشر.
     * @param userId معرف المستخدم.
     */
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * الحصول على قائمة الخدمات المتاحة (مثل واي فاي، تكييف).
     * @return قائمة الخدمات.
     */
    public List<String> getAmenities() { return amenities; }

    /**
     * تعيين قائمة الخدمات المتاحة.
     * @param amenities قائمة الخدمات الجديدة.
     */
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    /**
     * دالة مساعدة للحصول على الحجم (تحتاج لمراجعة منطقية حيث أنها تستدعي نفسها حالياً).
     * @return مصفوفة أحرف (كمثال).
     */
    public char[] getSize() {
        return "N/A".toCharArray();
    }
}
