package zhur.myapplication;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CommentActivity extends AppCompatActivity {

    private CountDownTimer timer;
    private EditText feedbackField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        startTimer(30000);

        //text change listener
        feedbackField = (EditText) findViewById(R.id.feedbackField);
        if(feedbackField != null) {
            feedbackField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(timer != null)
                        timer.cancel();
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(timer != null)
                        timer.cancel();
                    startTimer(20000);

                    Button readyBtn = (Button) findViewById(R.id.readyBtn);

                    LinearLayout parentL = (LinearLayout) readyBtn.getParent();
                    parentL.removeView(readyBtn);

                    LinearLayout l = (LinearLayout) findViewById(R.id.textLayout);
                    l.addView(readyBtn);

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }


    public void typeDoneHandler(View view) {
        if(timer != null)
            timer.cancel();

        //get text from field
        String comment = feedbackField.getText().toString();

        //save to firebase
        if(comment != null || !comment.isEmpty())
            DatabaseManager.getInstance().getReview().setComment(comment);

        startNextActivity();
    }

    private void startTimer(int time) {
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l) {
                Log.d("TIMER COMMENT", "The timer is ticked");
            }

            @Override
            public void onFinish() {
                if(!feedbackField.getText().toString().isEmpty()) {
                    DatabaseManager.getInstance().getReview().setComment(feedbackField.getText().toString());
                    DatabaseManager.getInstance().save();
                }

                callMainActivity();
            }
        }.start();
    }

    private void startNextActivity() {
        Intent thanksIntent = new Intent(this, ThanksActivity.class);
        startActivity(thanksIntent);
    }

    private void callMainActivity() {
        Intent comment = new Intent(this, FirstRating.class);
        startActivity(comment);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { return false; }
}
