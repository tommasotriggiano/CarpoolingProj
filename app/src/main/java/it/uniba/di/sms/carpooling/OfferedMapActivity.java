package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

import java.util.HashMap;
import java.util.Map;

public class OfferedMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM = 8.3f ;
    private GoogleMap mMap;

    private HashMap<String,Object> passeggero;
    private Map markerMap = new HashMap<String,Object>();
    private CollectionReference request;
    private FirebaseUser userAuth;
    private  CoordinatorLayout rootLayout;
    private String idPassaggio;
    private View bottomSheetDialog;
    private Button accept;
    private ImageButton reject;
    private TextView direzione,data,ora,posti,giorno,nomePass,cognomePass,telefono;
    private HashMap<String,Object> passaggio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offered_map);
        rootLayout= (CoordinatorLayout)findViewById((R.id.root)) ;
        bottomSheetDialog =findViewById(R.id.bottom_sheet);



        direzione=(TextView) findViewById(R.id.casa) ;
        data=(TextView)findViewById(R.id.Data) ;
        ora=(TextView)findViewById(R.id.Ora) ;
        giorno=(TextView)findViewById(R.id.Giorno);
        posti=(TextView)findViewById(R.id.postiOcc);

        Intent receive = getIntent();
         passaggio= (HashMap<String, Object>) receive.getSerializableExtra("passaggio");
         idPassaggio = passaggio.get("idPassaggio").toString();

         direzione.setText((String)passaggio.get("tipoViaggio"));
         giorno.setText((String)passaggio.get("giorno"));
         data.setText((String)passaggio.get("dataPassaggio"));
         ora.setText((String)passaggio.get("ora"));
         String postiDisp = passaggio.get("postiDisponibili").toString();
         posti.setText(" 0/"+postiDisp);

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


        Map<String,Object> autista = (Map<String, Object>)passaggio.get("autista");
        Map<String,Object> indirizzoAutista = (Map<String, Object>)autista.get("userAddress");

        final LatLng casa = new LatLng((Double)indirizzoAutista.get("latitude"),(Double)indirizzoAutista.get("longitude"));
                mMap.addMarker(new MarkerOptions().position(casa)
                .title(getResources().getString(R.string.Home))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.casamarker)));



        Map<String,Object> indirizzoLavoro = (Map<String, Object>)autista.get("userCompany");
        LatLng lavoro = new LatLng((Double)indirizzoLavoro.get("latitude"),(Double)indirizzoLavoro.get("longitude"));
                mMap.addMarker(new MarkerOptions().position(lavoro)
                .title(getResources().getString(R.string.Work))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerlavoro)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casa, DEFAULT_ZOOM));
            Query findrequest = request.whereEqualTo("autista.id",userAuth.getUid())
                                        .whereEqualTo("passaggio.idPassaggio",idPassaggio);

            findrequest.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot doc : task.getResult()) {
                        Map<String, Object> richieste = doc.getData();
                        passeggero = (HashMap<String, Object>) richieste.get("passeggero");
                        Map<String, Object> indirizzo = (Map<String, Object>) passeggero.get("userAddress");
                        LatLng indirizzoPasseggero = new LatLng((Double) indirizzo.get("latitude"), (Double) indirizzo.get("longitude"));
                        String status = richieste.get("status").toString();
                        Marker marker = mMap.addMarker(new MarkerOptions().position(indirizzoPasseggero));

                        switch (status) {
                            case "IN ATTESA":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerrichiedi));
                                break;
                            case "CONFERMATO":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerconfirmed));
                                break;
                            case "RIFIUTATO":
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerrefused));
                                break;
                        }


                        markerMap.put(marker.getId(), passeggero);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casa, DEFAULT_ZOOM));
                    }
                }

                    });
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetDialog);

                            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                }
                            });

                            final String key = marker.getId();
                            if (markerMap.get(key) != null) {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                                ImageView imgPass = (ImageView) bottomSheetDialog.findViewById(R.id.immagineProfilo);
                                nomePass = (TextView) bottomSheetDialog.findViewById(R.id.nomePass);
                                cognomePass = (TextView) bottomSheetDialog.findViewById(R.id.cognomePass);
                                telefono = (TextView) bottomSheetDialog.findViewById(R.id.telefono);
                                accept=(Button)bottomSheetDialog.findViewById(R.id.accept);
                                reject=(ImageButton)bottomSheetDialog.findViewById(R.id.reject);


                                Map<String, Object> passeggeroDati = (Map<String, Object>) markerMap.get(key);

                                nomePass.setText(passeggeroDati.get("name").toString());
                                cognomePass.setText(passeggeroDati.get("surname").toString());
                                telefono.setText(passeggeroDati.get("phone").toString());

                                accept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //integrare parte dell'Invio della notifica, aggiornamento database, e numero posti occupati
                                        //cambiare anche l'icona del marker
                                    }
                                });
                                reject.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //integrare parte dell'Invio della notifica, aggiornamento database
                                        //cambiare anche l'icona del marker
                                    }
                                });
                            }
                            else{
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            }

                            return false;
                        }
                    });

                }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId,int vectorBackResId) {
        Drawable background = ContextCompat.getDrawable(context,vectorBackResId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(25, 15, vectorDrawable.getIntrinsicWidth()+25, vectorDrawable.getIntrinsicHeight()+15);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(),background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}



