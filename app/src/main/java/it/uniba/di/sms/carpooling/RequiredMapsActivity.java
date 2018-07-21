package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class RequiredMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM = 8.3f;
    private static final float MARKER_ZOOM = 18.3f;
    public GoogleMap mMap;
    public LinearLayout richiesti;
    public LinearLayout richiesti2;
    public RelativeLayout rich;
    public CircleImageView immagine;
    public TextView data,giorno,ora,casa,telefono,nome,cognome,status;
    private DocumentReference user;
    private FirebaseUser userAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_required_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        user = FirebaseFirestore.getInstance().collection("Users").document(userAuth.getUid());

        Intent receive = getIntent();
        final HashMap<String, Object> richiesta = (HashMap<String, Object>) receive.getSerializableExtra("richiestaPassaggio");

        richiesti = (LinearLayout) findViewById(R.id.LinearRichiesti);
        richiesti2 = (LinearLayout) findViewById(R.id.LinearRichiesti2);
        rich = (RelativeLayout) findViewById(R.id.relativeRichiesti);

        data = (TextView) findViewById(R.id.Data);
        giorno = (TextView) findViewById(R.id.Giorno);
        ora = (TextView) findViewById(R.id.Ora);
        casa = (TextView) findViewById(R.id.Casa);
        telefono = (TextView) findViewById(R.id.Telefono);
        nome = (TextView) findViewById(R.id.nomeAut);
        cognome = (TextView) findViewById(R.id.cognomeAut);
        immagine = (CircleImageView) findViewById(R.id.immagineProfilo);
        status = (TextView) findViewById(R.id.Status);

        status.setText((String) richiesta.get("status"));
        String idPassaggio = richiesta.get("idPassaggio").toString();
        DocumentReference passaggio = FirebaseFirestore.getInstance().collection("Rides").document(idPassaggio);
        passaggio.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> passaggio = documentSnapshot.getData();
                data.setText((String) passaggio.get("data"));
                giorno.setText((String) passaggio.get("giorno"));
                ora.setText((String) passaggio.get("ora"));
                casa.setText((String) passaggio.get("tipoViaggio"));
                Map<String, Object> autista = (Map<String, Object>) passaggio.get("autista");
                telefono.setText((String) autista.get("phone"));
                cognome.setText((String) autista.get("surname"));
                nome.setText((String) autista.get("name"));

                if (autista.get("urlProfileImage") != null) {
                    Picasso.with(RequiredMapsActivity.this).load(autista.get("urlProfileImage").toString()).into(immagine);
                }
            }
        });

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
        final Map<String,Object> richiesta = (Map<String,Object>)receive.getSerializableExtra("richiestaPassaggio");

        readData(new FirestoreCallback() {
            @Override
            public void onCallback(final Map<String, Object> user) {
                String idAutista = richiesta.get("idAutista").toString();
                DocumentReference autista = FirebaseFirestore.getInstance().collection("Users").document(idAutista);
                autista.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> autista = documentSnapshot.getData();
                        Map<String,Object> address = (Map<String, Object>) autista.get("userAddress");
                        LatLng indirizzoAutista = new LatLng((Double) address.get("latitude"),(Double)address.get("longitude"));
                        Map<String,Object>addressP =(Map<String,Object>) user.get("userAddress");
                        LatLng indirizzoPassegero = new LatLng((Double) addressP.get("latitude"),(Double)addressP.get("longitude"));
                        Map<String, Object> userCompany = (Map<String, Object>) user.get("userCompany");
                        LatLng lavoro = new LatLng((Double) userCompany.get("latitude"), (Double) userCompany.get("longitude"));
                        mMap.addMarker(new MarkerOptions().position(lavoro).title("lavoro").snippet(userCompany.get("address").toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.markerlavoro)));
                        mMap.addMarker(new MarkerOptions().position(indirizzoPassegero).title(getResources().getString(R.string.Home)).icon(BitmapDescriptorFactory.fromResource(R.drawable.casamarker)));
                        Marker marker = mMap.addMarker(new MarkerOptions().position(indirizzoAutista).title("autista").snippet(address.get("address").toString()));
                        String status = richiesta.get("status").toString();
                        switch (status) {
                            case "IN ATTESA":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.autista_richiedi));
                                break;
                            case "CONFERMATO":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.autista_conferma));
                                break;
                            case "RIFIUTATO":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.autista_negato));
                                break;
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lavoro, DEFAULT_ZOOM));

                    }
                });
            }
        });

        final Handler handler = new Handler();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(MARKER_ZOOM),2000,null);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),MARKER_ZOOM));
                    }
                },100);
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM),1000,null);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));

                    }
                });
                return false;
            }
        });


    }





    //metodo per leggere all'esterno dell'On Succes Listener
    private void readData(final RequiredMapsActivity.FirestoreCallback callback){

        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> user = documentSnapshot.getData();

                callback.onCallback(user);

            }
        });
    }

    //interfaccia per metodo di callback
    private interface FirestoreCallback {
        void onCallback(Map<String,Object> user);
    }
}
