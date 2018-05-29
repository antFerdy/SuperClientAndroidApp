package zhur.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ThanksActivity extends AppCompatActivity implements View.OnClickListener {

    private CountDownTimer timer = null;
    private EditText phoneIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks);

        startTimer(20000);

        phoneIn = (EditText) findViewById(R.id.phoneInput);
        phoneIn.addTextChangedListener(new TextWatcher() {
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
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * handler в случае если оставил номер телефона
     **/
    @Override
    public void onClick(View view) {
        TextView askReview = (TextView) findViewById(R.id.askFeedbackView);
        LinearLayout layout = (LinearLayout) findViewById(R.id.numberChooseContainer);
        TextView thanksText = (TextView) findViewById(R.id.thanksText);

        //Устанавливаем текст
        askReview.setText(R.string.finalThanks);

        //прячем инпуты и верхний блок
        layout.setVisibility(View.INVISIBLE);
        thanksText.setVisibility(View.INVISIBLE);


        //прячем клаву
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(phoneIn.getWindowToken(), 0);

        //сохраняем номер телефона
        String telNumber = phoneIn.getText().toString();
        saveNumber(telNumber);


        //Устанавливаем таймер
        if(timer != null)
            timer.cancel();
        startTimer(20000);
    }




    public void startTimer(int time) {
        //go to main activity if there is not activity in 30 seconds
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l) {
                Log.d("TIMER", "The timer is ticked");
            }

            @Override
            public void onFinish() {
                String telNumber = phoneIn.getText().toString();
                if(telNumber != null && !telNumber.isEmpty()) {
                    saveNumber(telNumber);
                }
                callMainActivity();

            }
        }.start();
    }


    private void callMainActivity() {
        Intent main = new Intent(this, FirstRating.class);
        startActivity(main);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { return false; }

    public void saveNumber(String telNumber) {
        DatabaseManager db = DatabaseManager.getInstance();
        db.getReview().setTelNumber(telNumber);
        db.save();
    }

}
