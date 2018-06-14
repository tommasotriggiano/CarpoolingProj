package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {

    private TextView description;
    private EditText emailReset;
    private Button resetPass;
    private FirebaseAuth user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        getSupportActionBar().setTitle(R.string.ResetPassword);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        description = (TextView)findViewById(R.id.tvDescription);
        emailReset = (EditText)findViewById(R.id.etPassEmail);
        resetPass = (Button)findViewById(R.id.ResetPass);
        user = FirebaseAuth.getInstance();

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = emailReset.getText().toString().trim();
                if (userEmail.isEmpty()) {
                    emailReset.setError(getResources().getString(R.string.Toast1));
                    emailReset.requestFocus();
                    return;}
                else{
                    user.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(PasswordActivity.this, R.string.Emailsent, Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(PasswordActivity.this,LoginActivity.class));
                            }
                            else{
                                Toast.makeText(PasswordActivity.this, R.string.EmailNotRegistered, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            }
        });
    }
}
