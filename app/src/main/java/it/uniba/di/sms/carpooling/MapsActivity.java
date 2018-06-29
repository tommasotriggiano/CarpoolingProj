package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String tipoViaggio,data,ora,nome;
    CollectionReference passaggi;
    DocumentReference user;
    FirebaseUser userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent receive = getIntent();
        tipoViaggio = receive.getStringExtra("tipoViaggio");
        data = receive.getStringExtra("data");
        ora = receive.getStringExtra("ora");
        nome = receive.getStringExtra("nome");

        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        passaggi = FirebaseFirestore.getInstance().collection("Rides");
        user = FirebaseFirestore.getInstance().collection("Users").document(userAuth.getUid());





        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        readData(new FirestoreCallback() {
            @Override
            public void onCallback(Map<String, Object> userCompany) {
                Query findRides;
                String userNameCompany = (String) userCompany.get("name");
                final LatLng lavoro = new LatLng((Double)userCompany.get("latitude"),(Double)userCompany.get("longitude"));
                final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                Date date= null;
                try{date = dateFormat.parse(ora);
                }
                catch(ParseException p){

                }
                final Date oraMin= date;
                oraMin.setMinutes(date.getMinutes()-10);
                final Date oraMax= date;
                oraMax.setMinutes(date.getMinutes()+10);

                if((nome.isEmpty())){
                    findRides = passaggi.whereEqualTo("dataPassaggio",data).whereEqualTo("tipoViaggio",tipoViaggio).whereEqualTo("autista.userCompany.name",userNameCompany);
                }
                else{
                    findRides = passaggi.whereEqualTo("dataPassaggio",data).whereEqualTo("tipoViaggio",tipoViaggio).whereEqualTo("autista.surname",nome).whereEqualTo("autista.userCompany.name",userNameCompany);}

                Toast.makeText(MapsActivity.this,userNameCompany,Toast.LENGTH_LONG).show();
                findRides.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        mMap.addMarker(new MarkerOptions().position(lavoro).title("Marker in ..."));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(lavoro));
                        mMap.animateCamera(CameraUpdateFactory.zoomIn());

                        for(DocumentSnapshot document : task.getResult()){
                            Map<String,Object> passaggio = document.getData();
                            Map<String,Object> autista = (Map<String,Object>) passaggio.get("autista");
                            Map<String,Object> address = (Map<String,Object>) autista.get("userAddress");
                            Double latitude = (Double) address.get("latitude");
                            Double longitude = (Double) address.get("longitude");
                            String oraPartenza =(String) passaggio.get("ora");
                            Date oraP = null;
                            try {
                                oraP = dateFormat.parse(oraPartenza);
                                Toast.makeText(MapsActivity.this,oraP.toString(),Toast.LENGTH_LONG).show();;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(!(userAuth.getUid().equals(autista.get("id")))&&(oraP.after(oraMin))&&(oraP.before(oraMax))){

                                LatLng indirizzo = new LatLng(latitude,longitude);

                                mMap.addMarker(new MarkerOptions().position(indirizzo).title("Marker in ..."));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(indirizzo));}




                        }
                    }
                });

            }
        });


        }

        private void readData(final FirestoreCallback callback){

            user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String,Object> user = documentSnapshot.getData();
                    Map<String,Object> userCompanyAddress = (Map<String,Object>)user.get("userCompany");
                    callback.onCallback(userCompanyAddress);

                }
            });
    }

    //interfaccia per metodo di callback
    private interface FirestoreCallback {
        void onCallback(Map<String,Object> userCompany);
        }
    }




