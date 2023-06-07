package com.moels.farmconnect.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moels.farmconnect.R;
import com.moels.farmconnect.models.User;
import com.moels.farmconnect.utility_classes.UI;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private Button chooseProfilePicButton;
    private EditText phoneNumber, dateOfBirthEditText, username;
    private ImageView profilePicImageView;
    private Toolbar toolbar;
    RadioGroup accountTypeRadioGroup;
    private RadioButton buyerRadioButton, farmerRadioButton;
    private Spinner genderSpinner;

    private ProgressDialog progressDialog;
    Uri croppedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        initUI();
        setUpStatusBar();
        setSupportActionBar(toolbar);
        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);

        UI.setUpActionBar(getSupportActionBar(),"Create Profile");

        GradientDrawable circleDrawable = new GradientDrawable();
        circleDrawable.setShape(GradientDrawable.OVAL);
        circleDrawable.setColor(Color.WHITE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Drawable image = getDrawable(R.drawable.avatar2);
            Glide.with(this).load(image).circleCrop().into(profilePicImageView);
        }

//        new ProgressDialog().show(getSupportFragmentManager(), "sample");

        phoneNumber.setText(getIntent().getStringExtra("phoneNumber"));

        chooseProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(CreateProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    // permission is already granted, launch the image selection activity
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                } else {
                    ActivityCompat.requestPermissions(CreateProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                }

            }
        });

        dateOfBirthEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of the DatePickerDialog class
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CreateProfileActivity.this, // Context
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Do something with the selected date
                                String selectedDateOfBirth = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                dateOfBirthEditText.setText(selectedDateOfBirth);
                            }
                        }, // Listener to be called when the user sets the date
                        // Default date to show in the picker (optional)
                        2000, // Year
                        0, // Month (January is 0)
                        1 // Day
                );
                datePickerDialog.show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission is granted
                // launch the image selection activity
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            } else {
                // permission is denied
                // show a message explaining why the permission is needed
                Toast.makeText(this, "Allow FarmConnect To Read Your Files", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri uri = data.getData();
            CropImage.activity(uri)
                            .setAspectRatio(1,1) // Set aspect ratio to 1:1 (square)
                            .setGuidelines(CropImageView.Guidelines.ON) // Show guidelines to help with cropping
                            .start(this);
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK && data != null) {

            // Get the cropped image URI
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            croppedImageUri = result.getUri();

            // Load the cropped image into the ImageView using Glide
            Glide.with(this)
                    .load(croppedImageUri)
                    .circleCrop()
                    .into(profilePicImageView);
        }
    }

    //Attach menu icons to the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save){
            int checkedRadioGroupButton = accountTypeRadioGroup.getCheckedRadioButtonId();
            RadioButton checkedRadioButton = findViewById(checkedRadioGroupButton);
            String selectedValue = checkedRadioButton.getText().toString();
            String fullName = username.getText().toString();
            String authenticatedPhoneNumber = phoneNumber.getText().toString();
            String birthDate  = dateOfBirthEditText.getText().toString();
            String gender = genderSpinner.getSelectedItem().toString();

            profilePicImageView.setDrawingCacheEnabled(true);
            profilePicImageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable)profilePicImageView.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();

            String timestamp = String.valueOf(System.currentTimeMillis());
            String randomString = UUID.randomUUID().toString();
            String imageName = timestamp + "_" + randomString + ".png";

            SharedPreferences myAppPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = myAppPreferences.edit();

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Processing...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageReference imageReference = storageReference.child("ProfileImages").child(myAppPreferences.getString("authenticatedPhoneNumber", "123456789")).child(imageName);
            UploadTask uploadTask = imageReference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                    downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            User user = new User(fullName,gender, authenticatedPhoneNumber, birthDate, selectedValue, url);
                            uploadProfileData(editor, user);
                            saveChosenAccount(editor);
                        }
                    });
                }
            });


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadProfileData(SharedPreferences.Editor editor, User user) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference profileReference = databaseReference.child("userAccounts");

        // Check if the user already exists in the database
        Query query = profileReference.orderByChild("phoneNumber").equalTo(user.getPhoneNumber());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User already exists, handle accordingly
                    UI.displayToast(getApplicationContext(), "Profile already exists");
                    editor.putBoolean("profileCreated", true);
                    editor.apply();
                    progressDialog.dismiss();
                    startFinishSetUpActivity();
                } else {
                    // User doesn't exist, proceed with uploading
                    profileReference.push().setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            UI.displayToast(getApplicationContext(), "Profile Data Uploaded");
                            Log.d("On Success", "Uploaded data to Firebase Realtime Database");
                            editor.putBoolean("profileCreated", true);
                            editor.apply();
                            progressDialog.dismiss();
                            startFinishSetUpActivity();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            UI.displayToast(getApplicationContext(), "Failed to create your profile");
                            Log.d("On Failure", "Failed to upload data to Firebase Realtime Database");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors in the database operation
                UI.displayToast(getApplicationContext(), "Failed to check profile existence");
                Log.d("Database Error", databaseError.getMessage());
            }
        });
    }


    private void saveChosenAccount(SharedPreferences.Editor editor){
        if (buyerRadioButton.isChecked() == true){
            editor.putBoolean("buyerAccountTypeChosen", true);
            editor.putBoolean("farmerAccountTypeChosen", false);
            editor.apply();
        }

        if (farmerRadioButton.isChecked() == true){
            editor.putBoolean("farmerAccountTypeChosen", true);
            editor.putBoolean("buyerAccountTypeChosen", false);
            editor.apply();
        }
    }
    private void startFinishSetUpActivity(){
        Intent intent = new Intent(CreateProfileActivity.this, FinishSetUpActivity.class);
        startActivity(intent);
        finish();
    }

    private void initUI(){
        username = findViewById(R.id.name_edit_text);
        toolbar = findViewById(R.id.my_toolbar);
        dateOfBirthEditText = findViewById(R.id.birthdate_edit_text);
        profilePicImageView = findViewById(R.id.profile_pic_image_view);
        phoneNumber = findViewById(R.id.phone_number_edit_text);
        chooseProfilePicButton = findViewById(R.id.select_profile_pic_button);
        buyerRadioButton = findViewById(R.id.buyer_user_type_radio_button);
        farmerRadioButton = findViewById(R.id.farmer_user_type_radio_button);
        genderSpinner = findViewById(R.id.gender);
        accountTypeRadioGroup = findViewById(R.id.use_type_radio_group);
    }

    private void setUpStatusBar() {
        Window window = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
            int currentMode = uiModeManager.getNightMode();
            if (currentMode == UiModeManager.MODE_NIGHT_YES) {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));
            }else {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            }
        }

    }
}