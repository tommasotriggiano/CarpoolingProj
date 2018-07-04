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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class OfferedMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM = 6.3f ;
    private GoogleMap mMap;
    private CollectionReference request;
    private FirebaseUser userAuth;
    private String idPassaggio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offered_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        request = FirebaseFirestore.getInstance().collection("RideRequests");
        userAuth = FirebaseAuth.getInstance().getCurrentUser();
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
        Intent receive = getIntent();
        Map<String,Object> passaggio = (Map<String, Object>) receive.getSerializableExtra("passaggio");
        String idPassaggio = passaggio.get("idPassaggio").toString();

        Map<String,Object> autista = (Map<String, Object>)passaggio.get("autista");
        Map<String,Object> indirizzoAutista = (Map<String, Object>)autista.get("userAddress");
        final LatLng casa = new LatLng((Double)indirizzoAutista.get("latitude"),(Double)indirizzoAutista.get("longitude"));
        mMap.addMarker(new MarkerOptions().position(casa).title("casa").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        Map<String,Object> indirizzoLavoro = (Map<String, Object>)autista.get("userCompany");
        LatLng lavoro = new LatLng((Double)indirizzoLavoro.get("latitude"),(Double)indirizzoLavoro.get("longitude"));
        mMap.addMarker(new MarkerOptions().position(lavoro).title("lavoro").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            Query findrequest = request.whereEqualTo("autista.id",userAuth.getUid())
                                        .whereEqualTo("passaggio.idPassaggio",idPassaggio);

            findrequest.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot doc : task.getResult()){
                        Map<String,Object> richieste = doc.getData();
                        Map<String,Object> passeggero = (Map<String,Object>)richieste.get("passeggero");
                        String nome = (String)passeggero.get("name");
                        Map<String,Object> indirizzo = (Map<String,Object>)passeggero.get("userAddress");
                        LatLng indirizzoPasseggero = new LatLng((Double)indirizzo.get("latitude"),(Double)indirizzo.get("longitude"));
                        String status =  richieste.get("status").toString();
                        if(status.equals("IN ATTESA")){
                        mMap.addMarker(new MarkerOptions().position(indirizzoPasseggero).title(nome).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));}
                            else if(status.equals("CONFERMATO")){
                                mMap.addMarker(new MarkerOptions().position(indirizzoPasseggero).title(nome).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));}
                                    else{
                                        mMap.addMarker(new MarkerOptions().position(indirizzoPasseggero).title(nome).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));}

                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casa, DEFAULT_ZOOM));
                }

                    });

                }
            }



