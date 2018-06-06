package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistrationFormActivity extends AppCompatActivity {

    //dichiarazioni variabili per l'utente
     EditText nome;
     EditText cognome;
     EditText indirizzoCasa;
     Spinner azienda;
     EditText telefono;
     EditText automobile;
     Button confermaAccount;

     //creazione del database
    DatabaseReference databaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);

        //istanza del databse
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        //istanza del profilo autenticato all'applicazione
        final FirebaseUser profile = FirebaseAuth.getInstance().getCurrentUser();

        databaseUsers.child(profile.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        //se l'utente ha già compilato il form di registrazione allora viene indirizzato alla home page

                        finish();
                        startActivity(new Intent(RegistrationFormActivity.this, MainActivity.class));
                    } else {

                        nome = (EditText) findViewById(R.id.Nome);
                        cognome = (EditText) findViewById(R.id.Cognome);
                        indirizzoCasa = (EditText) findViewById(R.id.Indirizzo);
                        azienda = (Spinner) findViewById(R.id.Azienda);
                        telefono = (EditText) findViewById(R.id.Telefono);
                        automobile = (EditText) findViewById(R.id.Auto);
                        confermaAccount = (Button) findViewById(R.id.confirm);


                        confermaAccount.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addUser();
                                finish();

                            }
                        });
                    }//chiusura else

            }//chiusura on data change
                    @Override
                    public void onCancelled (DatabaseError databaseError){}
                });
            }



    //metodo per aggiungere un utente confermato
    public void addUser(){
        String name = nome.getText().toString().trim();
        String surname = cognome.getText().toString().trim();
        String address = indirizzoCasa.getText().toString().trim();
        String company = azienda.getSelectedItem().toString().trim();
        String phone = telefono.getText().toString().trim();

        if(name.isEmpty()){
            nome.setError(getResources().getString(R.string.EntName));
            nome.requestFocus();
            return;
        }
        if(surname.isEmpty()){
            cognome.setError(getResources().getString(R.string.EntSurname));
            cognome.requestFocus();
            return;
        }
        if(address.isEmpty()){
            indirizzoCasa.setError(getResources().getString(R.string.EntAddress));
            indirizzoCasa.requestFocus();
            return;
        }
        if(phone.isEmpty()){
            telefono.setError(getResources().getString(R.string.EntPhone));
            telefono.requestFocus();
            return;
        }
        if(phone.length() != 10){
            telefono.setError(getResources().getString(R.string.EntPhone2));
            telefono.requestFocus();
            return;
        }

        //ricavo l'email dall'autenticazione
        FirebaseUser profile = FirebaseAuth.getInstance().getCurrentUser();
        String email = profile.getEmail();




        //creo un'instanza dell'oggetto UserConfirmation
        UserConfirmation user = new UserConfirmation(email,name,surname,address,company,phone);

        //aggiungo l'instanza al database mettendo come chiave primaria l'UID creato al momento dell'autenticazione
        databaseUsers.child(profile.getUid()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //visualizza messaggio di successo e proseguo con l'altra activity
                    Toast.makeText(getApplicationContext(), R.string.ConfirmUser, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationFormActivity.this, MainActivity.class));
                    finish();
                }
                else{
                    //visualizza messaggio di errore
                    Toast.makeText(getApplicationContext(), R.string.Failure, Toast.LENGTH_SHORT).show();


                }

            }
        });
        }
    }










