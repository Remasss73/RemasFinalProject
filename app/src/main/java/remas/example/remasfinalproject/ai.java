package remas.example.remasfinalproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI Chat Activity: Provides step-by-step college and dorm recommendations
 * Conversation flow:
 * 1. AI greeting
 * 2. User provides city
 * 3. User provides major
 * 4. AI recommends colleges
 * 5. AI suggests dorms
 */
public class ai extends AppCompatActivity {
    private EditText etMessage;
    private ImageView ivSend;
    private ImageView ivBack;
    private ProgressBar pbLoading;
    private RecyclerView rvChatMessages;
    private ChatAdapter chatAdapter;
    
    // Conversation state
    private int conversationStep = 0;
    private String userCity = "";
    private String userMajor = "";
    
    // College database (simulated)
    private Map<String, List<College>> collegeDatabase;
    
    // Dorm database (simulated)
    private List<DormInfo> dormDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);
        
        // Initialize databases
        initializeCollegeDatabase();
        initializeDormDatabase();
        
        // Initialize UI components
        etMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);
        ivBack = findViewById(R.id.ivBack);
        pbLoading = findViewById(R.id.pbLoading);
        rvChatMessages = findViewById(R.id.rvChatMessages);
        
        // Setup RecyclerView
        chatAdapter = new ChatAdapter();
        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        rvChatMessages.setAdapter(chatAdapter);
        
        // Setup click listeners
        ivBack.setOnClickListener(v -> finish());
        ivSend.setOnClickListener(v -> sendMessage());
        
        // Enable edge-to-edge
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Start conversation with greeting
        startConversation();
    }

    /**
     * Start the conversation with an AI greeting.
     */
    private void startConversation() {
        conversationStep = 0;
        ChatMessage greeting = new ChatMessage(
            "🎓 Welcome to LUXE STAY AI!\n\n" +
            "I'm here to help you find the perfect college and dormitory. " +
            "Let's get started!\n\n" +
            "First, please tell me: **Which city would you like to study in?**",
            ChatMessage.MESSAGE_TYPE_RECEIVED
        );
        chatAdapter.addMessage(greeting);
    }
    
    /**
     * Send user message and process response.
     */
    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Add user message to chat
        ChatMessage userMessage = new ChatMessage(messageText, ChatMessage.MESSAGE_TYPE_SENT);
        chatAdapter.addMessage(userMessage);
        etMessage.setText("");
        
        // Process message based on conversation step
        processUserMessage(messageText);
    }
    
    /**
     * Process user message based on current conversation step.
     */
    private void processUserMessage(String message) {
        pbLoading.setVisibility(View.VISIBLE);
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            pbLoading.setVisibility(View.GONE);
            
            switch (conversationStep) {
                case 0:
                    // User provided city
                    userCity = message;
                    conversationStep = 1;
                    askForMajor();
                    break;
                case 1:
                    // User provided major
                    userMajor = message;
                    conversationStep = 2;
                    recommendColleges();
                    break;
                case 2:
                    // User asking for more info or dorms
                    recommendDorms();
                    conversationStep = 3;
                    break;
                default:
                    // Continue conversation
                    handleFollowUp(message);
                    break;
            }
        }, 1000); // Simulate AI thinking time
    }
    
    /**
     * Ask user for their major/field of study.
     */
    private void askForMajor() {
        String response = "Great choice! " + userCity + " is a wonderful city for students. 🏙️\n\n" +
            "Now, please tell me: **What major or field of study are you interested in?**\n\n" +
            "For example: Computer Science, Business, Engineering, Medicine, etc.";
        
        ChatMessage aiMessage = new ChatMessage(response, ChatMessage.MESSAGE_TYPE_RECEIVED);
        chatAdapter.addMessage(aiMessage);
    }
    
    /**
     * Recommend colleges based on city and major.
     */
    private void recommendColleges() {
        List<College> recommendedColleges = findColleges(userCity, userMajor);
        
        StringBuilder response = new StringBuilder();
        response.append("🎯 Based on your interest in **").append(userMajor)
               .append("** in **").append(userCity).append("**, here are my top recommendations:\n\n");
        
        if (recommendedColleges.isEmpty()) {
            response.append("I couldn't find specific colleges for that major in ")
                   .append(userCity)
                   .append(", but here are some top universities in the area that may offer related programs:\n\n");
            
            // Get general colleges in the city
            List<College> cityColleges = getCollegesByCity(userCity);
            for (int i = 0; i < Math.min(3, cityColleges.size()); i++) {
                College college = cityColleges.get(i);
                response.append(i + 1).append(". **").append(college.name)
                       .append("**\n   - ").append(college.description)
                       .append("\n   - Rating: ").append(college.rating).append("/5\n\n");
            }
        } else {
            for (int i = 0; i < recommendedColleges.size(); i++) {
                College college = recommendedColleges.get(i);
                response.append(i + 1).append(". **").append(college.name)
                       .append("**\n   - ").append(college.description)
                       .append("\n   - Rating: ").append(college.rating).append("/5")
                       .append("\n   - Tuition: ").append(college.tuition).append("\n\n");
            }
        }
        
        response.append("Would you like me to suggest suitable dorms near these colleges? 🏠");
        
        ChatMessage aiMessage = new ChatMessage(response.toString(), ChatMessage.MESSAGE_TYPE_RECEIVED);
        chatAdapter.addMessage(aiMessage);
    }
    
    /**
     * Recommend dorms based on selected colleges.
     */
    private void recommendDorms() {
        List<DormInfo> recommendedDorms = findDorms(userCity);
        
        StringBuilder response = new StringBuilder();
        response.append("🏠 Here are some excellent dormitory options near the colleges in **")
               .append(userCity).append("**:\n\n");
        
        for (int i = 0; i < Math.min(5, recommendedDorms.size()); i++) {
            DormInfo dorm = recommendedDorms.get(i);
            response.append(i + 1).append(". **").append(dorm.name)
                   .append("**\n   - Address: ").append(dorm.address)
                   .append("\n   - Rent: ").append(dorm.rent)
                   .append("\n   - Amenities: ").append(dorm.amenities)
                   .append("\n   - Distance to campus: ").append(dorm.distance)
                   .append("\n\n");
        }
        
        response.append("💡 Tip: I recommend visiting these dorms in person and checking their availability. " +
               "Would you like more information about any specific dorm or college?");
        
        ChatMessage aiMessage = new ChatMessage(response.toString(), ChatMessage.MESSAGE_TYPE_RECEIVED);
        chatAdapter.addMessage(aiMessage);
    }
    
    /**
     * Handle follow-up questions.
     */
    private void handleFollowUp(String message) {
        String response = "I'd be happy to help with more information! " +
            "You can ask me about:\n" +
            "- Specific college details\n" +
            "- Dorm amenities and pricing\n" +
            "- Transportation options\n" +
            "- Or start over with a new city and major";
        
        ChatMessage aiMessage = new ChatMessage(response, ChatMessage.MESSAGE_TYPE_RECEIVED);
        chatAdapter.addMessage(aiMessage);
    }
    
    /**
     * Initialize the college database with sample data.
     */
    private void initializeCollegeDatabase() {
        collegeDatabase = new HashMap<>();
        
        // New York colleges
        List<College> nyColleges = new ArrayList<>();
        nyColleges.add(new College("Columbia University", "Ivy League university with excellent programs in all fields", 4.8, "$60,000/year"));
        nyColleges.add(new College("New York University (NYU)", "Premier research university with strong arts and business programs", 4.7, "$58,000/year"));
        nyColleges.add(new College("City University of New York (CUNY)", "Affordable public university system with diverse programs", 4.3, "$7,000/year"));
        nyColleges.add(new College("Fordham University", "Jesuit university with strong business and law programs", 4.5, "$55,000/year"));
        collegeDatabase.put("New York", nyColleges);
        collegeDatabase.put("NYC", nyColleges);
        collegeDatabase.put("Manhattan", nyColleges);
        
        // Boston colleges
        List<College> bostonColleges = new ArrayList<>();
        bostonColleges.add(new College("Harvard University", "World-renowned Ivy League university", 4.9, "$55,000/year"));
        bostonColleges.add(new College("MIT", "Top-tier engineering and technology institute", 4.9, "$57,000/year"));
        bostonColleges.add(new College("Boston University", "Large research university with comprehensive programs", 4.6, "$58,000/year"));
        bostonColleges.add(new College("Northeastern University", "Strong co-op programs and experiential learning", 4.5, "$54,000/year"));
        collegeDatabase.put("Boston", bostonColleges);
        
        // Los Angeles colleges
        List<College> laColleges = new ArrayList<>();
        laColleges.add(new College("UCLA", "Top public university with excellent academics", 4.7, "$13,000/year"));
        laColleges.add(new College("USC", "Prestigious private university with strong alumni network", 4.6, "$60,000/year"));
        laColleges.add(new College("Caltech", "Premier science and engineering institute", 4.8, "$58,000/year"));
        collegeDatabase.put("Los Angeles", laColleges);
        collegeDatabase.put("LA", laColleges);
        
        // Chicago colleges
        List<College> chicagoColleges = new ArrayList<>();
        chicagoColleges.add(new College("University of Chicago", "Elite research university with strong academics", 4.8, "$59,000/year"));
        chicagoColleges.add(new College("Northwestern University", "Top-tier private university", 4.7, "$58,000/year"));
        chicagoColleges.add(new College("University of Illinois at Chicago", "Affordable public university", 4.4, "$15,000/year"));
        collegeDatabase.put("Chicago", chicagoColleges);
        
        // London colleges (international)
        List<College> londonColleges = new ArrayList<>();
        londonColleges.add(new College("Imperial College London", "World-class science and engineering university", 4.8, "£35,000/year"));
        londonColleges.add(new College("University College London (UCL)", "Leading multidisciplinary university", 4.7, "£32,000/year"));
        londonColleges.add(new College("King's College London", "Prestigious university with strong research", 4.6, "£30,000/year"));
        collegeDatabase.put("London", londonColleges);
        
        // Toronto colleges
        List<College> torontoColleges = new ArrayList<>();
        torontoColleges.add(new College("University of Toronto", "Top Canadian university with diverse programs", 4.7, "CAD$38,000/year"));
        torontoColleges.add(new College("York University", "Comprehensive university with strong research", 4.5, "CAD$32,000/year"));
        torontoColleges.add(new College("Ryerson University", "Urban university with practical programs", 4.4, "CAD$30,000/year"));
        collegeDatabase.put("Toronto", torontoColleges);
    }
    
    /**
     * Initialize the dorm database with sample data.
     */
    private void initializeDormDatabase() {
        dormDatabase = new ArrayList<>();
        
        // New York dorms
        dormDatabase.add(new DormInfo("Columbia Student Housing", "2910 Broadway, New York, NY", "$1,200/month", "WiFi, Gym, Laundry, Study Rooms", "On campus"));
        dormDatabase.add(new DormInfo("NYU Residence Halls", "100 Washington Square East, NYC", "$1,500/month", "WiFi, AC, Security, Dining", "On campus"));
        dormDatabase.add(new DormInfo("The Hub", "400 5th Avenue, New York, NY", "$1,100/month", "WiFi, Gym, Common Areas", "5 min walk"));
        dormDatabase.add(new DormInfo("Brooklyn Heights Student Housing", "100 Court Street, Brooklyn", "$900/month", "WiFi, Laundry, Rooftop", "20 min subway"));
        
        // Boston dorms
        dormDatabase.add(new DormInfo("Harvard Housing", "10 Holyoke Street, Cambridge", "$1,400/month", "WiFi, Dining, Study Lounges", "On campus"));
        dormDatabase.add(new DormInfo("MIT Graduate Housing", "305 Memorial Drive, Cambridge", "$1,300/month", "WiFi, Gym, Laundry", "On campus"));
        dormDatabase.add(new DormInfo("Boston University Housing", "595 Commonwealth Ave, Boston", "$1,200/month", "WiFi, AC, Security", "On campus"));
        dormDatabase.add(new DormInfo("Fenway Student Living", "100 Peterborough Street, Boston", "$1,000/month", "WiFi, Gym, Common Areas", "10 min walk"));
        
        // Los Angeles dorms
        dormDatabase.add(new DormInfo("UCLA Housing", "350 De Neve Drive, Los Angeles", "$1,300/month", "WiFi, Dining, Recreation", "On campus"));
        dormDatabase.add(new DormInfo("USC Housing", "634 West 34th Street, LA", "$1,400/month", "WiFi, AC, Security", "On campus"));
        dormDatabase.add(new DormInfo("Westwood Student Apartments", "1050 Broxton Avenue, LA", "$1,100/month", "WiFi, Pool, Gym", "5 min walk"));
        dormDatabase.add(new DormInfo("Santa Monica Student Housing", "1430 2nd Street, Santa Monica", "$1,200/month", "WiFi, Beach Access, Laundry", "15 min bus"));
        
        // Chicago dorms
        dormDatabase.add(new DormInfo("University of Chicago Housing", "5801 S Ellis Ave, Chicago", "$1,200/month", "WiFi, Dining, Study Spaces", "On campus"));
        dormDatabase.add(new DormInfo("Northwestern Housing", "633 Emerson Street, Evanston", "$1,300/month", "WiFi, Gym, Laundry", "On campus"));
        dormDatabase.add(new DormInfo("Loop Student Living", "200 S State Street, Chicago", "$1,000/month", "WiFi, Fitness Center, Rooftop", "20 min train"));
        
        // London dorms
        dormDatabase.add(new DormInfo("Imperial College Halls", "Prince Gardens, London", "£800/month", "WiFi, Kitchen, Common Room", "On campus"));
        dormDatabase.add(new DormInfo("UCL Student Housing", "19-26 Gordon Street, London", "£900/month", "WiFi, Laundry, Study Areas", "On campus"));
        dormDatabase.add(new DormInfo("London Student Accommodation", "100 Westminster Bridge Road", "£750/month", "WiFi, Gym, Social Spaces", "15 min tube"));
        
        // Toronto dorms
        dormDatabase.add(new DormInfo("U of T Residence", "89 Chestnut Street, Toronto", "CAD$1,100/month", "WiFi, Meal Plan, Study Rooms", "On campus"));
        dormDatabase.add(new DormInfo("York University Housing", "4700 Keele Street, Toronto", "CAD$950/month", "WiFi, Laundry, Common Areas", "On campus"));
        dormDatabase.add(new DormInfo("Downtown Toronto Student Housing", "100 King Street West, Toronto", "CAD$1,200/month", "WiFi, Gym, Transit Access", "20 min subway"));
    }
    
    /**
     * Find colleges matching the city and major.
     */
    private List<College> findColleges(String city, String major) {
        List<College> results = new ArrayList<>();
        
        // Get colleges in the city
        List<College> cityColleges = getCollegesByCity(city);
        
        // Filter by major (simplified matching)
        String majorLower = major.toLowerCase();
        for (College college : cityColleges) {
            if (college.description.toLowerCase().contains(majorLower) ||
                college.name.toLowerCase().contains(majorLower) ||
                majorLower.contains("computer") && college.name.contains("Technology") ||
                majorLower.contains("business") && college.description.toLowerCase().contains("business") ||
                majorLower.contains("engineering") && college.description.toLowerCase().contains("engineering") ||
                majorLower.contains("medicine") && college.description.toLowerCase().contains("medical")) {
                results.add(college);
            }
        }
        
        return results;
    }
    
    /**
     * Get all colleges in a city.
     */
    private List<College> getCollegesByCity(String city) {
        List<College> colleges = collegeDatabase.get(city);
        if (colleges == null) {
            // Try to find a partial match
            for (String key : collegeDatabase.keySet()) {
                if (key.toLowerCase().contains(city.toLowerCase()) ||
                    city.toLowerCase().contains(key.toLowerCase())) {
                    return collegeDatabase.get(key);
                }
            }
            return new ArrayList<>();
        }
        return colleges;
    }
    
    /**
     * Find dorms in a city.
     */
    private List<DormInfo> findDorms(String city) {
        List<DormInfo> results = new ArrayList<>();
        String cityLower = city.toLowerCase();
        
        for (DormInfo dorm : dormDatabase) {
            String addressLower = dorm.address.toLowerCase();
            if (addressLower.contains(cityLower) ||
                cityLower.contains("new york") && addressLower.contains("new york") ||
                cityLower.contains("nyc") && addressLower.contains("new york") ||
                cityLower.contains("boston") && addressLower.contains("boston") ||
                cityLower.contains("los angeles") && addressLower.contains("los angeles") ||
                cityLower.contains("la") && addressLower.contains("los angeles") ||
                cityLower.contains("chicago") && addressLower.contains("chicago") ||
                cityLower.contains("london") && addressLower.contains("london") ||
                cityLower.contains("toronto") && addressLower.contains("toronto")) {
                results.add(dorm);
            }
        }
        
        return results;
    }
    
    /**
     * Inner class representing a College.
     */
    private static class College {
        String name;
        String description;
        double rating;
        String tuition;
        
        College(String name, String description, double rating, String tuition) {
            this.name = name;
            this.description = description;
            this.rating = rating;
            this.tuition = tuition;
        }
    }
    
    /**
     * Inner class representing a Dorm.
     */
    private static class DormInfo {
        String name;
        String address;
        String rent;
        String amenities;
        String distance;
        
        DormInfo(String name, String address, String rent, String amenities, String distance) {
            this.name = name;
            this.address = address;
            this.rent = rent;
            this.amenities = amenities;
            this.distance = distance;
        }
    }
}