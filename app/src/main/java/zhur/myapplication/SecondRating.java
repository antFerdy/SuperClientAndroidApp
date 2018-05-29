package zhur.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;

public class SecondRating extends AppCompatActivity {
    private DatabaseManager dbManager;
    private CountDownTimer timer;
    private String secondRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_rating);

        dbManager = DatabaseManager.getInstance();


        //go to main activity if there is not activity in 30 seconds
        timer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long l) {
                Log.d("TIMER SECOND", "The timer is ticked");
            }

            @Override
            public void onFinish() {
                callMainActivity();

            }
        }.start();
    }


    /**
     * Handler для изменения рейтинга
     **/
    public void radioBtnClicked(View view) {
        RadioButton btn = (RadioButton) view;
        secondRating = btn.getText().toString();
        Log.i("RADIO BTN", secondRating);

        //Сохранить рейтинг
        dbManager.getReview().setSecondRating(secondRating);
        dbManager.save();


        //отмена таймера
        if(timer != null)
            timer.cancel();

        //запуск следующей активити
        goNextActivity();
    }

    //Вызов новой активити для следующего рейтинга
    public void goNextActivity() {
        Intent secondIntent = new Intent(this, VideoFeedback.class);
        startActivity(secondIntent);
    }

    private void callMainActivity() {
        Intent main = new Intent(this, FirstRating.class);
        startActivity(main);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("BACK", "Back is called");
        return false;
    }

    @Override
    public void onBackPressed() {
        Log.d("BACK", "Back btn is pressed");
    }




    //    private void camera2Setup() {
//        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//
//        try {
//
//            for (String cameraId : manager.getCameraIdList()) {
//                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
//
//                if (characteristics.get(CameraCharacteristics.LENS_FACING) != CameraCharacteristics.LENS_FACING_FRONT) {
//                    continue;
//                }
//
//                this.cameraId = cameraId;
//
//
//
//                imageReader = ImageReader.newInstance(640, 380, ImageFormat.JPEG, 2);
//                imageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler);
//            }
//
//        } catch (CameraAccessException | NullPointerException e) {
//            e.printStackTrace();
//        }
//    }



//    private void createCaptureRequest() {
//        try {
//
//            CaptureRequest.Builder requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
//            requestBuilder.addTarget(imageReader.getSurface());
//
////            // Focus
////            requestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
////
////            // Orientation
////            int rotation = windowManager.getDefaultDisplay().getRotation();
////            requestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
//
//            cameraCaptureSession.capture(requestBuilder.build(), camera2Callback, null);
//
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private final CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
//        @Override
//        public void onOpened(CameraDevice device) {
//            cameraDevice = device;
//            createCameraCaptureSession();
//        }
//
//        @Override
//        public void onDisconnected(CameraDevice cameraDevice) {}
//
//        @Override
//        public void onError(CameraDevice cameraDevice, int error) {}
//    };
//
//    private void createCaptureSession() {
//        List<Surface> outputSurfaces = new LinkedList<>();
//        outputSurfaces.add(imageReader.getSurface());
//
//        try {
//
//            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
//                @Override
//                public void onConfigured(CameraCaptureSession session) {
//                    cameraCaptureSession = session;
//                }
//
//                @Override
//                public void onConfigureFailed(CameraCaptureSession session) {}
//            }, null);
//
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }


}
