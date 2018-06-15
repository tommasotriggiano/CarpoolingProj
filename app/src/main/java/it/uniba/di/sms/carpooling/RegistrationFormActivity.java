package it.uniba.di.sms.carpooling;

import android.*;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationFormActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getSimpleName();
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    //dichiarazioni variabili per l'utente
    TextView indirizzoCasa,text;
     EditText nome;
     EditText cognome;
     Spinner azienda;
     EditText telefono;
     EditText automobile;
     Button confermaAccount;
     ImageButton addPhoto;
     Integer REQUEST_CAMERA=2, SELECT_FILE=0;
     Integer CAMERA_PERMISSION_REQUEST_ID=3;
     CircleImageView image;
     //creazione del database
    DatabaseReference databaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.Registration);
        toolbar.setLogo(R.mipmap.ic_launcher3_round);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        image= (CircleImageView) findViewById(R.id.imageView2) ;
        addPhoto=(ImageButton)findViewById(R.id.addPhoto);
        //istanza del databse
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        //istanza del profilo autenticato all'applicazione
        final FirebaseUser profile = FirebaseAuth.getInstance().getCurrentUser();

        nome = (EditText) findViewById(R.id.Nome);
        cognome = (EditText) findViewById(R.id.Cognome);
        /** Inserimento posto con autocompletamento **/
        indirizzoCasa = (TextView) findViewById(R.id.Indirizzo);
        text=(TextView) findViewById(R.id.ind);
        //Listener sul editText indirizzo
        indirizzoCasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indirizzoCasa.setText("");
                text.setVisibility(View.VISIBLE);
                int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(RegistrationFormActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

            }
        });

        azienda = (Spinner) findViewById(R.id.Azienda);
        telefono = (EditText) findViewById(R.id.Telefono);
        automobile = (EditText) findViewById(R.id.Auto);
        confermaAccount = (Button) findViewById(R.id.confirm);
        confermaAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addUser();
                sendMail();
                finish();

            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



    }


    public void sendMail() {
        try
        {
            LongOperation l=new LongOperation();
            l.execute();  //sends the email in background
            Toast.makeText(RegistrationFormActivity.this, l.get(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
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




        //creo un'instanza dell'oggetto User
        User user = new User(email,name,surname,address,company,phone);

        //aggiungo l'instanza al database mettendo come chiave primaria l'UID creato al momento dell'autenticazione
        databaseUsers.child(profile.getUid()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //visualizza messaggio di successo e proseguo con l'altra activity
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

        public void selectImage(){
            final CharSequence items []={getResources().getString(R.string.Camera),getResources().getString(R.string.Gallery),getResources().getString(R.string.Cancel)};
            AlertDialog.Builder builder= new AlertDialog.Builder(RegistrationFormActivity.this);
            builder.setTitle(R.string.addphoto);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (items[i].equals(getResources().getString(R.string.Camera))){
                        // inserire verifica permesso
                        startPickImageCamera();
                        //Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //startActivityForResult(intent,REQUEST_CAMERA);
                    }else if (items[i].equals(getResources().getString(R.string.Gallery))){
                        Intent intent= new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent.createChooser(intent,getResources().getString(R.string.selectfile)),SELECT_FILE);

                    }else if (items[i].equals(getResources().getString(R.string.Cancel))) {
                        dialogInterface.dismiss();
                    }
                }
            });
            builder.show();
        }

    private void startPickImageCamera(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,REQUEST_CAMERA);
        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)){
            new AlertDialog.Builder(this).setTitle("Permesso Camera").setMessage("E' necessario questo permesso per" +
                    " impostare la foto profilo").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(RegistrationFormActivity.this,new String[]{android.Manifest.permission.CAMERA}
                            ,CAMERA_PERMISSION_REQUEST_ID);
                }
            })
                    .create()
                    .show();
        }else{
            ActivityCompat.requestPermissions(RegistrationFormActivity.this,new String[]{android.Manifest.permission.CAMERA}
                    ,CAMERA_PERMISSION_REQUEST_ID);
        }
    }



    public void onRequestPermissionResult(int requestCode,String[] permission,int[] grantResults){
        if ( requestCode == CAMERA_PERMISSION_REQUEST_ID){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                startPickImageCamera();
            }
        }

    }
        @Override
        public void onActivityResult(int requestCode,int resultCode,Intent data){
            super.onActivityResult(requestCode,resultCode,data);

            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    Log.i(TAG, "Place: " + place.getName());
                    //posizione gps
                    LatLng position = place.getLatLng();
                    indirizzoCasa.setTextColor(getResources().getColor(R.color.black));
                    indirizzoCasa.setText(place.getAddress().toString());
                    text.setTextColor(getResources().getColor(R.color.black_overlay));
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }

            if (resultCode== Activity.RESULT_OK){
                if (requestCode== REQUEST_CAMERA){
                    Bundle bundle = data.getExtras();
                    final Bitmap bmp= (Bitmap) bundle.get("data");
                    image.setImageBitmap(bmp);

                }else if (requestCode== SELECT_FILE){
                        Uri selectImageUri= data.getData();
                        image.setImageURI(selectImageUri);
                }
            }
        }




}
