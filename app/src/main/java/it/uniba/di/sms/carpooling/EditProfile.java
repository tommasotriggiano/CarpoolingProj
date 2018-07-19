package it.uniba.di.sms.carpooling;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.app.Activity.RESULT_OK;


public class EditProfile extends Fragment {
    private final int REQUEST_CAMERA=2, SELECT_FILE=0;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private LinearLayout rootLayout;
    private DocumentReference user;
    private CollectionReference rides;
    DocumentReference ride;

    private FirebaseUser userAuth;
    //creazione dello storage per la foto utente
    private StorageReference mStorage;
    private TextView nome,cognome,prefix,telefono,auto;
    private ImageView iNome,iCognome,iTelefono,iAuto;
    private EditText eNome,eCognome,eTelefono,eAuto;
    private CardView cNome,cCognome,cTelefono,cAuto;
    private CircleImageView profile;
    private Button save;
    private ImageButton addPhoto;
    Uri resultUri;
    Snackbar snackbar;
    WriteBatch batch;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_edit_profile, container,false);
        rootLayout = (LinearLayout)  view.findViewById(R.id.linear);

        nome = (TextView) view.findViewById(R.id.text_name);
        cognome=(TextView) view.findViewById(R.id.text_surname);
        prefix=(TextView) view.findViewById(R.id.prefix);
        telefono=(TextView) view.findViewById(R.id.text_phone);
        auto=(TextView) view.findViewById(R.id.text_car);

        eNome= (EditText) view.findViewById(R.id.edit_name);
        eCognome=(EditText) view.findViewById(R.id.edit_surname);
        eTelefono=(EditText) view.findViewById(R.id.edit_phone);
        eAuto=(EditText) view.findViewById(R.id.edit_car);

        iNome = (ImageView) view.findViewById(R.id.img_edit_name);
        iCognome=(ImageView) view.findViewById(R.id.img_edit_surname);
        iTelefono=(ImageView) view.findViewById(R.id.img_edit_phone);
        iAuto=(ImageView) view.findViewById(R.id.img_edit_car);

        cNome = (CardView) view.findViewById(R.id.card_name);
        cCognome=(CardView) view.findViewById(R.id.card_surname);
        cTelefono=(CardView) view.findViewById(R.id.card_phone);
        cAuto=(CardView) view.findViewById(R.id.card_car);

        profile = (CircleImageView) view.findViewById(R.id.profile);
        save = (Button)view.findViewById(R.id.modifica);
        addPhoto = (ImageButton)view.findViewById(R.id.addPhoto);


        //reference allo storage
        mStorage = FirebaseStorage.getInstance().getReference();
        batch = FirebaseFirestore.getInstance().batch();


        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        user = FirebaseFirestore.getInstance().collection("Users").document(userAuth.getUid());
        rides = FirebaseFirestore.getInstance().collection("Rides");
        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Map<String,Object> user = documentSnapshot.getData();
                    nome.setText((String)user.get("name"));
                    cognome.setText((String)user.get("surname"));
                    String parts[] = user.get("phone").toString().split(" ");
                    prefix.setText(parts[0]);
                    telefono.setText(parts[1]);
                    if(user.get("car") != null){
                        auto.setText((String)user.get("car"));
                    }else {
                        auto.setText(getResources().getString(R.string.nothing_car));
                    }
                    if(user.get("urlProfileImage")!= null){
                        Picasso.with(getActivity()).load(user.get("urlProfileImage").toString()).into(profile);
                    }
                }
            }
        });


        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        getActivity().setTitle(R.string.profile);
        iNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cNome.setVisibility(View.GONE);
                eNome.setVisibility(View.VISIBLE);
                eNome.setText(nome.getText());
                eNome.requestFocus();

            }
        });
        iCognome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cCognome.setVisibility(View.GONE);
                eCognome.setVisibility(View.VISIBLE);
                eCognome.setText(cognome.getText());
                eCognome.requestFocus();
            }
        });
        iTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cTelefono.setVisibility(View.GONE);
                eTelefono.setVisibility(View.VISIBLE);
                eTelefono.setText(telefono.getText());
                eTelefono.requestFocus();
            }
        });
        iAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cAuto.setVisibility(View.GONE);
                eAuto.setVisibility(View.VISIBLE);
                eAuto.setText(auto.getText());
                eAuto.requestFocus();
            }
        });



        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifiedPermission();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();


            }
        });


    }

    private void saveChanges() {
        final Map modifiche = new HashMap();

        cNome.setVisibility(View.VISIBLE);
        cCognome.setVisibility(View.VISIBLE);
        cTelefono.setVisibility(View.VISIBLE);
        cAuto.setVisibility(View.VISIBLE);
        eNome.setVisibility(View.GONE);
        eCognome.setVisibility(View.GONE);
        eTelefono.setVisibility(View.GONE);
        eAuto.setVisibility(View.GONE);

        if (resultUri != null) {
            StorageReference filePath = mStorage.child("Foto profilo").child(userAuth.getUid());
            filePath.putFile(resultUri);
            modifiche.put("urlProfileImage", resultUri.toString());
        }

        final String name = eNome.getText().toString().trim();
        if (!(name.isEmpty())) {
            nome.setText(name);
            modifiche.put("name", name);
        }

        final String surname = eCognome.getText().toString().trim();
        if (!(surname.isEmpty())) {
            cognome.setText(surname);
            modifiche.put("surname", surname);
        }

        final String phone = eTelefono.getText().toString().trim();
        final String phone_complete = prefix.getText().toString() + " "+phone;
        if (!(phone.isEmpty())) {
            telefono.setText(phone);
            modifiche.put("phone", phone_complete);
        }

        final String car = eAuto.getText().toString().trim();
        if (!(car.isEmpty())) {
            auto.setText(car);
            modifiche.put("car", car);
        }

        Query findRides = rides.whereEqualTo("autista.id", userAuth.getUid());

        findRides.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentSnapshot d : documentSnapshots.getDocuments()) {

                    String id = d.getId();
                    ride = rides.document(id);

                    if (resultUri != null) {
                        StorageReference filePath = mStorage.child("Foto profilo").child(userAuth.getUid());
                        filePath.putFile(resultUri);
                        ride.update("autista.urlProfileImage", resultUri.toString());
                    }
                    if (!(name.isEmpty())) {
                        ride.update("autista.name", name);
                    }
                    if (!(surname.isEmpty())) {
                        ride.update("autista.surname", surname);
                    }
                    if (!(phone.isEmpty())) {
                        ride.update("autista.phone", phone_complete);
                    }
                    if (!(car.isEmpty())) {
                        ride.update("autista.car", car);
                    }
                }


            }
        });
        user.update(modifiche).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                snackbar = Snackbar.make(rootLayout,getActivity().getResources().getString(R.string.saved),Snackbar.LENGTH_SHORT);
                snackbar.show();

            }
        });
    }


    public void selectImage(){
        final RegistrationForm.Item[] items = {
                new RegistrationForm.Item(getResources().getString(R.string.Camera), android.R.drawable.ic_menu_camera),
                new RegistrationForm.Item(getResources().getString(R.string.Gallery), android.R.drawable.ic_menu_gallery),
                new RegistrationForm.Item(getResources().getString(R.string.Cancel),android.R.drawable.ic_menu_close_clear_cancel)
        };

        ListAdapter adapter = new ArrayAdapter<RegistrationForm.Item>(getContext(), android.R.layout.select_dialog_item, android.R.id.text1, items){
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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
        if (!addPermission(permissionsList, android.Manifest.permission.READ_EXTERNAL_STORAGE))
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
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

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case REQUEST_CAMERA:
                if (resultCode==RESULT_OK){
                    Bundle bundle = data.getExtras();
                    final Bitmap bmp= (Bitmap) bundle.get("data");
                    resultUri = data.getData();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    profile.setImageBitmap(bmp);
                }
                break;
            case SELECT_FILE:
                if (resultCode==RESULT_OK) {
                    resultUri = data.getData();
                    profile.setImageURI(resultUri);
                }
                break;
            default:
                break;
        }
    }
}
