package com.peaceandcotton.sweatshirts;



import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.security.GeneralSecurityException;
import java.util.Collections;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Builder;
import com.google.api.services.drive.DriveScopes;

import kotlinx.coroutines.channels.TickerChannelsKt;
import java.io.*;
import java.util.Objects;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

public class MainActivity extends AppCompatActivity {
    private RadioButton radioOption1,radioOption2, radioOption3;
    private RadioGroup sizeRadioGroup, logoRadioGroup;
    private ImageView sweater1,sweater2, sweater3, selectedImage;
    private TextInputEditText initialsTextInput;
    private TextView initialsText, initialsTextView;
    private ImageView shirtImageView;
    private Drawable drawable;
    private com.peaceandcotton.sweatshirts.Data data;

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

    private static final String EMAILTAG = "EmailFileSender";
    private Button sendEmailButton;
    private File fileToSend; // The file we'll attach

    private Drawable drawableLogo;

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
        data = new com.peaceandcotton.sweatshirts.Data();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name");
            //int userId = extras.getInt("user_id", -1); // -1 is a default value if key not found
            data = (com.peaceandcotton.sweatshirts.Data) extras.getSerializable("data");
            data.setColor("blackcrew");
            // Use the data
            //Log.d("MainActivity", "Received: Name=" + data.getName() );
           // Log.d("MainActivity", "Received: Email=" + data.getEmail() );
        }

        Button submitButton = findViewById(R.id.submit_button);
        Button switchButton = findViewById(R.id.switch_button);
        sweater1 = findViewById(R.id.sweater1);
        sweater2 = findViewById(R.id.sweater2);
        sweater3 = findViewById(R.id.sweater3);
        sizeRadioGroup = findViewById(R.id.sizeRadioGroup);
        logoRadioGroup = findViewById(R.id.logoRadioGroup);
        radioOption1 = findViewById(R.id.radioOption1);
        radioOption2 = findViewById(R.id.radioOption2);
        radioOption3 = findViewById(R.id.radioOption3);
        initialsTextView = findViewById(R.id.initialsTextView);
        initialsTextInput = findViewById(R.id.initialsTextInput);
        initialsText = findViewById(R.id.initialsText);
        selectedImage = findViewById(R.id.selectedImage);
        shirtImageView = findViewById(R.id.shirtImageView);

        sweater1.setOnClickListener( v -> {
            int resourceId = getResources().getIdentifier("blackcrew","drawable", getPackageName());
            shirtImageView.setImageResource(resourceId);
            drawable = shirtImageView.getDrawable();
            data.setColor("blackcrew");
            shirtImageView.setImageDrawable(drawable);
            initialsText.setTextColor(Color.WHITE);
        });

        sweater2.setOnClickListener( v -> {
            int resourceId = getResources().getIdentifier("light_steel_crew","drawable", getPackageName());
            shirtImageView.setImageResource(resourceId);
            drawable = shirtImageView.getDrawable();
            data.setColor("light steel crew");
            /*ViewGroup.LayoutParams imageParams = selectedImage.getLayoutParams();
            int widthInPx = ConvertDpToPixels (720);
            int heightInPx = ConvertDpToPixels (720);
            imageParams.width = widthInPx;
            imageParams.height = heightInPx;
            selectedImage.setLayoutParams(imageParams);*/
            initialsText.setTextColor(Color.BLACK);
            shirtImageView.setImageDrawable(drawable);
        });

        sweater3.setOnClickListener( v -> {
            int resourceId = getResources().getIdentifier("sand_crew","drawable", getPackageName());
            shirtImageView.setImageResource(resourceId);
            drawable = shirtImageView.getDrawable();
            data.setColor("sand crew");
            initialsText.setTextColor(Color.BLACK);
            shirtImageView.setImageDrawable(drawable);
        });

        // Set the listener for the RadioGroup
        sizeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // `group` is the RadioGroup that received the change
                // `checkedId` is the ID of the RadioButton that is now checked

                // Find the selected RadioButton by its ID
                RadioButton selectedRadioButton = findViewById(checkedId);

                if (selectedRadioButton != null) {
                    String selectedOption = selectedRadioButton.getText().toString();
                    //selectionStatusTextView.setText("Selected: " + selectedOption);
                    data.setSize(selectedOption);
                    Toast.makeText(MainActivity.this, "You selected: " + selectedOption, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the listener for the RadioGroup
        logoRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // `group` is the RadioGroup that received the change
                // `checkedId` is the ID of the RadioButton that is now checked

                // Find the selected RadioButton by its ID
                RadioButton selectedRadioButton = findViewById(checkedId);

                if (selectedRadioButton != null) {
                    String selectedOption = selectedRadioButton.getText().toString();
                    ViewGroup.LayoutParams imageParams = selectedImage.getLayoutParams();
                    int widthInPx = ConvertDpToPixels (100);
                    int heightInPx = ConvertDpToPixels (100);
                    imageParams.width = widthInPx;
                    imageParams.height = heightInPx;
                    selectedImage.setLayoutParams(imageParams);
                    selectedImage.setX((float)shirtImageView.getWidth()/4 );
                    selectedImage.setY((float)shirtImageView.getHeight()/4 );
                    data.setLogo(selectedOption);
                    switch (selectedOption)
                    {
                        case "Vibey":
                            selectedImage.setImageResource(R.drawable.tiktok_vibey);
                            initialsText.setText("");
                            initialsTextInput.setVisibility(GONE);
                            initialsTextView.setVisibility(GONE);
                            break;
                        case "Slay":
                            selectedImage.setImageResource(R.drawable.tiktok_slay);
                            initialsText.setText("");
                            initialsTextInput.setVisibility(GONE);
                            initialsTextView.setVisibility(GONE);
                            break;
                        case "Maple Leaf":
                            selectedImage.setImageResource(R.drawable.tiktok_maple_leafs);
                            initialsText.setText("");
                            initialsTextInput.setVisibility(GONE);
                            initialsTextView.setVisibility(GONE);
                            break;
                        case "Initials":
                            selectedImage.setImageDrawable(null);
                            String userInput = initialsTextInput.getText().toString();
                            initialsText.setText(userInput);
                            if (data.getColor() == "blackcrew")
                                initialsText.setTextColor(Color.WHITE);
                            else
                                initialsText.setTextColor(Color.BLACK);
                            initialsText.setX((float)shirtImageView.getWidth()/4 );
                            initialsText.setY((float)shirtImageView.getHeight()/4 );
                            initialsTextInput.setVisibility(VISIBLE);
                            initialsTextView.setVisibility(VISIBLE);
                            break;
                    }
                    //selectionStatusTextView.setText("Selected: " + selectedOption);
                    //data.setSize(selectedOption);
                    //Toast.makeText(MainActivity.this, "You selected: " + selectedOption, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add the TextWatcher to your EditText
        initialsTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed.
                // You can log or perform actions before the change happens.
                // For example: Log.d("TextWatcher", "Before: " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called as the text is changing (character by character).
                // 's' contains the new text.
                // You can use this for real-time validation or filtering.
                String logo = data.getLogo();
                if (data.getColor() == "blackcrew")
                    initialsText.setTextColor(Color.WHITE);
                else
                    initialsText.setTextColor(Color.BLACK);
                if (logo!=null) {
                    if (logo.equals("Initials"))
                        initialsText.setText(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text has changed.
                // 's' contains the final, editable text.
                // This is often the most convenient place to get the final text after a change.
                // For example: String finalInput = s.toString();
                // if (finalInput.length() > 10) {
                //     myEditText.setError("Too long!");
                // }
            }
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
                Intent intent = new Intent(MainActivity.this, com.peaceandcotton.sweatshirts.TikTokActivity.class);
                intent.putExtra("data", data);
                // 4. Start the new Activity using the Intent
                startActivity(intent);

                // Optional: If you want to prevent the user from coming back to MainActivity
                // by pressing the back button, you can call finish() here.
                // finish();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = "test.txt";
                String fileContent = "Name: " + data.getName() +
                        "\nEmail: " + data.getEmail() + "\n\n" +
                        "Which TikTok Solutions are you most interested in learning more about?\n";

                String solution;
                for (int i = 0; i < data.getsTikTokSolutions().length; i++)
                {
                    boolean checked = data.getCheckedTikTokSolutionAt(i);
                    if (checked) {
                        solution = data.getCheckedTikTokSolutionTextAt(i);
                        solution = solution.substring(0, solution.indexOf(":")) + "\n";
                        fileContent += solution;
                    }
                }
                fileContent+="\n";
                fileContent+="Color: " + data.getColor();
                fileContent+="\n";
                fileContent+="Size: " + data.getSize();
                data.setInitials(Objects.requireNonNull(initialsTextInput.getText()).toString());
                fileContent+="\n";
                fileContent+="Logo: " + data.getLogo() + "\n";
                if (data.getLogo().equals("Initials"))
                    fileContent+="Initials: " + data.getInitials();
                //File file = saveTextToFile(fileName, fileContent);

                //uploadFileToDrive(file);
                createDummyFile(fileContent);
                sendEmailWithAttachment(fileToSend);
            }
        });
    }

    private void sendEmailWithAttachment(File file) {
        // 2. Get the URI for the file using FileProvider
        // IMPORTANT: You MUST set up FileProvider in your AndroidManifest.xml and res/xml/file_paths.xml
        // (See step 3 below)
        Uri fileUri = null;
        try {
            fileUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        } catch (IllegalArgumentException e) {
            //Log.e(EMAILTAG, "The selected file can't be shared: " + e.getMessage());
            Toast.makeText(this, "Could not share file.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // Set the MIME type based on the file. Use "message/rfc822" for email if you don't know the exact type.
        // For a text file: "text/plain"
        // For a PDF: "application/pdf"
        // For an image: "image/jpeg"
        emailIntent.setType("text/plain"); // Or use getMimeType(file.getAbsolutePath()) for dynamic type
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"panda@peaceandcotton.com,gamedude30a@gmail.com"}); // Optional: Pre-fill recipients
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Attached File from Sweatshirt App");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find the attached document.");
        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri); // Attach the file URI

        // Grant read permission to the email app temporarily
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Verify that there's an app that can handle this intent
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            // Start the email client picker
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } else {
            Toast.makeText(this, "No email client found.", Toast.LENGTH_SHORT).show();
        }
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
                //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
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
            //Log.e(TAG, "Failed to init http transport");
            gse.printStackTrace();
        }

        mDriveService = new Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY, // for Android
                credential)
                .setApplicationName("SweatShirt") // Set your app name here
                .build();

        //Log.d(TAG, "Drive service initialized.");
        // Now mDriveService is ready to make API calls
    }



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
                //Log.e(TAG, "Error uploading file: " + e.getMessage(), e);
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

    private void createDummyFile(String content) {
        // Get the cache directory (good for temporary files)
        File cachePath = new File(getCacheDir(), "attachments");
        if (!cachePath.exists()) {
            cachePath.mkdirs(); // Create the directory if it doesn't exist
        }

        fileToSend = new File(cachePath, "sweatshirt.txt");

        try (FileOutputStream fos = new FileOutputStream(fileToSend)) {
            /*String content = "This is the content of the attached document.\n";
            content += "You can put any text data here.\n";
            content += "Hello from Android App!";*/
            fos.write(content.getBytes());
            //Log.d(TAG, "Dummy file created: " + fileToSend.getAbsolutePath());
        } catch (IOException e) {
            //Log.e(TAG, "Error creating dummy file: " + e.getMessage());
            Toast.makeText(this, "Failed to create dummy file.", Toast.LENGTH_SHORT).show();
            fileToSend = null; // Mark as null if creation failed
        }
    }

    private File saveTextToFile(String fileName, String content) {
        // Option 1: Using getFilesDir() for persistent private storage
        File file = new File(getFilesDir(), fileName);

        // Option 2: Using getCacheDir() for temporary private storage (uncomment to use)
        //File file = new File(getCacheDir(), fileName);

        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(fos);
             BufferedWriter writer = new BufferedWriter(osw)) { // Use BufferedWriter for efficiency

            writer.write(content);
            writer.flush(); // Ensure all buffered data is written to the file
            Toast.makeText(this, "File saved to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            //Log.d(TAG, "File saved: " + file.getAbsolutePath());

        } catch (IOException e) {
            //Log.e(TAG, "Error saving file: " + e.getMessage(), e);
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