package com.moels.farmconnect.model.database;

import android.content.Context;

import com.moels.farmconnect.utils.models.ContactCardItem;

import java.util.List;

public interface ContactsTable {
    static ContactsTable getInstance(Context context){
        return ContactsTableUtil.getInstance(context);
    }

    List<ContactCardItem> getAllContacts();
    boolean addContactToDatabase(List<String> contactDetails);
    List<String> getAllPhoneNumbers();
    String getOwnerImageUrl(String phoneNumber);
    String getOwnerUsername(String phoneNumber);
}
