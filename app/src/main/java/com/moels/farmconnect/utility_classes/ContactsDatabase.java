package com.moels.farmconnect.utility_classes;

import java.util.List;

public interface ContactsDatabase {
    boolean addContactToDatabase(List<String> contactDetails);
    List<String> getAllRegisteredContacts();
    String getOwnerImageUrl(String phoneNumber);
    String getOwnerUsername(String phoneNumber);
}
