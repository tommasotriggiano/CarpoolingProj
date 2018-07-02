package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputLayout layout_email,layout_password;
    private EditText inputEmail,inputPassword;
    private Button SignUp;
    private FirebaseAuth mAuth;
    private static final String TAG = "RegistrationActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();

        SignUp = (Button)findViewById(R.id.Reg2);
        inputEmail = (EditText)findViewById(R.id.email);
        inputPassword = (EditText)findViewById(R.id.password);
        layout_password= (TextInputLayout) findViewById(R.id.layout_signup_password) ;
        layout_email=(TextInputLayout) findViewById(R.id.layout_signup_email) ;

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    layout_email.setError(getResources().getString(R.string.Toast1));
                    layout_email.requestFocus();
                    return;}

                if (password.isEmpty()) {
                    layout_password.setError(getResources().getString(R.string.Toast2));
                    layout_password.requestFocus();
                return;}

                if (password.length() < 6) {
                    layout_password.setError(getResources().getString(R.string.TstPassShort));
                    layout_password.requestFocus();
                    return;}


                //create user
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, R.string.Failure, Toast.LENGTH_SHORT).show();

                            Log.e(TAG,""+task.getException());
                        } else {
                            //manda email di verifica e poi ritorna sulla login page
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegistrationActivity.this, R.string.Emailsent, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                }
                            });
                        }
                    }

                });


            }
        });

        }}





    



