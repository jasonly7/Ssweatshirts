package com.peaceandcotton.sweatshirts;

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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.peaceandcotton.sweatshirts.CustomTypefaceSpan;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class TikTokActivity extends AppCompatActivity {
    private static final String TAG = "DriveActivity";
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2; // For picking a file if needed
    private static final int REQUEST_CODE_CREATE_FILE = 3;

    /**
     * Global instance of the HTTP transport.
     */
    private static NetHttpTransport HTTP_TRANSPORT;

    private Drive mDriveService;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private Data data;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView statusTextView;

    private Button signinButton;
    private Button signoutButton;
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
        signinButton = findViewById(R.id.signin_button);
        signoutButton = findViewById(R.id.signout_button);
        statusTextView = findViewById((R.id.statusTextView));

        CheckBox checkBox1 = findViewById(R.id.checkbox1);
        CheckBox checkBox2 = findViewById(R.id.checkbox2);
        CheckBox checkBox3 = findViewById(R.id.checkbox3);
        CheckBox checkBox4 = findViewById(R.id.checkbox4);
        CheckBox checkBox5 = findViewById(R.id.checkbox5);
        CheckBox checkBox6 = findViewById(R.id.checkbox6);
        CheckBox checkBox7 = findViewById(R.id.checkbox7);

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

            checkBox1.setChecked(data.getCheckedTikTokSolutionAt(0));
            checkBox2.setChecked(data.getCheckedTikTokSolutionAt(1));
            checkBox3.setChecked(data.getCheckedTikTokSolutionAt(2));
            checkBox4.setChecked(data.getCheckedTikTokSolutionAt(3));
            checkBox5.setChecked(data.getCheckedTikTokSolutionAt(4));
            checkBox6.setChecked(data.getCheckedTikTokSolutionAt(5));
            checkBox7.setChecked(data.getCheckedTikTokSolutionAt(6));
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

        ShowText(checkBox1);
        data.setCheckedTikTokSolutionTextAt(0,checkBox1.getText().toString());

        ShowText(checkBox2);
        data.setCheckedTikTokSolutionTextAt(1,checkBox2.getText().toString());

        ShowText(checkBox3);
        data.setCheckedTikTokSolutionTextAt(2,checkBox3.getText().toString());

        ShowText(checkBox4);
        data.setCheckedTikTokSolutionTextAt(3,checkBox4.getText().toString());

        ShowText(checkBox5);
        data.setCheckedTikTokSolutionTextAt(4,checkBox5.getText().toString());

        ShowText(checkBox6);
        data.setCheckedTikTokSolutionTextAt(5,checkBox5.getText().toString());

        ShowText(checkBox7);
        data.setCheckedTikTokSolutionTextAt(6,checkBox6.getText().toString());

        checkBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setCheckedTikTokSolutionAt(0,isChecked);
            }
        });

        checkBox2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setCheckedTikTokSolutionAt(1,isChecked);
            }
        });

        checkBox3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setCheckedTikTokSolutionAt(2,isChecked);
            }
        });

        checkBox4.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setCheckedTikTokSolutionAt(3,isChecked);
            }
        });

        checkBox5.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setCheckedTikTokSolutionAt(4,isChecked);
            }
        });

        checkBox5.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setCheckedTikTokSolutionAt(5,isChecked);
            }
        });

        checkBox6.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setCheckedTikTokSolutionAt(6,isChecked);
            }
        });

        // Configure Google Sign-In for Drive scope
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE)) // Request scope for specific file access
                        // .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA)) // If you need app-specific folder
                        // .requestScopes(new Scope(DriveScopes.DRIVE)) // Full Drive access (be careful with this!)
                        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);

       // checkCurrentUserStatus();

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(resultData);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    initializeDriveClient(account);
                }
                checkCurrentUserStatus();
            } catch (ApiException e) {
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                // Handle sign-in failure
            }
        }
        // ... other request codes like for picking a file if you implement it
    }

    private void initializeDriveClient(GoogleSignInAccount account) {
        // Use the authenticated account to build a Drive service object.
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        this, Collections.singleton(DriveScopes.DRIVE_FILE)); // Must match the scope requested in GSO
        credential.setSelectedAccount(account.getAccount());
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException gse) {
            Log.e(TAG, "Failed to init http transport");
            gse.printStackTrace();
        }

        mDriveService = new Drive.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY, // for Android
                credential)
                .setApplicationName("SweatShirt") // Set your app name here
                .build();

        Log.d(TAG, "Drive service initialized.");
        // Now mDriveService is ready to make API calls
    }
    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            statusTextView.setText("Signed in as: " + account.getEmail());
            signinButton.setVisibility(View.GONE);
            signoutButton.setVisibility(View.VISIBLE);
            // Optionally, you might enable other features like upload button here
            // findViewById(R.id.uploadButton).setEnabled(true);
        } else {
            statusTextView.setText("Not signed in.");
            signinButton.setVisibility(View.VISIBLE);
            signoutButton.setVisibility(View.GONE);
            // Optionally, disable features requiring sign-in
            // findViewById(R.id.uploadButton).setEnabled(false);
        }
    }
    private void checkCurrentUserStatus() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // This method is called when the sign-out operation completes,
                        // whether it was successful or not.
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User signed out successfully.");
                            Toast.makeText(TikTokActivity.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                            // You might want to update your UI or navigate to a login screen
                            // For example, if you have a signIn() method:
                             signIn(); // Optionally, immediately offer sign-in again
                        } else {
                            Log.e(TAG, "Error signing out: " + task.getException());
                            Toast.makeText(TikTokActivity.this, "Sign-out failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}