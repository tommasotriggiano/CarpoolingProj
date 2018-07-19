package it.uniba.di.sms.carpooling;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private TextInputLayout layout_email,layout_password;
    private Button btnLogin;
    private Button register;
    private TextView resetPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private FirebaseAuth authInstance = FirebaseAuth.getInstance();

    private String adminUid;
    private DatabaseReference adminRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        authInstance.signOut();
        boolean isApproved=false;
        if( isApproved ){

            /*CREATION PENDENT INTENT FOR NOTIFY*/
            Intent intentNoti=new Intent(this,MainActivity.class);
            PendingIntent pendingIntentNoti=PendingIntent.getActivity(this, 0, intentNoti, 0);
            /*CREATION NOTIFY*/
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    //.setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("")
                    .setContentText("")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationCompat.Builder n  = new NotificationCompat.Builder(this)
                    .setContentTitle(getResources().getString(R.string.NotifyApproved))
                    .setContentText("Autore: Mobility Manager")
                    .setSmallIcon(android.R.drawable.ic_dialog_email)
                    .setContentIntent(pendingIntentNoti)
                    .setSound(sound)
                    .setAutoCancel(true);
            /*SEND NOTIFY*/
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, n.build());

        }

        mAuth = FirebaseAuth.getInstance();

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        layout_password= (TextInputLayout) findViewById(R.id.layout_password) ;
        layout_email=(TextInputLayout) findViewById(R.id.layout_email) ;
        resetPassword=(TextView)findViewById(R.id.tvForgotPassword);
        btnLogin = (Button)findViewById(R.id.Reg2);
        progressBar=(ProgressBar)findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);
        register = (Button)findViewById(R.id.Register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(i);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,PasswordActivity.class));
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                final String password = inputPassword.getText().toString();

                if (email.isEmpty()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    layout_email.setError(getResources().getString(R.string.Toast1));
                    layout_email.requestFocus();
                    return;}

                if (password.isEmpty()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    layout_password.setError(getResources().getString(R.string.Toast2));
                    layout_password.requestFocus();

                    return;}

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            // there was an error
                            if (password.length() < 6) {
                                Toast.makeText(getApplicationContext(), R.string.TstPassShort, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.Authenticationf, Toast.LENGTH_SHORT).show();
                            }
                        }
                            else{
                                checkIfEmailVerified();}

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
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        } else{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this, R.string.NotVerified, Toast.LENGTH_SHORT).show();
        }
}}




















