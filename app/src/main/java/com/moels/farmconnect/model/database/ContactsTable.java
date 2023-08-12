package com.moels.farmconnect.model.database;

import java.util.List;

public interface ContactsTable {
    boolean addContactToDatabase(List<String> contactDetails);
    List<String> getAllRegisteredContacts();
    String getOwnerImageUrl(String phoneNumber);
    String getOwnerUsername(String phoneNumber);
}
