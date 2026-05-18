package remas.example.remasfinalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

/**
 * نشاط المساعدة والدعم: يوفر للمستخدم روابط للتواصل مع الدعم الفني، الأسئلة الشائعة، ودليل المستخدم.
 */
public class HelpActivity extends BaseActivity {
    
    // UI Components
    private Toolbar toolbar;
    private MaterialButton btnContactSupport, btnFAQ, btnUserGuide, btnVideoTutorials;
    private MaterialButton btnEmergencyContact, btnLiveChat, btnReportIssue;
    private CircularProgressIndicator progressIndicator;

    /**
     * يتم استدعاؤها عند إنشاء النشاط؛ تقوم بتهيئة الواجهة وإعداد شريط الأدوات ومستمعي النقرات.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initializeViews();
        setupToolbar();
        setupClickListeners();
    }

    /**
     * ربط متغيرات الكود بالعناصر المرئية في ملف التصميم.
     */
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        btnContactSupport = findViewById(R.id.btnContactSupport);
        btnFAQ = findViewById(R.id.btnFAQ);
        btnUserGuide = findViewById(R.id.btnUserGuide);
        btnVideoTutorials = findViewById(R.id.btnVideoTutorials);
        btnEmergencyContact = findViewById(R.id.btnEmergencyContact);
        btnLiveChat = findViewById(R.id.btnLiveChat);
        btnReportIssue = findViewById(R.id.btnReportIssue);
        progressIndicator = findViewById(R.id.progressIndicator);
    }

    /**
     * إعداد شريط العنوان وتفعيل زر الرجوع.
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * إعداد مستمعي النقرات لجميع أزرار الدعم والمساعدة.
     */
    private void setupClickListeners() {
        btnContactSupport.setOnClickListener(v -> openContactSupport());
        btnFAQ.setOnClickListener(v -> openFAQ());
        btnUserGuide.setOnClickListener(v -> openUserGuide());
        btnVideoTutorials.setOnClickListener(v -> openVideoTutorials());
        btnEmergencyContact.setOnClickListener(v -> openEmergencyContact());
        btnLiveChat.setOnClickListener(v -> openLiveChat());
        btnReportIssue.setOnClickListener(v -> openIssueReport());
    }

    /**
     * فتح تطبيق البريد الإلكتروني لإرسال طلب دعم فني.
     */
    private void openContactSupport() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:support@luxestay.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "LUXE STAY - Support Request");
        startActivity(intent);
    }

    /**
     * فتح رابط صفحة الأسئلة الشائعة في المتصفح.
     */
    private void openFAQ() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://luxestay.com/faq"));
        startActivity(intent);
    }

    /**
     * فتح رابط دليل المستخدم في المتصفح.
     */
    private void openUserGuide() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://luxestay.com/guide"));
        startActivity(intent);
    }

    /**
     * فتح رابط دروس الفيديو التعليمية في المتصفح.
     */
    private void openVideoTutorials() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://luxestay.com/tutorials"));
        startActivity(intent);
    }

    /**
     * فتح تطبيق البريد الإلكتروني لإرسال بلاغ طارئ.
     */
    private void openEmergencyContact() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:emergency@luxestay.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "LUXE STAY - EMERGENCY");
        startActivity(intent);
    }

    /**
     * فتح رابط الدردشة المباشرة في المتصفح.
     */
    private void openLiveChat() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://luxestay.com/chat"));
        startActivity(intent);
    }

    /**
     * فتح تطبيق البريد الإلكتروني للإبلاغ عن مشكلة تقنية.
     */
    private void openIssueReport() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:issues@luxestay.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "LUXE STAY - Issue Report");
        startActivity(intent);
    }

    /**
     * التحكم في إظهار أو إخفاء مؤشر التحميل.
     * @param show true للإظهار، false للإخفاء.
     */
    private void showLoadingState(boolean show) {
        if (progressIndicator != null) {
            progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
