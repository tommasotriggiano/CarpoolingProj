package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private Button register;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        //controls if the user is already logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }


        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button)findViewById(R.id.Reg2);

        register = (Button)findViewById(R.id.Register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (email.isEmpty()) {
                    inputEmail.setError(getResources().getString(R.string.Toast1));
                    inputEmail.requestFocus();
                    return;}

                if (password.isEmpty()) {
                    inputPassword.setError(getResources().getString(R.string.Toast2));
                    inputPassword.requestFocus();
                    return;}

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                Toast.makeText(getApplicationContext(), R.string.TstPassShort, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.Authenticationf, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            checkIfEmailVerified();

                        }
                    }
                });
            }
        });
    }

    //metodo per controllare se l'email è stata verificata
    private void checkIfEmailVerified(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified()){
            Toast.makeText(LoginActivity.this, R.string.Successful, Toast.LENGTH_SHORT).show();
            Intent regform = new Intent(LoginActivity.this,RegistrationFormActivity.class);
            startActivity(regform);
            finish();}

        else{
            Toast.makeText(LoginActivity.this, R.string.NotVerified, Toast.LENGTH_SHORT).show();
        }

    }
}



















