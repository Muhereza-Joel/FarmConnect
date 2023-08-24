package com.moels.farmconnect.utils.preferences;

import androidx.annotation.NonNull;

public final class Globals {
    public static final String AUTHENTICATED_PHONE_NUMBER = "authenticatedPhoneNumber";
    public static final String ACTIVE = "active";
    public static final String CLOSED = "closed";
    public static final String BUYER_ACCOUNT = "";
    public static final String SELLER_ACCOUNT = "";
    public static final String PRODUCT_AVAILABLE = "available";
    public static final String PRODUCT_SOLD = "sold";
    public static final String PRODUCT_OUT_STOCK = "out of stock";

    public static final String PRODUCT_ID = "productID";
    public static final String ZONE_ID = "zoneID";
    public static final String EXTERNAL_STORAGE_FOLDER = "FarmConnect";
    public static final String MEDIA_SUBFOLDER = "Media";
    public static final String IMAGES_FOLDER = "Images";
    public static final String AUDIO_FOLDER = "Audio";
    public static final String VIDEO_FOLDER = "Video";
    public static final String PROFILE_PHOTOS = "Profile Photos";
    public static final String PRODUCT_IMAGES_SUBFOLDER = "Product Images";
    public static final String THUMBNAILS_SUBFOLDER = "Thumbnails";
    public static final String PRODUCT_STATUS_UPDATE_MSG = "Product Status Updated..";
    public static final String ZONE_STATUS_UPDATE_MSG = "Zone Status Updated..";

    public enum UploadStatus{
        FALSE, TRUE;

        @NonNull
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public enum UpdateStatus{
        FALSE, TRUE;

        @NonNull
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }
}
