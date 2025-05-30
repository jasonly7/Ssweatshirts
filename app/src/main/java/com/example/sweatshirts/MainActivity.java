package com.example.sweatshirts;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import kotlinx.coroutines.channels.TickerChannelsKt;

public class MainActivity extends AppCompatActivity {
    //private RadioButton radioOption1,radioOption2, radioOption3;
    private ImageView sweater1,sweater2, sweater3;
    private ImageView shirtImageView;
    private Drawable drawable;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        data = new Data();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name");
            //int userId = extras.getInt("user_id", -1); // -1 is a default value if key not found
            data = (Data) extras.getSerializable("data");

            // Use the data
            Log.d("MainActivity", "Received: Name=" + data.getName() );
            Log.d("MainActivity", "Received: Email=" + data.getEmail() );
        }


        Button switchButton = findViewById(R.id.switch_button);
        sweater1 = findViewById(R.id.sweater1);
        sweater2 = findViewById(R.id.sweater2);
        sweater3 = findViewById(R.id.sweater3);

        shirtImageView = findViewById(R.id.shirtImageView);

        sweater1.setOnClickListener( v -> {
            int resourceId = getResources().getIdentifier("blackcrew","drawable", getPackageName());
            shirtImageView.setImageResource(resourceId);
            drawable = shirtImageView.getDrawable();

            shirtImageView.setImageDrawable(drawable);
        });

        sweater2.setOnClickListener( v -> {
            int resourceId = getResources().getIdentifier("light_steel_crew","drawable", getPackageName());
            shirtImageView.setImageResource(resourceId);
            drawable = shirtImageView.getDrawable();
            shirtImageView.setImageDrawable(drawable);
        });

        sweater3.setOnClickListener( v -> {
            int resourceId = getResources().getIdentifier("sand_crew","drawable", getPackageName());
            shirtImageView.setImageResource(resourceId);
            drawable = shirtImageView.getDrawable();
            shirtImageView.setImageDrawable(drawable);
        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This code executes when the button is clicked

                // 3. Create an Intent
                // An Intent is an object that describes an action to be performed.
                // Here, we're creating an explicit Intent to start a specific Activity.
                // Parameters:
                //   - Context: The current Activity (MainActivity.this)
                //   - Class: The Activity you want to start (SecondActivity.class)
                Intent intent = new Intent(MainActivity.this, TikTokActivity.class);
                intent.putExtra("data",data);
                // 4. Start the new Activity using the Intent
                startActivity(intent);

                // Optional: If you want to prevent the user from coming back to MainActivity
                // by pressing the back button, you can call finish() here.
                // finish();
            }
        });
    }

    private int ConvertDpToPixels(int dp)
    {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}