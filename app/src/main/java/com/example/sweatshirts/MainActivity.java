package com.example.sweatshirts;



import java.security.GeneralSecurityException;
import java.util.Collections;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Builder;
import com.google.api.services.drive.DriveScopes;

import kotlinx.coroutines.channels.TickerChannelsKt;
import java.io.*;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

public class MainActivity extends AppCompatActivity {
    //private RadioButton radioOption1,radioOption2, radioOption3;
    private ImageView sweater1,sweater2, sweater3;
    private ImageView shirtImageView;
    private Drawable drawable;
    private Data data;

    private static final String TAG = "DriveActivity";
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2; // For picking a file if needed
    private static final int REQUEST_CODE_CREATE_FILE = 3;

    /**
     * Global instance of the HTTP transport.
     */
    private static NetHttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private GoogleSignInClient mGoogleSignInClient;
    private Drive mDriveService;



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
        Button signinButton = findViewById(R.id.signin_button);
        Button submitButton = findViewById(R.id.submit_button);
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

        // Configure Google Sign-In for Drive scope
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE)) // Request scope for specific file access
                        // .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA)) // If you need app-specific folder
                        // .requestScopes(new Scope(DriveScopes.DRIVE)) // Full Drive access (be careful with this!)
                        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = "test.txt";
                String fileContent = "blah blah blah";
                File file = saveTextToFile(fileName, fileContent);

                uploadFileToDrive(file);

            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
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

        mDriveService = new Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY, // for Android
                credential)
                .setApplicationName("SweatShirt") // Set your app name here
                .build();

        Log.d(TAG, "Drive service initialized.");
        // Now mDriveService is ready to make API calls
    }

    /**
     * Build and return an authorized Drive Activity client service.
     *
     * @return an authorized DriveActivity client service
     * @throws IOException

    public static com.google.api.services.driveactivity.v2.DriveActivity getDriveActivityService()
            throws IOException {
        Credential credential = authorize();
        com.google.api.services.driveactivity.v2.DriveActivity service =
                new com.google.api.services.driveactivity.v2.DriveActivity.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, credential)
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        return service;
    }*/

    // ... uploadFileToDrive() method will go here
    private void uploadFileToDrive(File fileToUpload) {
        if (mDriveService == null) {
            Toast.makeText(this, "Google Drive service not initialized. Sign in first.", Toast.LENGTH_SHORT).show();
            signIn(); // Prompt sign-in if not already signed in
            return;
        }

        // IMPORTANT: Replace this with the actual file you want to upload.
        // This example assumes you have a File object ready (e.g., from internal storage,
        // or picked by the user, or created dynamically).
       // java.io.File fileToUpload; // You need to implement this
        //fileToUpload = file;
        if (fileToUpload == null || !fileToUpload.exists()) {
            Toast.makeText(this, "File to upload not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Run upload in a background thread to prevent ANR
        new Thread(() -> {
            try {
                com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
                fileMetadata.setName(fileToUpload.getName()); // Use the original file name

                // Optionally, set parent folder ID if you want to upload to a specific folder
                // fileMetadata.setParents(Collections.singletonList("YOUR_FOLDER_ID_HERE"));

                // Determine MIME type (e.g., "image/jpeg", "application/pdf", "text/plain")
                String mimeType = getMimeType(fileToUpload.getAbsolutePath()); // You need to implement getMimeType
                FileContent mediaContent = new FileContent(mimeType, fileToUpload);

                com.google.api.services.drive.model.File uploadedFile = mDriveService.files().create(fileMetadata, mediaContent)
                        .setFields("id,name,webViewLink") // Request specific fields in the response
                        .execute();

                runOnUiThread(() -> {
                    if (uploadedFile != null) {
                        Toast.makeText(this, "File uploaded! ID: " + uploadedFile.getId(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Uploaded file: " + uploadedFile.getName() + ", ID: " + uploadedFile.getId() + ", Link: " + uploadedFile.getWebViewLink());
                        // You can open the link in a browser:
                        // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uploadedFile.getWebViewLink()));
                        // startActivity(browserIntent);
                    } else {
                        Toast.makeText(this, "File upload failed.", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (IOException e) {
                Log.e(TAG, "Error uploading file: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(this, "Upload error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    // Basic MIME type detection. For production, consider a more robust library or `URLConnection.guessContentTypeFromName`.
    private String getMimeType(String filePath) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type != null ? type : "application/octet-stream"; // Default if unknown
    }

    private File saveTextToFile(String fileName, String content) {
        // Option 1: Using getFilesDir() for persistent private storage
        File file = new File(getFilesDir(), fileName);

        // Option 2: Using getCacheDir() for temporary private storage (uncomment to use)
        // File file = new File(getCacheDir(), fileName);

        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(fos);
             BufferedWriter writer = new BufferedWriter(osw)) { // Use BufferedWriter for efficiency

            writer.write(content);
            writer.flush(); // Ensure all buffered data is written to the file
            Toast.makeText(this, "File saved to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "File saved: " + file.getAbsolutePath());

        } catch (IOException e) {
            Log.e(TAG, "Error saving file: " + e.getMessage(), e);
            Toast.makeText(this, "Error saving file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return file;
    }
    private int ConvertDpToPixels(int dp)
    {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}