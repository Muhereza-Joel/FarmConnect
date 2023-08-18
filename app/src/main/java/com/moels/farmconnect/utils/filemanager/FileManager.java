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

        File audioSubFolder = new File(mediaSubfolder, Globals.AUDIO_FOLDER);
        if (!audioSubFolder.exists()) audioSubFolder.mkdirs();

        File videoSubfolder = new File(mediaSubfolder, Globals.VIDEO_FOLDER);
        if (!videoSubfolder.exists()) videoSubfolder.mkdirs();

        File profilePhotosSubfolder = new File(imagesSubfolder, Globals.PROFILE_PHOTOS);
        if (!profilePhotosSubfolder.exists()) profilePhotosSubfolder.mkdirs();

        File profilePhotosThumbnailsFolder = new File(profilePhotosSubfolder, Globals.THUMBNAILS_SUBFOLDER);
        if (!profilePhotosThumbnailsFolder.exists()) profilePhotosThumbnailsFolder.mkdirs();

        File productImagesFolder = new File(imagesSubfolder, Globals.PRODUCT_IMAGES_SUBFOLDER);
        if (!productImagesFolder.exists()) productImagesFolder.mkdirs();

        File productImagesThumbnailsFolder = new File(productImagesFolder, Globals.THUMBNAILS_SUBFOLDER);
        if (!productImagesThumbnailsFolder.exists()) productImagesThumbnailsFolder.mkdirs();


    }
}
