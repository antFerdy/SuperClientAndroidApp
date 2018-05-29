package zhur.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhur on 03.09.2016.
 */
public class FileStorageManager {
    private static FileStorageManager instance = null;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    private DatabaseManager db;
    private byte[] fData;

    public static FileStorageManager getInstance() {
        if(instance == null)
            instance = new FileStorageManager();

        return instance;
    }

    private FileStorageManager() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://superclient-2171f.appspot.com/media/");
        db = DatabaseManager.getInstance();
    }


    public void upload(byte[] data, final String format) {
        fData = data;

        //Формируем имя файла
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "." + format;

        imagesRef = storageRef.child(imageFileName);
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("FILE UPLOAD", exception.toString());
                FirebaseCrash.report(exception);
                //сохраняем на файлуху
                //saveToFileSystem(fData, format);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri url = taskSnapshot.getDownloadUrl();
                FirebaseCrash.log("Data saved " + url.toString());
                //save to database
                if(format.equalsIgnoreCase("jpeg")) {
                    db.getReview().setPhotoUrl(url.toString());
                } else {
                    db.getReview().setVideoUrl(url.toString());
                }

                db.save();
            }
        });
    }

    //save to filesystem
    private void saveToFileSystem(byte[] data, String format) {
        //Bitmap bitMap = decodeFile(data);
        try {
            if(isExternalStorageWritable()) {
                File image = createImageFile(format);
                if(image.exists())
                    return;

                FileOutputStream fos = new FileOutputStream(image);
//                bitMap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.write(data);
                fos.flush();
                fos.close();

                //galleryAddPic(image.getAbsolutePath());
                //mCamera.release();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private Bitmap decodeFile(byte[] data) {
//        Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data .length);
//        return bitmap;
//    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private File createImageFile(String format) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Media_" + timeStamp + "." + format;

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/mediaStorage");
        if(!myDir.exists())
            myDir.mkdirs();

        File image = new File(myDir, imageFileName);
        return image;
    }

//    private void galleryAddPic(String mCurrentPhotoPath) {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }

}
