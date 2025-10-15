/*
 * Copyright (C) 2016 mendhak
 *
 * This file is part of GPSLogger for Android.
 *
 * GPSLogger for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * GPSLogger for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.example.registro_cuentas.ex;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
//import com.mendhak.gpslogger.R;
//import com.mendhak.gpslogger.common.slf4j.Logs;
//import com.mendhak.gpslogger.loggers.Files;
//import com.mendhak.gpslogger.common.AppSettings;

import com.example.registro_cuentas.StartVar;

import org.slf4j.Logger;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class PreferenceHelper {

    private static PreferenceHelper instance = null;
    private SharedPreferences prefs;
    private static final Logger LOG = Logs.of(PreferenceHelper.class);

    //Test------------------------------------
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    //---------------------------------------
    /**
     * Use PreferenceHelper.getInstance()
     */
    private PreferenceHelper( ){
    }

    public static PreferenceHelper getInstance(){
        if(instance==null){
            instance = new PreferenceHelper();
            instance.prefs = PreferenceManager.getDefaultSharedPreferences(StartVar.mContex/*AppSettings.getInstance().getApplicationContext()*/);
        }

        return instance;
    }

    @ProfilePreference(name=PreferenceNames.AUTOSEND_GOOGLE_DRIVE_ENABLED)
    public boolean isGoogleDriveAutoSendEnabled() {
        return prefs.getBoolean(PreferenceNames.AUTOSEND_GOOGLE_DRIVE_ENABLED, false);
    }

    public String getGoogleDriveAuthState(){
        return prefs.getString(PreferenceNames.GOOGLE_DRIVE_AUTH_STATE, null);
    }

    public void setGoogleDriveAuthState(String auth_state_json_serialized){
        prefs.edit().putString(PreferenceNames.GOOGLE_DRIVE_AUTH_STATE, auth_state_json_serialized).apply();
    }

    @ProfilePreference(name=PreferenceNames.GOOGLE_DRIVE_FOLDER_PATH)
    public String getGoogleDriveFolderPath() {
        return prefs.getString(PreferenceNames.GOOGLE_DRIVE_FOLDER_PATH, "Cow-Data-Save");
    }

    public void setGoogleDriveFolderPath(String folderPath){
        prefs.edit().putString(PreferenceNames.GOOGLE_DRIVE_FOLDER_PATH, folderPath).apply();
    }

    @ProfilePreference(name=PreferenceNames.GOOGLE_DRIVE_IMG_PATH)
    public String getGoogleDriveImgPath() {
        return prefs.getString(PreferenceNames.GOOGLE_DRIVE_IMG_PATH, "Img");
    }

    public void setGoogleDriveImgPath(String folderPath){
        prefs.edit().putString(PreferenceNames.GOOGLE_DRIVE_IMG_PATH, folderPath).apply();
    }


    @ProfilePreference(name=PreferenceNames.GOOGLE_DRIVE_FOLDER_ID)
    public String getGoogleDriveFolderId() {
        return prefs.getString(PreferenceNames.GOOGLE_DRIVE_FOLDER_ID, null);
    }

    public void setGoogleDriveFolderId(String folderId){
        prefs.edit().putString(PreferenceNames.GOOGLE_DRIVE_FOLDER_ID, folderId).apply();
    }

    @ProfilePreference(name=PreferenceNames.GOOGLE_DRIVE_FOLDER_IMG_ID)
    public String getGoogleDriveFolderImgId() {
        return prefs.getString(PreferenceNames.GOOGLE_DRIVE_FOLDER_IMG_ID, null);
    }

    public void setGoogleDriveFolderImgId(String folderId){
        prefs.edit().putString(PreferenceNames.GOOGLE_DRIVE_FOLDER_IMG_ID, folderId).apply();
    }

    /**
     * GPS Logger folder path on phone.  Falls back to  if nothing specified.
     */
    @ProfilePreference(name= PreferenceNames.GPSLOGGER_FOLDER)
    public String getGpsLoggerFolder() {
        return prefs.getString(PreferenceNames.GPSLOGGER_FOLDER, storageFolder(StartVar.mContex).getAbsolutePath());
    }

    public static File storageFolder(Context context){
        File storageFolder = context.getExternalFilesDir(null);
        if(storageFolder == null){
            storageFolder = context.getFilesDir();
        }
        return storageFolder;
    }

    /**
     * Whether to autosend only if wifi is enabled
     */
    @ProfilePreference(name= PreferenceNames.AUTOSEND_WIFI_ONLY)
    public boolean shouldAutoSendOnWifiOnly() {
        return prefs.getBoolean(PreferenceNames.AUTOSEND_WIFI_ONLY, false);
    }

    @ProfilePreference(name=PreferenceNames.GALLERY_POSITION)
    public int getGalleryPosition() {
        return prefs.getInt(PreferenceNames.GALLERY_POSITION, 0);
    }

    @ProfilePreference(name=PreferenceNames.GALLERY_OFFSET)
    public int getGalleryOffset() {
        return prefs.getInt(PreferenceNames.GALLERY_OFFSET, 0);
    }

    public void setGalleryPosition(int position, int offset){
        prefs.edit().putInt(PreferenceNames.GALLERY_POSITION, position).apply();
        prefs.edit().putInt(PreferenceNames.GALLERY_OFFSET, offset).apply();
    }
}
