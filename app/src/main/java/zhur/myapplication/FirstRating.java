package zhur.myapplication;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import java.io.IOException;
import java.util.List;

public class FirstRating extends AppCompatActivity implements RatingBar.OnRatingChangeListener, Camera.PictureCallback{
    private DatabaseManager dbManager;
    private Review newReview;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_rating);

        RatingBar bar = (RatingBar) findViewById(R.id.rating);
        bar.setOnRatingChangeListener(this);

        dbManager = DatabaseManager.getInstance();
    }

    @Override
    protected  void onDestroy() {
        super.onDestroy();

        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {return false;}

    @Override
    public void onRatingChange(float firstRating) {
        Log.i("RATING",String.valueOf(firstRating));
        //Создаем новый ревью, сетим первый отзыв
        newReview = new Review();
        newReview.setFirstRating(String.valueOf(firstRating));
        dbManager.setReview(newReview);

        //Запуск новой активити
        goNextActivity();

        //Делаем фото
        makePhoto();

    }



    /**
     * Делаем фотку
     **/
    public void makePhoto() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            //mCamera.unlock();
            //mCamera.reconnect();

            mCamera.setPreviewTexture(new SurfaceTexture(10));
        } catch (IOException e) {
            Log.d("PHOTO", "Set preview texture operation failed");
        }

        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> supSizes = params.getSupportedPreviewSizes();
        Camera.Size firstSize = supSizes.get(0);
        params.setPreviewSize(firstSize.width, firstSize.height);
        params.setAutoWhiteBalanceLock(true);
        params.setPictureSize(1024, 768);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        params.setPictureFormat(ImageFormat.JPEG);
        mCamera.setParameters(params);
        mCamera.startPreview();
        mCamera.takePicture(null, null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i("PHOTO taken", String.valueOf(data.length));
        if(data.length > 0) {
            //save image to firebase
            FileStorageManager.getInstance().upload(data, "jpeg");
            camera.release();
        }
    }

    private void goNextActivity() {
        //вызываем третью активити с видео отзывом
        Intent newIntent = new Intent(this, SecondRating.class);
        startActivity(newIntent);
    }


}
