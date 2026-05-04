package remas.example.remasfinalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    
    // UI Components
    private Toolbar toolbar;
    private MaterialButton btnEditProfile, btnChangePassword, btnDeleteAccount;
    private MaterialButton btnNotifications, btnPrivacy, btnTerms, btnLanguage;
    private MaterialButton btnHelpCenter, btnContactUs, btnFeedback, btnReportBug;
    private MaterialButton btnAboutApp, btnRateApp, btnShareApp;
    private CircularProgressIndicator progressIndicator;
    
    // Firebase & Preferences
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LuxeStayPrefs";
    private static final String LANGUAGE_KEY = "selected_language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize UI components
        initializeViews();
        setupToolbar();
        setupClickListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        
        // Account Settings
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        
        // App Settings
        btnNotifications = findViewById(R.id.btnNotifications);
        btnPrivacy = findViewById(R.id.btnPrivacy);
        btnTerms = findViewById(R.id.btnTerms);
        btnLanguage = findViewById(R.id.btnLanguage);
        
        // Support
        btnHelpCenter = findViewById(R.id.btnHelpCenter);
        btnContactUs = findViewById(R.id.btnContactUs);
        btnFeedback = findViewById(R.id.btnFeedback);
        btnReportBug = findViewById(R.id.btnReportBug);
        
        // App Info
        btnAboutApp = findViewById(R.id.btnAboutApp);
        btnRateApp = findViewById(R.id.btnRateApp);
        btnShareApp = findViewById(R.id.btnShareApp);
        
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
        // Account Settings
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, Profile.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());

        // App Settings
        btnNotifications.setOnClickListener(v -> openNotificationSettings());

        btnPrivacy.setOnClickListener(v -> openPrivacyPolicy());

        btnTerms.setOnClickListener(v -> openTermsOfService());

        btnLanguage.setOnClickListener(v -> showLanguageSelectionDialog());

        // Support
        btnHelpCenter.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, HelpActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnContactUs.setOnClickListener(v -> openContactEmail());

        btnFeedback.setOnClickListener(v -> openFeedbackEmail());

        btnReportBug.setOnClickListener(v -> openBugReportEmail());

        // App Info
        btnAboutApp.setOnClickListener(v -> showAboutDialog());

        btnRateApp.setOnClickListener(v -> {
            showLoadingState(true);
            // TODO: Open app store for rating
            Toast.makeText(this, "Rate app feature coming soon", Toast.LENGTH_SHORT).show();
            showLoadingState(false);
        });

        btnShareApp.setOnClickListener(v -> shareApp());
    }

    private void showChangePasswordDialog() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Change Password")
                    .setMessage("A password reset link will be sent to your email: " + user.getEmail())
                    .setPositiveButton("Send Reset Link", (dialog, which) -> {
                        mAuth.sendPasswordResetEmail(user.getEmail())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SettingsActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SettingsActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void showDeleteAccountDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    showLoadingState(true);
                    // TODO: Implement account deletion
                    mAuth.getCurrentUser().delete()
                            .addOnCompleteListener(task -> {
                                showLoadingState(false);
                                if (task.isSuccessful()) {
                                    Toast.makeText(SettingsActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                    // Navigate to login screen
                                    Intent intent = new Intent(SettingsActivity.this, SignIn.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SettingsActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("About LUXE STAY")
                .setMessage("LUXE STAY - Premium Accommodations\n\nVersion 1.0\n\nFind your perfect living space with our premium dormitory and apartment listings.\n\n© 2024 LUXE STAY. All rights reserved.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLanguageSelectionDialog() {
        String[] languages = {"English", "العربية", "עברית"};
        String[] languageCodes = {"en", "ar", "he"};
        
        String currentLanguage = getCurrentLanguage();
        int currentSelection = 0;
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLanguage)) {
                currentSelection = i;
                break;
            }
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Select Language")
                .setSingleChoiceItems(languages, currentSelection, (dialog, which) -> {
                    String selectedLanguage = languageCodes[which];
                    setLanguage(selectedLanguage);
                    dialog.dismiss();
                })
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String getCurrentLanguage() {
        return sharedPreferences.getString(LANGUAGE_KEY, "en");
    }

    private void setLanguage(String languageCode) {
        sharedPreferences.edit()
                .putString(LANGUAGE_KEY, languageCode)
                .apply();

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        recreate();
    }

    private void openNotificationSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
    }

    private void openPrivacyPolicy() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://luxestay.com/privacy"));
        startActivity(intent);
    }

    private void openTermsOfService() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://luxestay.com/terms"));
        startActivity(intent);
    }

    private void openContactEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:support@luxestay.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "LUXE STAY - Contact Us");
        startActivity(intent);
    }

    private void openFeedbackEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:feedback@luxestay.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "LUXE STAY - Feedback");
        startActivity(intent);
    }

    private void openBugReportEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:bugs@luxestay.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "LUXE STAY - Bug Report");
        startActivity(intent);
    }

    private void openAppStoreForRating() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        startActivity(intent);
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "LUXE STAY - Find Your Perfect Stay");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out LUXE STAY - The best app for finding accommodation! Download now: https://play.google.com/store/apps/details?id=" + getPackageName());
        startActivity(Intent.createChooser(shareIntent, "Share LUXE STAY"));
    }

    private void showLoadingState(boolean show) {
        if (progressIndicator != null) {
            progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
