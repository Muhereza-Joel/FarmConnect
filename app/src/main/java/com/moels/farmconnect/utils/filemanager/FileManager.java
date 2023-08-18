package com.moels.farmconnect.utils.filemanager;

import com.moels.farmconnect.utils.preferences.Globals;

import java.io.File;

public class FileManager {
    public static void createMediaStorageFolders(){

        File farmConnectFolder = new File(android.os.Environment.getExternalStorageDirectory(), Globals.EXTERNAL_STORAGE_FOLDER);
        if (!farmConnectFolder.exists()) farmConnectFolder.mkdirs();

        File mediaSubfolder = new File(farmConnectFolder, Globals.MEDIA_SUBFOLDER);
        if (!mediaSubfolder.exists()) mediaSubfolder.mkdirs();

        File imagesSubfolder = new File(mediaSubfolder, Globals.IMAGES_FOLDER);
        if (!imagesSubfolder.exists()) imagesSubfolder.mkdirs();

        File productImagesFolder = new File(imagesSubfolder, Globals.PRODUCT_IMAGES_SUBFOLDER);
        if (!productImagesFolder.exists()) productImagesFolder.mkdirs();

        File productImagesThumbnailsFolder = new File(imagesSubfolder, Globals.PRODUCT_IMAGES_THUMBNAILS_SUBFOLDER);
        if (!productImagesThumbnailsFolder.exists()) productImagesThumbnailsFolder.mkdirs();


    }
}
