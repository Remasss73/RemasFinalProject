package remas.example.remasfinalproject;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import remas.example.remasfinalproject.data.AppDatabase;
import remas.example.remasfinalproject.data.Dorm.Dorms;
import remas.example.remasfinalproject.data.Seeker.Seekers;

public class AddDormActivity extends AppCompatActivity {

    private EditText etCity;
    private EditText etAddress;
    private EditText etZipcode;
    private EditText etRent;
    private EditText etAmenities;
    private EditText etDescription;
    private EditText etStatus;
    private MaterialButton btnSaveDorm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dorm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
    }

    private void initializeViews() {
        etCity = findViewById(R.id.etCity);
        etAddress = findViewById(R.id.etAddress);
        etZipcode = findViewById(R.id.etZipcode);
        etRent = findViewById(R.id.etRent);
        etAmenities = findViewById(R.id.etAmenities);
        etDescription = findViewById(R.id.etDescription);
        etStatus = findViewById(R.id.etStatus);
        btnSaveDorm = findViewById(R.id.btnSaveDorm);

        btnSaveDorm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicListing();
            }
        });

    }


    public void onClick(View view) {

        Intent i = new Intent(AddDormActivity.this, DormsListActivity.class);
        startActivity(i);
    }


    private void AddListing() {
        // Get values from EditText fields
        String city = etCity.getText().toString();
        String address = etAddress.getText().toString();
        String zipcode = etZipcode.getText().toString();
        String rent = etRent.getText().toString();
        String amenities = etAmenities.getText().toString();
        String description = etDescription.getText().toString();
        String status = etStatus.getText().toString();


        // TODO: Implement save logic
    }

    private boolean validateInputs() {
        if (etCity.getText().toString().isEmpty()) {
            etCity.setError("City is required");
            return false;
        }
        if (etAddress.getText().toString().isEmpty()) {
            etAddress.setError("Address is required");
            return false;
        }
        if (etZipcode.getText().toString().isEmpty()) {
            etZipcode.setError("Zipcode is required");
            return false;
        }
        if (etRent.getText().toString().isEmpty()) {
            etRent.setError("Rent is required");
            return false;
        }
        if (etAmenities.getText().toString().isEmpty()) {
            etAmenities.setError("Amenities is required");
            return false;
        }
        if (etDescription.getText().toString().isEmpty()) {
            etDescription.setError("Description is required");
            return false;
        }
        if (etStatus.getText().toString().isEmpty()) {
            etStatus.setError("Status is required");
            return false;
        }
        return true;
    }

    private void PublicListing() {
        if (validateInputs()) {
            String City = etCity.getText().toString();
            String Address = etAddress.getText().toString();
            String Zipcode = etZipcode.getText().toString();
            String Rent = etRent.getText().toString();
            String Amenities = etAmenities.getText().toString();
            String Description = etDescription.getText().toString();
            String Status = etStatus.getText().toString();

            Dorms dorm = new Dorms();
            dorm.setCity(City);
            dorm.setAddress(Address);
            dorm.setZipcode(Zipcode);
            dorm.setRent(Rent);
            dorm.setAmenities(Amenities);
            dorm.setDescription(Description);
            dorm.setStatus(Status);

            AppDatabase db = AppDatabase.getDB(getApplication());
            db.getDormQuery().insert(dorm);

            String msg = "Listing Published" + City + Address + Zipcode + Rent + Amenities + Description + Status;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            finish();


        }
        // TODO: Implement validation logic
    }

    public void saveUser(Dorms dorm) {// الحصول على مرجع إلى عقدة "users" في قاعدة البيانات

        // تهيئة Firebase Realtime Database    //مؤشر لقاعدة البيانات
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
// ‏مؤشر لجدول المستعملين
        DatabaseReference usersRef = database.child("seekes");
        // إنشاء مفتاح فريد للمستخدم الجديد
        DatabaseReference newUserRef = usersRef.push();
        // تعيين معرف المستخدم في كائن MyUser
        dorm.setDormId(newUserRef.getKey());
        // حفظ بيانات المستخدم في قاعدة البيانات
        //اضافة كائن "لمجموعة" المستعملين ومعالج حدث لفحص نجاح المطلوب
        // معالج حدث لفحص هل تم المطلوب من قاعدة البيانات //
        newUserRef.setValue(dorm)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddDormActivity.this, "Succeeded to add User", Toast.LENGTH_SHORT).show();
                        finish();
                        // تم حفظ البيانات بنجاح
                        Log.d(TAG, "تم حفظ المستخدم بنجاح: " + dorm.getDormId());
                        // تحديث واجهة المستخدم أو تنفيذ إجراءات أخرى
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // معالجة الأخطاء
                        Log.e(TAG, "خطأ في حفظ المستخدم: " + e.getMessage(), e);
                        Toast.makeText(AddDormActivity.this, "Failed to add User", Toast.LENGTH_SHORT).show();
                        // عرض رسالة خطأ للمستخدم
                    }
                });
    }
}

