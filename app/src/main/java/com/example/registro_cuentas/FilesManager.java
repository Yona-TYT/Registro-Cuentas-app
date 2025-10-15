package com.example.registro_cuentas;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.registro_cuentas.activitys.MainActivity;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class FilesManager extends MainActivity implements View.OnClickListener{

    public static ImageView mImgPrev;
    public static Uri currUri;

    public FilesManager(){
    }

    public void setImgPicker(ImageView mImg, View mView){
        mView.setTag("pick");
        mView.setOnClickListener(this);
        FilesManager.mImgPrev = mImg;
    }

    public String getImage(String sImage, ImageView mImgPrev) {
        if (!sImage.isEmpty()) {
            Uri mUri = Uri.fromFile(new File(sImage));
            try {
                if (isBlockedPath(this, sImage)) {
                    mImgPrev.setImageURI(mUri);
                    return  sImage;
                }
                else {
                    Log.d("PhotoPicker", "noooooo hayyyyyyyyyy: " + sImage);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sImage;
    }

    public String SavePhoto(Bitmap bmp, String fName, Uri oldFile, Context contex, ContentResolver resolver){

        //Creamos el directorio para los archivos
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/.accdata/");
        boolean isDiralloway = true;
        if(!path.exists()){
            isDiralloway = path.mkdir();
        }
        //------------------------------------------

        //Si se crea correctamente entonces procede a escribir
        if(isDiralloway) {
            File file = new File(path, fName);
            FileOutputStream stream = null;

//            Log.d("PhotoPicker", " Aquiiiiiiiiii Hayyyyyy 11100------------------------: " );

            try {
                stream = new FileOutputStream(file);

                // Use the compress method on the BitMap object to write image to the OutputStream
                if(!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)){
                    throw new RuntimeException("Could Save Bit map");
                }
                else {
                    return file.getAbsolutePath();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {
                    stream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public File csvExport(List<String[]> list) throws IOException {
        // Definimos la class
        CsvWriterSimple write = new CsvWriterSimple();

        //Creamos el directorio para los archivos
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+StartVar.dirAppName);
        boolean isDiralloway = true;
        if(!path.exists()){
            isDiralloway = path.mkdir();
        }
        //------------------------------------------

        //Si se crea correctamente entonces procede a escribir
        if(isDiralloway) {
            LocalDate currdate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                currdate = LocalDate.now();
            }
            String name = (currdate == null? "CowData_Save.csv" : "RegistroDatos_"+currdate.toString()+".csv" );
            File file = new File(path, name);
            write.writeToCsvFile(list, file);
            return file;
        }
        //-----------------------------------------------------------
        return null;
    }

    public File csvExport(List<String[]> list, String fileName) throws IOException {
        // Definimos la class
        CsvWriterSimple write = new CsvWriterSimple();

        File path = directoryCreate();

        //Si se crea correctamente entonces procede a escribir
        if (path != null) {
            String myName = "CowData_Save.csv";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                myName = "CowData_" + LocalDate.now().toString() + "_" + (LocalTime.now().toString().replaceAll("\\D", "_")) + ".csv";
            }
            File file = new File(path, fileName.isEmpty() ? myName : fileName);
            write.writeToCsvFile(list, file);
            return file;
        }
        //-----------------------------------------------------------
        return null;
    }

    public static File directoryCreate() {
        //Creamos el directorio para los archivos

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + StartVar.dirAppName);
        boolean isDiralloway = true;
        if (!path.exists()) {
            isDiralloway = path.mkdir();
        }

        if(!isDiralloway){
            return null;
        }
        return path;
    }

    public boolean csvImport(String dir) throws IOException, CsvValidationException {
        Log.d("PhotoPicker", " Aquiiiiiiiiii Hayyyyyy ------------------------: "+ dir );

        //Se detecta si el archivo existe
        File file = new File(dir);
        if(file.exists()){
            CSVReader reader = new CSVReader(new FileReader(file));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                //System.out.println(nextLine[0] + nextLine[1] + "etc...");
                Log.d("PhotoPicker", " Aquiiiiiiiiii Hayyyyyy ------------------------: "+ nextLine[0] );
            }
        }
        //-----------------------------------------------------------
        return true;
    }

    public static void DeleteFile(File file) {

        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                File currFile = new File(file, children[i]);
                String name = currFile.getName();
                if(name.endsWith(".csv")) {
                    //Log.d("PhotoPicker", " Aquiiiiiiiiii Hayyyyyy ------------------------: " + name);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        boolean threis = currFile.exists();
                        if(threis) {
                            currFile.delete();
                        }
                    }
                }
            }
        }
    }


    public void RemoveFile(String dir, ContentResolver resolver) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
           // Log.d("PhotoPicker", " =======================Aquiiiiiiiiii Hayyyyyy 11100------------------------: " );
            File file = new File(dir);
            boolean threis = file.exists();
            if(threis) {
                file.delete();
            }
        }
    }
    public boolean nameCompare(String a, String b) {
        // Paths that should rarely be exposed
        if (a.startsWith(b)){
            return true;
        }
        return false;
    }

    boolean isBlockedPath(Context ctx, String dir) {
        // Paths that should rarely be exposed
        return dir.startsWith("content://media/" + MediaStore.VOLUME_EXTERNAL_PRIMARY) || dir.startsWith("/storage/emulated/0/Documents/");
    }

    @Override
    public void onClick(View view) {
        Object itemTag = view.getTag();
        if (Objects.equals((String)itemTag, "pick")) {
            if (StartVar.mPermiss){
                // Launch the photo picker and let the user choose only images.
                //fmang.FilesManager();
                pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
            }
            else{
                Basic.msg("Error Permiso Denegado!");
            }
        }
    }

    // Registers a photo picker activity launcher in single-select mode.
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    FilesManager.mImgPrev.setImageURI(uri);
                    FilesManager.currUri = uri;
                }
                else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });


    // Método para copiar el archivo con un nuevo nombre
    public static File getNewFile(String rutaOriginal, String newName, Context context) throws IOException {
        File originalFile = new File(rutaOriginal);
        if (!originalFile.exists()) {
            return null; // El archivo original no existe
        }

        // Crear un nuevo archivo en el directorio de caché o almacenamiento interno
        File newFile = new File(context.getCacheDir(), newName);
        // Copiar contenido del archivo original al nuevo archivo
        try (FileInputStream in = new FileInputStream(originalFile);
             FileOutputStream out = new FileOutputStream(newFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        return newFile;
    }

    public static File getFileFromUri(Context context, Uri uri) throws IOException {
        // Verificar si la Uri es null
        if (uri == null) {
            throw new IOException("Uri es null");
        }

        // Obtener el ContentResolver
        String fileName = getFileName(context, uri);
        File file = new File(context.getCacheDir(), fileName != null ? fileName : "temp_file");

        // Copiar el contenido de la Uri a un archivo temporal
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            if (inputStream != null) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }

        return file;
    }

    // Método para obtener el nombre del archivo desde la Uri (opcional)
    private static String getFileName(Context context, Uri uri) {
        String fileName = null;
        String[] projection = { android.provider.MediaStore.MediaColumns.DISPLAY_NAME };

        try (android.database.Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns.DISPLAY_NAME);
                fileName = cursor.getString(nameIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileName;
    }

}
