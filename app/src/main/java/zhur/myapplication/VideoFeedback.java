package zhur.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.crash.FirebaseCrash;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class VideoFeedback extends AppCompatActivity implements View.OnClickListener {

    private Intent takeVideoIntent;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private CountDownTimer timer;
    private Uri videoUri;
    private EditText feedbackField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_feedback);

        //add buttom handler
        Button cancelBtn = (Button) findViewById(R.id.btnFeedbackCancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timer != null)
                    timer.cancel();
                startNextActivity();
            }
        });

        //go to main activity if there is not activity in 30 seconds
        startTimer(30000);

    }

    private void startTimer(int time) {
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l) {
                Log.d("TIMER VIDEO", "The timer is ticked");
            }

            @Override
            public void onFinish() {
                callMainActivity();
            }
        }.start();
    }


    private void callMainActivity() {
        Intent main = new Intent(this, FirstRating.class);
        startActivity(main);

        //timer.cancel();
    }

    @Override
    /*
    * Video record starting listener
    * **/
    public void onClick(View view) {
        if(timer != null)
            timer.cancel();

        //Start video recording
        takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            //takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
            takeVideoIntent.putExtra("android.intent.extra.durationLimit", 60);
            //takeVideoIntent.putExtra(Camera.CameraInfo.CAMERA_FACING_FRONT, 1);
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }


    }


    //handler для комментариев
    public void leaveComment(View view) {
        if(timer != null)
            timer.cancel();
        callCommentActivity();
//        LinearLayout layout = (LinearLayout) findViewById(R.id.videoFeedbackLayout);
//
//        ViewGroup.LayoutParams params = findViewById(R.id.btnFeedbackCancel).getLayoutParams();
//
//        layout.removeView(findViewById(R.id.startVideoBtn));
//        layout.removeView(findViewById(R.id.leaveCommentBtn));
//        layout.removeView(findViewById(R.id.btnFeedbackCancel));
//
//        feedbackField = new EditText(this);
//        feedbackField.setFocusable(true);
//        feedbackField.setMinimumWidth(200);
//        layout.addView(feedbackField);
//
//        Button cancelBtn = new Button(this);
//        cancelBtn.setLayoutParams(params);
//        cancelBtn.setText("Далее");
//        layout.addView(cancelBtn);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (timer != null)
                timer.cancel();

            if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
                videoUri = data.getData();
                Log.d("VIDEO", videoUri.toString());
                FirebaseCrash.log("I've got video data: " + videoUri.toString());
                try {
                    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                    InputStream fis = getContentResolver().openInputStream(videoUri);
                    BufferedInputStream bufferIs = new BufferedInputStream(fis);

                    boolean eos = false;
                    int numByte = bufferIs.available();
                    byte [] buffer = new byte[numByte];
                    while(!eos) {
                        int value = bufferIs.read(buffer, 0, buffer.length);
                        if (value == -1)
                            eos = true;

                        byteBuffer.write(buffer);
                    }
                    byteBuffer.flush();
                    byte[] res = byteBuffer.toByteArray();


                    //FirebaseCrash.log("VIDEO length " + fis.available());

//                    byte[] buffer = new byte[10000];
//                    int len = 0;
//                    while ((len = fis.read()) != -1) {
//                        byteBuffer.write(buffer, 0, len);
//                    }



                    FileStorageManager.getInstance().upload(res, "3gp");
                } catch (Exception ex) {
                    FirebaseCrash.report(ex);
                    Log.e("UPLOAD VIDEO ERROR", "Ошибка с сохранением данных");
                }
            }

            startNextActivity();
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
    }

    private void startNextActivity() {
        Intent thanksIntent = new Intent(this, ThanksActivity.class);
        startActivity(thanksIntent);
    }

    private void callCommentActivity() {
        Intent comment = new Intent(this, CommentActivity.class);
        startActivity(comment);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { return false; }

}


