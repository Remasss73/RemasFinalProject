package remas.example.remasfinalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AiActivity extends BaseActivity {
    private EditText etTaskTopic;
    private Button btnSuggestSteps;
    private ProgressBar pbLoading;
    private TextView tvAiResponse;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        etTaskTopic = findViewById(R.id.etTaskTopic);
        btnSuggestSteps = findViewById(R.id.btnSuggestSteps);
        pbLoading = findViewById(R.id.pbLoading);
        tvAiResponse = findViewById(R.id.tvAiResponse);
        
        // Setup button click listener
        btnSuggestSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = etTaskTopic.getText().toString().trim();
                if (topic.isEmpty()) {
                    Toast.makeText(AiActivity.this, "Please enter a task", Toast.LENGTH_SHORT).show();
                    return;
                }
                // For now, show a placeholder response
                // AI integration can be added later with proper dependencies
                showPlaceholderResponse(topic);
            }
        });
    }
    
    private void showPlaceholderResponse(String topic) {
        pbLoading.setVisibility(View.VISIBLE);
        tvAiResponse.setText("");
        btnSuggestSteps.setEnabled(false);
        
        // Simulate loading
        pbLoading.postDelayed(new Runnable() {
            @Override
            public void run() {
                pbLoading.setVisibility(View.GONE);
                btnSuggestSteps.setEnabled(true);
                String response = "AI Assistant is not yet configured.\n\n" +
                        "Task: " + topic + "\n\n" +
                        "To enable AI features, add the Firebase AI dependency to build.gradle:\n" +
                        "implementation 'com.google.firebase:firebase-ai:latest_version'";
                tvAiResponse.setText(response);
            }
        }, 1000);
    }
}