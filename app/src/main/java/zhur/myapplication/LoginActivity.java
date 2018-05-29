package zhur.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LOGIN";
    private EditText loginInput;
    private EditText passInput;
    private Button loginBtn;
    private static final String Key = "TsD4n1nb8H2TeGT1D0mKhC6ps341Pbfv";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView errorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginInput = (EditText) findViewById(R.id.userInput);
        passInput = (EditText) findViewById(R.id.passInput);
        errorView = (TextView) findViewById(R.id.errorTextView);
        errorView.setVisibility(View.INVISIBLE);

//        loginInput.setOnFocusChangeListener(this);
//        passInput.setOnFocusChangeListener(this);

        loginInit();
    }

    private void loginInit() {
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };



    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {
        if(loginInput.getText() != null && (passInput.getText() != null)) {
            String login = loginInput.getText().toString();
            String password = passInput.getText().toString();
            mAuth.signInWithEmailAndPassword(login, password).addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());

                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    String errCode = e.getErrorCode();
                                    if(errCode.equalsIgnoreCase("ERROR_USER_DISABLED")) {
                                        reportError("Пользователь заблокирован");
                                    } else if(errCode.equalsIgnoreCase("ERROR_USER_NOT_FOUND")) {
                                        reportError("Пользователь не найден");
                                    }
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    reportError("Неверный логин или пароль");
                                } catch (FirebaseAuthException e) {
                                    reportError("Ошибка авторизации: " + e.getLocalizedMessage());
                                } catch (Exception e) {
                                    FirebaseCrash.report(e);
                                }
                            } else {
                                Log.d("LOGIN", mAuth.getCurrentUser().getUid().toString());
                                //DatabaseManager.getInstance().getReview().setSpotId(mAuth.getCurrentUser().getUid().toString());
                                errorView.setVisibility(View.INVISIBLE);
                                startNextActivity();
                            }
                        }
                    }
            ).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.toString());
                    FirebaseCrash.report(e);
                }
            });
        }
    }

    private void reportError(String errCode) {
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(errCode);
    }

    private void startNextActivity() {
        //if login and password is valid start new activity
        Intent newIn = new Intent(this, FirstRating.class);
        startActivity(newIn);
    }


//    @Override
//    public void onFocusChange(View view, boolean hasFocus) {
//        if(hasFocus) {
//            EditText v = (EditText) view;
//            v.setText("");
//        }
//
//
//    }
}
