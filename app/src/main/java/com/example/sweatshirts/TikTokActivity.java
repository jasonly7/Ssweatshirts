package com.example.sweatshirts;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.Button; // Import Button
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class TikTokActivity extends AppCompatActivity {
    private Data data;
    private void ShowText(CheckBox checkBox)
    {
        SpannableString spannableString = new SpannableString(checkBox.getText());

        // 2. Load your custom fonts as Typeface objects
        // Use ResourcesCompat.getFont for backward compatibility and efficiency
        Typeface font1 = ResourcesCompat.getFont(this, R.font.tiktok); // Assuming tiktok.otf is font1.otf
        Typeface font2 = ResourcesCompat.getFont(this, R.font.tiktok_light); // Assuming another_font.otf is font2.otf

        // Find the start and end indices of the part you want to bold
        int startIndex = 0;
        int endIndex = checkBox.getText().toString().indexOf(":");

        if (startIndex != -1) { // Ensure the substring was found
            // Apply the StyleSpan for bold
           /* spannableString.setSpan(
                    new StyleSpan(Typeface.BOLD), // The span to apply (bold style)
                    startIndex,                    // Start index of the text to bold (inclusive)
                    endIndex,                      // End index of the text to bold (exclusive)
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Flag for how the span behaves when text is added/removed
            );*/
            spannableString.setSpan(
                    new CustomTypefaceSpan(font1), // The span to apply (bold style)
                    startIndex,                    // Start index of the text to bold (inclusive)
                    endIndex,                      // End index of the text to bold (exclusive)
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Flag for how the span behaves when text is added/removed
            );
        }

        checkBox.setText(spannableString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tik_tok);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        data = new Data();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //String name = extras.getString("name");
            //int userId = extras.getInt("user_id", -1); // -1 is a default value if key not found
            Data data = (Data) extras.getSerializable("data");

            // Use the data
            Log.d("TikTokActivity", "Received: Name=" + data.getName() );
            Log.d("TikTokActivity", "Received: Email=" + data.getEmail() );
            TextInputEditText nameTextInput = findViewById(R.id.nameTextInput);
            TextInputEditText emailTextInput = findViewById(R.id.emailTextInput);
            nameTextInput.setText(data.getName());
            emailTextInput.setText(data.getEmail());
        }
        // 1. Find the Button by its ID from the layout
        Button switchButton = findViewById(R.id.switch_button);

        // 2. Set an OnClickListener for the button
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText nameTextInput = findViewById(R.id.nameTextInput);
                String name = nameTextInput.getText().toString();
                // This code executes when the button is clicked
                data.setName(name);

                TextInputEditText emailTextInput = findViewById(R.id.emailTextInput);
                String email = emailTextInput.getText().toString();
                // This code executes when the button is clicked
                data.setEmail(email);

                // 3. Create an Intent
                // An Intent is an object that describes an action to be performed.
                // Here, we're creating an explicit Intent to start a specific Activity.
                // Parameters:
                //   - Context: The current Activity (MainActivity.this)
                //   - Class: The Activity you want to start (SecondActivity.class)
                Intent intent = new Intent(TikTokActivity.this, MainActivity.class);
                //intent.putExtra("name", data.getName());

                intent.putExtra("data",data);

                // 4. Start the new Activity using the Intent
                startActivity(intent);

                // Optional: If you want to prevent the user from coming back to MainActivity
                // by pressing the back button, you can call finish() here.
                // finish();
            }
        });

        CheckBox checkBox1 = findViewById(R.id.checkbox1);
        ShowText(checkBox1);
        CheckBox checkBox2 = findViewById(R.id.checkbox2);
        ShowText(checkBox2);
        CheckBox checkBox3 = findViewById(R.id.checkbox3);
        ShowText(checkBox3);
        CheckBox checkBox4 = findViewById(R.id.checkbox4);
        ShowText(checkBox4);
        CheckBox checkBox5 = findViewById(R.id.checkbox5);
        ShowText(checkBox5);
        CheckBox checkBox6 = findViewById(R.id.checkbox6);
        ShowText(checkBox6);
        CheckBox checkBox7 = findViewById(R.id.checkbox7);
        ShowText(checkBox7);
        /*String htmlString = "<b>Brand &amp; Awareness Solutions:</b> Build impact with premium, high-reach and high-attention formats (TopView, Top Feed, TikTok Pulse)";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // For API 24 (Nougat) and above, use FROM_HTML_MODE_COMPACT
            checkBox1.setText(Html.fromHtml(htmlString, Html.FROM_HTML_MODE_COMPACT));
        } else {
            // For older APIs, use the deprecated fromHtml method
            checkBox1.setText(Html.fromHtml(htmlString));
        }*/
    }
}