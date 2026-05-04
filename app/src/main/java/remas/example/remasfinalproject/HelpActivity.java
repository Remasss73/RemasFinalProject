package remas.example.remasfinalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class HelpActivity extends AppCompatActivity {
    
    // UI Components
    private Toolbar toolbar;
    private MaterialButton btnContactSupport, btnFAQ, btnUserGuide, btnVideoTutorials;
    private MaterialButton btnEmergencyContact, btnLiveChat, btnReportIssue;
    private CircularProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Initialize UI components
        initializeViews();
        setupToolbar();
        setupClickListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        
        // Contact Support
        btnContactSupport = findViewById(R.id.btnContactSupport);
        btnFAQ = findViewById(R.id.btnFAQ);
        btnUserGuide = findViewById(R.id.btnUserGuide);
        btnVideoTutorials = findViewById(R.id.btnVideoTutorials);
        
        // Quick Help
        btnEmergencyContact = findViewById(R.id.btnEmergencyContact);
        btnLiveChat = findViewById(R.id.btnLiveChat);
        btnReportIssue = findViewById(R.id.btnReportIssue);
        
        progressIndicator = findViewById(R.id.progressIndicator);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        // Contact Support
        btnContactSupport.setOnClickListener(v -> openContactSupport());

        btnFAQ.setOnClickListener(v -> openFAQ());

        btnUserGuide.setOnClickListener(v -> openUserGuide());

        btnVideoTutorials.setOnClickListener(v -> openVideoTutorials());

        // Quick Help
        btnEmergencyContact.setOnClickListener(v -> openEmergencyContact());

        btnLiveChat.setOnClickListener(v -> openLiveChat());

        btnReportIssue.setOnClickListener(v -> openIssueReport());
    }

    private void openContactSupport() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:support@luxestay.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "LUXE STAY - Support Request");
        startActivity(intent);
    }

    private void openFAQ() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://luxestay.com/faq"));
        startActivity(intent);
    }

    private void openUserGuide() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://luxestay.com/guide"));
        startActivity(intent);
    }

    private void openVideoTutorials() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://luxestay.com/tutorials"));
        startActivity(intent);
    }

    private void openEmergencyContact() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:emergency@luxestay.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "LUXE STAY - EMERGENCY");
        startActivity(intent);
    }

    private void openLiveChat() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://luxestay.com/chat"));
        startActivity(intent);
    }

    private void openIssueReport() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:issues@luxestay.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "LUXE STAY - Issue Report");
        startActivity(intent);
    }

    private void showLoadingState(boolean show) {
        if (progressIndicator != null) {
            progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
