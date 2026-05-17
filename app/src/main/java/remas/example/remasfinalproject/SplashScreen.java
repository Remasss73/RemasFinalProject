package remas.example.remasfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * شاشة البداية (Splash Screen)
 * =========================
 * 
 * هذه الفئة تمثل شاشة البداية التي تظهر عند تشغيل التطبيق.
 * وظيفتها الأساسية هي عرض شعار التطبيق أو رسالة ترحيبية لفترة قصيرة
 * قبل الانتقال إلى شاشة تسجيل الدخول.
 *
 * SplashScreen Class
 * ==================
 *
 * This class represents the splash screen that appears when the application starts.
 * Its main function is to display the application logo or a welcome message for a short period
 * before navigating to the sign-in screen.
 *
 * @author Remas Project Team
 * @version 1.0
 */
public class SplashScreen extends BaseActivity {

    /**
     * onCreate method
     * ===============
     * 
     * هذه الطريقة يتم استدعاؤها عند إنشاء النشاط (Activity) لأول مرة.
 * تقوم بالخطوات التالية:
     * 1. استدعاء onCreate للفئة الأصل (BaseActivity) لضمان التهيئة الصحيحة
     * 2. تعيين ملف التخطيط (layout) الخاص بشاشة البداية
     * 3. استخدام Handler لتأخير الانتقال إلى شاشة تسجيل الدخول لمدة 3 ثواني
     * 4. بعد انتهاء المهلة، يتم إنشاء Intent للانتقال إلى SignIn
     * 5. بدء النشاط الجديد وإغلاق شاشة البداية
     *
     * onCreate Method
     * ================
     *
     * This method is called when the activity is created for the first time.
     * It performs the following steps:
     * 1. Calls onCreate of the parent class (BaseActivity) to ensure proper initialization
     * 2. Sets the layout file for the splash screen
     * 3. Uses a Handler to delay navigation to the sign-in screen for 3 seconds
     * 4. After the delay expires, creates an Intent to navigate to SignIn
     * 5. Starts the new activity and closes the splash screen
     *
     * @param savedInstanceState حزمة تحتوي على حالة النشاط المحفوظة سابقاً
     *                           Bundle containing the previously saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        
        /**
         * شرح مفصل لـ Handler و postDelayed
         * =================================
         * 
         * ما هو Handler؟
         * --------------
         * Handler هو فئة أساسية في نظام Android (android.os.Handler) تعمل كـ "مراسل" أو "منسق"
         * بين الخيوط (Threads) المختلفة في التطبيق.
         * 
         * لماذا نحتاجه؟
         * ---------------
         * في Android، هناك قاعدة مهمة: لا يمكن تحديث واجهة المستخدم (UI) من أي خيط غير الخيط الرئيسي (Main Thread).
         * الخيط الرئيسي هو المسؤول عن رسم الواجهة والاستجابة للمستخدم.
         * إذا حاولت تحديث UI من خيط آخر، سيتوقف التطبيق عن العمل (Crash).
         * 
         * من يستخدم Handler؟
         * ------------------
         * - المطورون (Developers): لجدولة المهام في المستقبل
         * - نظام Android نفسه: لإرسال الرسائل بين المكونات المختلفة
         * - الخلفيات (Background Threads): لإرسال النتائج إلى الخيط الرئيسي
         * 
         * ما يحتاجه Handler ليعمل؟
         * ------------------------
         * 1. Looper: حلقة تكرار (loop) ت continuously تفحص الرسائل القادمة
         *    - الخيط الرئيسي لديه Looper جاهز تلقائياً
         *    - الخيوط الأخرى تحتاج إلى إنشاء Looper يدوياً
         * 2. MessageQueue: قائمة انتظار تخزن الرسائل والمهام
         * 3. Thread: الخيط الذي سيعمل عليه Handler
         * 
         * كيف يعمل Handler؟
         * -----------------
         * 1. تنشئ Handler (غالباً على الخيط الرئيسي)
         * 2. ترسل رسالة أو مهمة عبر post() أو postDelayed()
         * 3. الرسالة تذهب إلى MessageQueue
         * 4. Looper يفحص القائمة باستمرار
         * 5. عند وصول وقت التنفيذ، Looper يسلم الرسالة لل Handler
         * 6. Handler ينفذ الكود في الخيط الذي تم إنشاؤه عليه
         * 
         * postDelayed بالتحديد:
         * ---------------------
         * - postDelayed(Runnable, delayMillis): جدولة مهمة لتتنفذ بعد مهلة محددة
         * - delayMillis: الوقت بالميلي ثانية (1000 مللي ثانية = 1 ثانية)
         * - Runnable: واجهة تحتوي على طريقة run() التي تحمل الكود المراد تنفيذه
         * 
         * في هذا الكود:
         * - new Handler(): ينشئ Handler على الخيط الرئيسي (لأننا في onCreate)
         * - postDelayed(..., 3000): يطلب تنفيذ المهمة بعد 3 ثواني
         * - new Runnable() {...}: يحدد ماذا سيحدث بعد 3 ثواني
         * - Intent: يحدد إلى أين سننتقل (من SplashScreen إلى SignIn)
         * - startActivity(): يبدأ النشاط الجديد فعلياً
         * - finish(): يغلق شاشة البداية حتى لا يعود المستخدم إليها
         * 
         * Detailed Explanation of Handler and postDelayed
         * ================================================
         * 
         * What is Handler?
         * ---------------
         * Handler is a fundamental class in the Android system (android.os.Handler) that acts as a
         * "messenger" or "coordinator" between different threads in the application.
         * 
         * Why do we need it?
         * ------------------
         * In Android, there's an important rule: You cannot update the User Interface (UI) from any thread
         * other than the Main Thread (also called UI Thread).
         * The Main Thread is responsible for drawing the UI and responding to user interactions.
         * If you try to update UI from another thread, the application will crash (Application Not Responding).
         * 
         * Who uses Handler?
         * ----------------
         * - Developers: To schedule tasks to run in the future
         * - The Android system itself: To send messages between different components
         * - Background Threads: To send results back to the Main Thread
         * 
         * What does Handler need to work?
         * ------------------------------
         * 1. Looper: A continuous loop that continuously checks for incoming messages
         *    - The Main Thread has a Looper ready automatically
         *    - Other threads need to create a Looper manually
         * 2. MessageQueue: A queue that stores messages and tasks
         * 3. Thread: The thread on which the Handler will operate
         * 
         * How does Handler work?
         * ---------------------
         * 1. You create a Handler (usually on the Main Thread)
         * 2. You send a message or task via post() or postDelayed()
         * 3. The message goes into the MessageQueue
         * 4. The Looper continuously checks the queue
         * 5. When the execution time arrives, Looper delivers the message to Handler
         * 6. Handler executes the code on the thread it was created on
         * 
         * postDelayed specifically:
         * ------------------------
         * - postDelayed(Runnable, delayMillis): Schedule a task to execute after a specified delay
         * - delayMillis: Time in milliseconds (1000 milliseconds = 1 second)
         * - Runnable: An interface containing the run() method that holds the code to execute
         * 
         * In this code:
         * - new Handler(): Creates a Handler on the Main Thread (because we're in onCreate)
         * - postDelayed(..., 3000): Requests to execute the task after 3 seconds
         * - new Runnable() {...}: Defines what will happen after 3 seconds
         * - Intent: Specifies where we'll navigate (from SplashScreen to SignIn)
         * - startActivity(): Actually starts the new activity
         * - finish(): Closes the splash screen so the user cannot return to it
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, SignIn.class);
                startActivity(i);
                finish();
            }
        }, 3000);
    }
}
