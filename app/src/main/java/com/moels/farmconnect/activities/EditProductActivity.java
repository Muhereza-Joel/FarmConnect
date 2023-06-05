package com.moels.farmconnect.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moels.farmconnect.R;
import com.moels.farmconnect.utility_classes.ProductsDatabaseHelper;
import com.moels.farmconnect.utility_classes.UI;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 3;
    private static final int CAMERA_IMAGE_REQUEST = 4;
    private Toolbar toolbar;
    private ImageView productImageView;
    private TextView productNameEditView, productQuantityEditView, productPriceEditView;
    private ProductsDatabaseHelper productsDatabaseHelper;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        initUI();
        setUpStatusBar();
        setSupportActionBar(toolbar);
        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);
        UI.setUpActionBar(getSupportActionBar(),R.drawable.ic_back_arrow, "Edit Product", true);

        Drawable icon = toolbar.getOverflowIcon();
        if (icon != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setTint(ContextCompat.getColor(this, R.color.colorWhite));
                toolbar.setOverflowIcon(icon);
            }
        }

        productsDatabaseHelper = new ProductsDatabaseHelper(getApplicationContext());
        showProductDetails(productsDatabaseHelper.getProductDetails(getIntent().getStringExtra("productID")));
        productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProductActivity.this);
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
    }

    private void initUI(){
        toolbar = findViewById(R.id.edit_product_activity_toolbar);
        productImageView = findViewById(R.id.product_image_view);
        productNameEditView = findViewById(R.id.product_name_edit_text);
        productQuantityEditView = findViewById(R.id.product_quantity_edit_text);
        productPriceEditView = findViewById(R.id.product_price_edit_text);
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

    private void showProductDetails(List<String> productDetails){
        if (productDetails.size() > 0){
            Glide.with(getApplicationContext()).load(productDetails.get(0)).into(productImageView);
            productNameEditView.setText(productDetails.get(1));
            productQuantityEditView.setText(productDetails.get(2));
            productPriceEditView.setText(productDetails.get(3));
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
        getMenuInflater().inflate(R.menu.edit_product_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_edited_product_details_btn){
            if (validateEditViews()){
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Processing...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                replaceProductImage();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateEditViews(){
        //TODO Add functionality to check for the type of image
        boolean validated = true;
        if (TextUtils.isEmpty(productNameEditView.getText().toString())
                || TextUtils.isEmpty(productQuantityEditView.getText().toString())
                || TextUtils.isEmpty(productPriceEditView.getText().toString())){
            UI.displayToast(getApplicationContext(), "All fields are required");
            validated = false;
        }
        return validated;
    }

    private void replaceProductImage() {
        productImageView.setDrawingCacheEnabled(true);
        productImageView.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) productImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        String existingImageUrl = productsDatabaseHelper.getImageUrl(getIntent().getStringExtra("productID"));

        // Get the existing image reference
        StorageReference existingImageReference = storageReference.getStorage().getReferenceFromUrl(existingImageUrl);

        // Delete the existing image
        existingImageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Existing image deleted successfully, proceed with uploading the new image

                String timestamp = String.valueOf(System.currentTimeMillis());
                String randomString = UUID.randomUUID().toString();
                String imageName = timestamp + "_" + randomString + ".png";
                StorageReference imageReference = storageReference.child("ProductImages").child("0776579631").child(imageName);

                UploadTask uploadTask = imageReference.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String ex = e.getLocalizedMessage();
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
                                updateProduct(getUpdatedValuesFromUI(url), getIntent().getStringExtra("productID"));
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors that occur while deleting the existing image
            }
        });
    }


    private List<String> getUpdatedValuesFromUI(String imageUrl){
        List<String> values = new ArrayList<>();

        String productName = productNameEditView.getText().toString();
        String productQuantity = productQuantityEditView.getText().toString();
        String productPrice = productPriceEditView.getText().toString();

        values.add(imageUrl);
        values.add(productName);
        values.add(productQuantity);
        values.add(productPrice);

        return values;
    }

    private void updateProduct(List<String> values, String productID){
        ContentValues contentValues = new ContentValues();
        contentValues.put("imageUrl", values.get(0));
        contentValues.put("productName", values.get(1));
        contentValues.put("quantity", values.get(2));
        contentValues.put("price", values.get(3));

        boolean productIsUpdated = productsDatabaseHelper.updateProduct(productID, contentValues);
        if (productIsUpdated){
            UI.displayToast(getApplicationContext(), "Product Updated");
            //TODO start product edit service
        }
    }

}