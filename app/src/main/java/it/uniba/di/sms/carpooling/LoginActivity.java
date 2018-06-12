package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private TextInputLayout layout_email,layout_password;
    private Button btnLogin;
    private Button register;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        layout_password= (TextInputLayout) findViewById(R.id.layout_password) ;
        layout_email=(TextInputLayout) findViewById(R.id.layout_email) ;
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
                    layout_email.setError(getResources().getString(R.string.Toast1));
                    layout_email.requestFocus();
                    return;}

                if (password.isEmpty()) {
                    layout_password.setError(getResources().getString(R.string.Toast2));
                    layout_password.requestFocus();

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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified()){
            Toast.makeText(LoginActivity.this, R.string.Successful, Toast.LENGTH_SHORT).show();
            //leggo l'instanza del database creato nella regsitration form
            ref.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        //se l'istanza del database esiste, quindi l'utente ha già inserito le sue informazioni, si passa all'activity principale
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                    else{
                        startActivity(new Intent(LoginActivity.this,RegistrationFormActivity.class));
                        finish();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        //se l'email non è stata verificata
        else{
            Toast.makeText(LoginActivity.this, R.string.NotVerified, Toast.LENGTH_SHORT).show();
        }
}}




















