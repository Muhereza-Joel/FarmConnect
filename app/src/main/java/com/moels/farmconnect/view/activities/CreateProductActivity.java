package com.moels.farmconnect.view.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moels.farmconnect.R;
import com.moels.farmconnect.model.database.services.ProductsUploadService;
import com.moels.farmconnect.utils.models.Product;
import com.moels.farmconnect.utils.preferences.FarmConnectAppPreferences;
import com.moels.farmconnect.utils.preferences.Globals;
import com.moels.farmconnect.utils.preferences.Preferences;
import com.moels.farmconnect.model.database.ProductsTable;
import com.moels.farmconnect.model.database.ProductsTableUtil;
import com.moels.farmconnect.utils.UI;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CreateProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_IMAGE_REQUEST = 2;
    private Toolbar toolbar;
    private ImageView productImageView;
    private EditText productNameEditText, productQuantityEditText, productUnitPriceEditText;
    private Spinner quantityUnitsSpinner;
    private TextView productPriceTextView;
    private ProgressDialog progressDialog;
    private ProductsTable productsDatabase;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        initUI();
        setUpStatusBar();
        setSupportActionBar(toolbar);
        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Create New Product", true);

        productsDatabase = ProductsTableUtil.getInstance(getApplicationContext());
        preferences = FarmConnectAppPreferences.getInstance(getApplicationContext());

        productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateProductActivity.this);
                builder.setTitle("Choose An Option");
                String[] options = {"Chose Photo From Gallery", "Take Photo Using Camera"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Gallery
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
                                break;
                            case 1: // Camera
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST);
                                break;
                        }
                    }
                });
                builder.show();
            }

        });

        productQuantityEditText.addTextChangedListener(createTextWatcher());
        productUnitPriceEditText.addTextChangedListener(createTextWatcher());
    }

    private void initUI(){
        toolbar = findViewById(R.id.create_product_activity_toolbar);
        productImageView = findViewById(R.id.product_image_view);
        productNameEditText = findViewById(R.id.product_name_edit_text);
        productQuantityEditText = findViewById(R.id.product_quantity_edit_text);
        productUnitPriceEditText = findViewById(R.id.product_unit_price_edit_text);
        productPriceTextView = findViewById(R.id.product_auto_price_text_view);
        quantityUnitsSpinner = findViewById(R.id.units_spinner);
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

    private TextWatcher createTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotalPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void calculateTotalPrice(){
        String quantityString = productQuantityEditText.getText().toString();
        String unitPriceString = productUnitPriceEditText.getText().toString();

        if (!TextUtils.isEmpty(quantityString) && !TextUtils.isEmpty(unitPriceString)){
            double quantity = Double.parseDouble(quantityString);
            double unitPrice = Double.parseDouble(unitPriceString);
            long totalPrice = (long) (quantity * unitPrice);
            productPriceTextView.setText(String.format("%s", totalPrice));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                Uri uri = data.getData();
                CropImage.activity(uri)
                        .setActivityTitle("Crop Image")
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            } else if (requestCode == CAMERA_IMAGE_REQUEST && data != null && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri uri = getImageUri(this, photo);
                CropImage.activity(uri)
                        .setActivityTitle("Crop Image")
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri croppedImageURI = result.getUri();
                Glide.with(this)
                        .load(croppedImageURI)
                        .into(productImageView);
            }
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_product_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save){
            if (validateViews() == true){
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Processing...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                uploadProductImage();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadProductImage(){
        enableDrawingCacheOnImageView();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference imageReference = storageReference.child("ProductImages").child(preferences.getString("authenticatedPhoneNumber")).child(generateImageName());

        UploadTask uploadTask = imageReference.putBytes(getImageFromView());
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String ex = e.getLocalizedMessage();
                progressDialog.dismiss();
                UI.displayToast(getApplicationContext(), "An Error Occurred");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL
                Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        progressDialog.dismiss();

                        boolean productIsCreated = productsDatabase.addProduct(getValuesFromUI(url), getIntent().getStringExtra(Globals.ZONE_ID));
                        if (productIsCreated){
                            resetUI();
                            View parentView = findViewById(R.id.parent);
                            UI.displaySnackBar(getApplicationContext(), parentView, "Product Added Successfully!!");
                            if (getIntent().getStringExtra(Globals.ZONE_ID) != null){
                                startProductsUploadService(getIntent().getStringExtra(Globals.ZONE_ID));
                            }


                        }
                    }
                });

            }
        });
    }

    private boolean validateViews(){
        boolean validated = true;
        Drawable productImageDrawable = productImageView.getDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (productImageDrawable instanceof VectorDrawable) {
                UI.displayToast(getApplicationContext(), "Please select an image");
                validated = false;
            } else if (!validateEditTexts()){
                    UI.displayToast(getApplicationContext(), "All fields are required");
                    validated = false;

            }else if (!validateSelectedUnit()){
                UI.displayToast(getApplicationContext(), "Please Select A Unit");
                validated = false;
            }
        }
        return validated;
    }

    private boolean validateEditTexts(){
        //TODO change data validation to do through the Validator
        boolean validated = true;
        if (TextUtils.isEmpty(productNameEditText.getText().toString())
                || TextUtils.isEmpty(productQuantityEditText.getText().toString())
                || TextUtils.isEmpty(productPriceTextView.getText().toString())
                || TextUtils.isEmpty(productUnitPriceEditText.getText().toString())){
            validated = false;

        }
        return validated;
    }

    private boolean validateSelectedUnit(){
        boolean validated = true;
        if (quantityUnitsSpinner.getSelectedItem().toString().equals("Choose Unit")){
            validated = false;
        }
        return validated;
    }

    private void enableDrawingCacheOnImageView(){
        productImageView.setDrawingCacheEnabled(true);
        productImageView.buildDrawingCache();
    }

    private String generateImageName(){
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomString = UUID.randomUUID().toString();
        return (timestamp + "_" + randomString + ".png");

    }

    private byte[] getImageFromView(){
        Bitmap bitmap = ((BitmapDrawable)productImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        return data;
    }

    private Product getValuesFromUI(String imageUrl){

        return new Product()
                .setProductID(generateUniqueID())
                .setProductName(productNameEditText.getText().toString())
                .setQuantity(combineQuantityWithUnit())
                .setUnitPrice(productUnitPriceEditText.getText().toString())
                .setPrice(productPriceTextView.getText().toString())
                .setImageUrl(imageUrl)
                .setUploadStatus(Globals.UploadStatus.FALSE.toString())
                .setUpdatedStatus(Globals.UpdateStatus.FALSE.toString())
                .setOwner(preferences.getString(Globals.AUTHENTICATED_PHONE_NUMBER))
                .setCreateDate(getCurrentDate())
                .setCreateTime(getCurrentTime())
                .setStatus(Globals.PRODUCT_AVAILABLE);

    }

    private String combineQuantityWithUnit(){
        String quantity = productQuantityEditText.getText().toString();
        String unit = quantityUnitsSpinner.getSelectedItem().toString();
        return (quantity + " " + unit);
    }

    private static String generateUniqueID(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = simpleDateFormat.format(new Date());

        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        String zoneId = currentTime + randomNumber;
        return zoneId;
    }

    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(calendar.getTime());
        return formattedDate;
    }

    private String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
        String formattedTime = timeFormat.format(calendar.getTime());
        return formattedTime;
    }

    private void startProductsUploadService(String zoneID) {
        if (zoneID != null && !zoneID.isEmpty()) {
            Intent serviceIntent = new Intent(getApplicationContext(), ProductsUploadService.class);
            serviceIntent.putExtra(Globals.ZONE_ID, zoneID);
            startService(serviceIntent);
        }
    }

    private void resetUI(){
        productImageView.setImageResource(R.drawable.baseline_insert_photo_24);
        productNameEditText.setText("");
        productQuantityEditText.setText("");
        productUnitPriceEditText.setText("");
        productPriceTextView.setText("");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateProductActivity.this, AddProductToZoneActivity.class);
        intent.putExtra(Globals.ZONE_ID, getIntent().getStringExtra(Globals.ZONE_ID));
        intent.putExtra("zoneName", getIntent().getStringExtra("zoneName"));
        startActivity(intent);
        finish();
    }


}