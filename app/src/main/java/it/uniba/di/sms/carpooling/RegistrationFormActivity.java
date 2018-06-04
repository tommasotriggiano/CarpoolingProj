package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationFormActivity extends AppCompatActivity {

    //dichiarazioni variabili per l'utente
     EditText nome;
     EditText cognome;
     EditText indirizzoCasa;
     AutoCompleteTextView azienda;
     EditText telefono;
     EditText automobile;
     Button confermaAccount;

     //creazione del database
    DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);

        nome = (EditText)findViewById(R.id.Nome);
        cognome = (EditText)findViewById(R.id.Cognome);
        indirizzoCasa = (EditText)findViewById(R.id.Indirizzo);
        azienda = (AutoCompleteTextView)findViewById(R.id.Azienda);
        telefono = (EditText)findViewById(R.id.Telefono);
        automobile = (EditText)findViewById(R.id.Auto);
        confermaAccount = (Button)findViewById(R.id.confirm);

        //istanza del databse
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        confermaAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
                startActivity(new Intent(RegistrationFormActivity.this, MainActivity.class));

            }
        });


    }

    //metodo per aggiungere un utente confermato
    public void addUser(){
        String name = nome.getText().toString().trim();
        String surname = cognome.getText().toString().trim();
        String address = indirizzoCasa.getText().toString().trim();
        String company = azienda.getText().toString().trim();
        String phone = telefono.getText().toString().trim();
        FirebaseUser profile = FirebaseAuth.getInstance().getCurrentUser();

        String email = profile.getEmail();

    //potrebbe essere migliorato con una switch
        if(!(TextUtils.isEmpty(name) && TextUtils.isEmpty(surname) && TextUtils.isEmpty(address) && TextUtils.isEmpty(company) && TextUtils.isEmpty(phone))){

            String id = databaseUsers.push().getKey();
            UserConfirmation user = new UserConfirmation(id,email,name,surname,address,company,phone);

            databaseUsers.child(id).setValue(user);
            Toast.makeText(getApplicationContext(), R.string.ConfirmUser, Toast.LENGTH_SHORT).show();}

            else{
            Toast.makeText(getApplicationContext(), R.string.EntName, Toast.LENGTH_SHORT).show();

        }















    }

}
