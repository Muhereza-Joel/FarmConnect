package com.moels.farmconnect.model.database;

import android.content.Context;

import java.util.List;

public interface ProductZoneMappingTable {

    static ProductZoneMappingTable getInstance(Context context){
        return ProductZoneMappingTableUtil.getInstance(context);
    }
    void addNewMapping(String productID, String zoneID);
    List<String> getProductMappings(String productID);
    List<String> getZoneMappings(String zoneID);
    boolean updateProductMapping(String currentZoneId, String targetZoneId, String productId);
    boolean deleteProductMapping(String productId, String zoneId);

}
