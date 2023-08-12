package com.moels.farmconnect.utils.preferences;

import androidx.annotation.NonNull;

public class Globals {
    public static final String AUTHENTICATED_PHONE_NUMBER = "authenticatedPhoneNumber";
    public  static final String ACTIVE = "active";
    public static final String BUYER_ACCOUNT = "";
    public static final String SELLER_ACCOUNT = "";


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
