package it.uniba.di.sms.carpooling;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class RegistrationForm extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private final int REQUEST_CAMERA=2, SELECT_FILE=0;
    private final int CAMERA_PERMISSION_REQUEST_ID=3;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    //dichiarazioni variabili per l'utente
    private TextView indirizzoCasa,text;
    private EditText nome;
    private EditText cognome;
    private Spinner azienda;
    private EditText telefono;
    private EditText automobile;
    private Button confermaAccount;
    private ImageButton addPhoto;

    private CircleImageView image;
    //creazione del database
    private DatabaseReference databaseUsers;
    //creazione dello storage per la foto utente
    private StorageReference mStorage;
    //database per l'autenticazione
    private FirebaseUser profile;

    private DocumentReference rf;

    private DocumentReference companyRef;

    Uri resultUri;

    LatLng position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.activity_registration_form, container,false);

        image= (CircleImageView) view.findViewById(R.id.imageView2) ;
        addPhoto=(ImageButton)view.findViewById(R.id.addPhoto);
        //istanza del databse
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        //istanza del profilo autenticato all'applicazione
        profile = FirebaseAuth.getInstance().getCurrentUser();

        rf = FirebaseFirestore.getInstance().document("Users"+"/"+profile.getUid());

        //istanza del profilo autenticato all'applicazione

        //reference allo storage
        mStorage = FirebaseStorage.getInstance().getReference();

        nome = (EditText) view.findViewById(R.id.Nome);
        cognome = (EditText) view.findViewById(R.id.Cognome);
        // Inserimento posto con autocompletamento
        indirizzoCasa = (TextView) view.findViewById(R.id.Indirizzo);
        text=(TextView) view.findViewById(R.id.ind);
        azienda = (Spinner) view.findViewById(R.id.Azienda);
        telefono = (EditText) view.findViewById(R.id.Telefono);
        automobile = (EditText) view.findViewById(R.id.Auto);
        confermaAccount = (Button) view.findViewById(R.id.confirm);

        return view;
        }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        getActivity().setTitle(R.string.profile);

        indirizzoCasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indirizzoCasa.setText("");
                text.setVisibility(View.VISIBLE);
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

            }
        });

        confermaAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
                addProfileImage();
                sendMail();


            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifiedPermission();
            }
        });

    }
    // metodo per inviare l'email al mobility manager
    public void sendMail() {
        try
        {
            LongOperation l=new LongOperation();
            l.execute();  //sends the email in background
            Toast.makeText(getActivity(), l.get(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }


    //metodo per aggiungere un utente confermato
    public void addUser(){
        final String name = nome.getText().toString().trim();
        final String surname = cognome.getText().toString().trim();
        String address = indirizzoCasa.getText().toString().trim();
        String company = azienda.getSelectedItem().toString().trim();
        final String phone = telefono.getText().toString().trim();

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
        final String email = profile.getEmail();

        final HashMap<String,Object> userAddress = new HashMap<>();
        userAddress.put("address",address);
        userAddress.put("latitude",position.latitude);
        userAddress.put("longitude",position.longitude);

        companyRef = FirebaseFirestore.getInstance().collection("Companies").document(company);

        companyRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HashMap<String,Object> com = (HashMap<String,Object>) documentSnapshot.getData();

                //creo un'instanza dell'oggetto User
                Map<String,Object> user = new HashMap<>();
                user.put("id",profile.getUid());
                user.put("email",email);
                user.put("name",name);
                user.put("surname",surname);
                user.put("userAddress",userAddress);
                user.put("userCompany",com);
                user.put("phone",phone);

                rf.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();

                        }
                        else{
                            //visualizza messaggio di errore
                            Toast.makeText(getContext(), R.string.Failure, Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        });}



        // salvataggio dell'immagine profilo sullo storage
    public void addProfileImage() {
        if (resultUri != null){
            StorageReference filePath = mStorage.child("Foto profilo").child(profile.getUid());
            UploadTask uploadTask = filePath.putFile(resultUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Map newImage = new HashMap();
                    newImage.put("urlProfileImage",downloadUrl.toString());
                    /*se l'utente ha inserito un'immagine di profilo allora nel database degli utenti verrà inserito un campo
                    in cui ci sarà l'url dell'immagine caricata*/
                    databaseUsers.child(profile.getUid()).updateChildren(newImage);
                    rf.update(newImage);


                }
            });
        }
        else{
            //go to home page

        }}

    // classe per definire l'elenco da visualizzare nell'Alert Dialog per inserire l'immagine profilo
    public static class Item{
        public final String text;
        public final int icon;
        public Item(String text, Integer icon) {
            this.text = text;
            this.icon = icon;
        }
        @Override
        public String toString() {
            return text;
        }
    }



    //metodo per selezionare l'immagine
    public void selectImage(){
        final Item[] items = {
                new Item(getResources().getString(R.string.Camera), android.R.drawable.ic_menu_camera),
                new Item(getResources().getString(R.string.Gallery), android.R.drawable.ic_menu_gallery),
                new Item(getResources().getString(R.string.Cancel),android.R.drawable.ic_menu_close_clear_cancel)
        };

        ListAdapter adapter = new ArrayAdapter<Item>(getContext(), android.R.layout.select_dialog_item, android.R.id.text1, items){
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);

                return v;
            }
        };

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.addphoto);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if ((items[i].text).equals(getResources().getString(R.string.Camera))){
                    //startPickImageCamera();
                    Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,REQUEST_CAMERA);
                }else if ((items[i].text).equals(getResources().getString(R.string.Gallery))){
                    Intent intent= new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent,getResources().getString(R.string.selectfile)),SELECT_FILE);

                }else if ((items[i].text).equals(getResources().getString(R.string.Cancel))) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }


    private void verifiedPermission() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList,android.Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");
        if (!addPermission(permissionsList,android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write external storage");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read external storage");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        selectImage();

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(getContext(),permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    selectImage();
                } else {
                    // Permission Denied
                    Toast.makeText(getContext(), "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


   /* private void startPickImageCamera(){
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,REQUEST_CAMERA);
        }else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.CAMERA)){
            new AlertDialog.Builder(getActivity()).setTitle("Permesso Camera").setMessage("E' necessario questo permesso per" +
                    " impostare la foto profilo").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.CAMERA}
                            ,CAMERA_PERMISSION_REQUEST_ID);
                }
            })
                    .create()
                    .show();
        }else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.CAMERA}
                    ,CAMERA_PERMISSION_REQUEST_ID);
        }
    }


    public void onRequestPermissionResult(int requestCode,String[] permission,int[] grantResults){
        if ( requestCode == CAMERA_PERMISSION_REQUEST_ID){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                startPickImageCamera();
            }
        }

    }*/

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(getContext(), data);
                    Log.i(TAG, "Place: " + place.getName());
                    //posizione gps
                    position = place.getLatLng();
                    indirizzoCasa.setTextColor(getResources().getColor(R.color.black));
                    indirizzoCasa.setText(place.getAddress().toString());
                    text.setTextColor(getResources().getColor(R.color.black_overlay));
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getContext(), data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode==RESULT_OK){
                    Bundle bundle = data.getExtras();
                    final Bitmap bmp= (Bitmap) bundle.get("data");
                    resultUri = data.getData();
                    //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    //byte[] data1 = baos.toByteArray();
                    //filePath.putBytes(data1);
                    image.setImageBitmap(bmp);
                }
                break;
            case SELECT_FILE:
                if (resultCode==RESULT_OK) {
                    resultUri = data.getData();
                    //filePath.putFile(imageUri);
                    image.setImageURI(resultUri);
                }
                break;
            default:
                break;
        }


    }













}
