package com.example.registro_cuentas;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.util.function.Consumer;

public class CameraLauncher extends Fragment implements DefaultLifecycleObserver {
    private final ActivityResultRegistry registry;
    private final Context context;
    private final Consumer<Uri> onCapture;

    private ActivityResultLauncher<String> cameraPermissionLauncher;
    //private ActivityResultLauncher<Intent> cameraLauncher;

    // Registers a photo picker activity launcher in single-select mode.
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    public static ImageView mImgPrev;
    public static Uri currUri;

    private Uri captureImageUri;

    public CameraLauncher(ActivityResultRegistry registry, Context context, Consumer<Uri> onCapture) {
        this.registry = registry;
        this.context = context;
        this.onCapture = onCapture;
    }

    @Override
    public void onCreate(LifecycleOwner owner) {
        cameraPermissionLauncher = registry.register(
                "KEY_CAMERA_PERMISSION_LAUNCHER",
                owner,
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        openAppSettings(context);
                    }
                }
        );

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    onCapture.accept(captureImageUri);
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    CameraLauncher.mImgPrev.setImageURI(uri);
                    CameraLauncher.currUri = uri;
                }
                else {
                    Log.d("PhotoPicker", "No media selected");
                }
        });
    }




    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), "capture-" + System.currentTimeMillis() + ".jpg");
        file.deleteOnExit();
        captureImageUri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                file
        );
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureImageUri);
        pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
    }

    public void openAppSettings(Context context) {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.getPackageName(), null)
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void launch() {
//        if (isPermissionGranted(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
//            openCamera();
//        }
//        else {
//            cameraPermissionLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
//        }
        if (StartVar.mPermiss){
            // Launch the photo picker and let the user choose only images.
            //fmang.FilesManager();
            pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
        }
        else{
            Basic.msg("Error Permiso Denegado!");
        }
    }

    private boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(
                context,
                permission
        ) == PackageManager.PERMISSION_GRANTED;
    }
}