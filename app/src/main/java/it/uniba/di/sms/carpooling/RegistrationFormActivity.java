package it.uniba.di.sms.carpooling;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class RegistrationFormActivity extends AppCompatActivity {

    //dichiarazioni variabili per l'utente
     EditText nome;
     EditText cognome;
     EditText indirizzoCasa;
     AutoCompleteTextView azienda;
     EditText telefono;
     EditText automobile;
     Button confermaAccount;

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

        confermaAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();

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

        if(TextUtils.isEmpty(name)){

        }
        if(TextUtils.isEmpty(surname)){

        }
        if(TextUtils.isEmpty(name)){

        }
        if(TextUtils.isEmpty(name)){

        }






    }

}
