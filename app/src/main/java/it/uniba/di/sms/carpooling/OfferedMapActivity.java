package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class OfferedMapActivity extends FragmentActivity implements OnMapReadyCallback {


    private final String PASSAGGIOACCETTATO = "PassaggioAccettato";
    private final String PASSAGGIORIFIUTATO = "PassaggioRifiutato";
    private static final float DEFAULT_ZOOM = 8.3f ;
    private static final float MARKER_ZOOM = 18.3f ;
    private static final String TAG = OfferedMapActivity.class.getName();
    private GoogleMap mMap;

    //private HashMap<String,Object> passeggero;
    private Map markerMap = new HashMap<String,Object>();
    private Map markerPass = new HashMap<String,String>();
    private CollectionReference request;
    private DocumentReference passeggero;
    private FirebaseUser userAuth;
    private  CoordinatorLayout rootLayout;
    private String idPassaggio;
    private View bottomSheetDialog;
    private Button accept;
    private ImageButton reject;
    private TextView direzione,data,ora,posti,giorno,nomePass,cognomePass,telefono,address1;
    private HashMap<String,Object> passaggio;
    BottomSheetBehavior bottomSheetBehavior;
    CollectionReference passeggeri;
    DocumentReference itemPasseggero;
    DocumentReference passaggioRf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offered_map);
        rootLayout= (CoordinatorLayout)findViewById((R.id.root)) ;
        bottomSheetDialog =findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetDialog);
        //BottomSheetDialog
        ImageView imgPass = (ImageView) bottomSheetDialog.findViewById(R.id.immagineProfilo);
        nomePass = (TextView) bottomSheetDialog.findViewById(R.id.nomePass);
        cognomePass = (TextView) bottomSheetDialog.findViewById(R.id.cognomePass);
        telefono = (TextView) bottomSheetDialog.findViewById(R.id.telefono);
        address1 =(TextView) bottomSheetDialog.findViewById(R.id.indirizzo);
        accept=(Button)bottomSheetDialog.findViewById(R.id.accept);
        reject=(ImageButton)bottomSheetDialog.findViewById(R.id.reject);

        //CardView
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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        passaggioRf = FirebaseFirestore.getInstance().collection("Rides").document(idPassaggio);
        passaggioRf.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(e != null){
                    Log.e(TAG,e.toString());
                    return;
                }
                if(documentSnapshot.exists()){
                    Long occupati = (Long)documentSnapshot.get("postiOccupati");
                    Long disponibili = (Long)documentSnapshot.get("postiDisponibili");
                    Long sum = occupati+disponibili;
                    if(disponibili <= 0){
                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.error_seats),Toast.LENGTH_SHORT).show();
                        accept.setVisibility(View.INVISIBLE);

                        posti.setText(occupati.toString()+"/"+sum);
                    }
                    else{
                    posti.setText(occupati.toString()+"/"+sum);
                    }

                }

            }
        });



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
                .title(getResources().getString(R.string.Home)).snippet(indirizzoAutista.get("address").toString())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.casamarker)));



        Map<String,Object> indirizzoLavoro = (Map<String, Object>)autista.get("userCompany");
        LatLng lavoro = new LatLng((Double)indirizzoLavoro.get("latitude"),(Double)indirizzoLavoro.get("longitude"));
                mMap.addMarker(new MarkerOptions().position(lavoro)
                .title(getResources().getString(R.string.Work)).snippet(indirizzoLavoro.get("address").toString())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerlavoro)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casa, DEFAULT_ZOOM));
            Query findrequest = request.whereEqualTo("idAutista",userAuth.getUid())
                                        .whereEqualTo("idPassaggio",idPassaggio);

            findrequest.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if(e != null){
                        Log.e(TAG,e.toString());
                        return;
                    }
                    else if(!(documentSnapshots.isEmpty())){
                        for(DocumentSnapshot doc : documentSnapshots.getDocuments()){
                            final Map<String, Object> richieste = doc.getData();
                            String idPassegero = richieste.get("idPasseggero").toString();
                            final String status = richieste.get("status").toString();
                            passeggero = FirebaseFirestore.getInstance().collection("Users").document(idPassegero);
                            passeggero.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Map<String,Object> passeggero = documentSnapshot.getData();

                                    //inizializzo i marker
                                    Map<String, Object> indirizzo = (Map<String, Object>) passeggero.get("userAddress");
                                    LatLng indirizzoPasseggero = new LatLng((Double) indirizzo.get("latitude"), (Double) indirizzo.get("longitude"));
                                    Marker marker = mMap.addMarker(new MarkerOptions().position(indirizzoPasseggero));
                                    switch (status) {
                                        case "IN ATTESA":
                                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerrichiedi));
                                            break;
                                        case "CONFERMATO":
                                            accept.setVisibility(View.INVISIBLE);
                                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerconfirmed));
                                            break;
                                        case "RIFIUTATO":
                                            accept.setVisibility(View.INVISIBLE);
                                            reject.setVisibility(View.INVISIBLE);
                                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.markerrefused));
                                            break;
                                    }
                                    Map<String,Object> richiesta = new HashMap<>();
                                    richiesta.put("idPassaggio",richieste.get("idPassaggio").toString());
                                    richiesta.put("passeggero",passeggero);
                                    markerMap.put(marker.getId(), richiesta);
                                }

                            });
                        }
                    }
                }
            });
                    final Handler handler = new Handler();

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(final Marker marker) {

                            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM),1000,null);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
                                }
                            });
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(MARKER_ZOOM),2000,null);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),MARKER_ZOOM));
                                }
                            },100);


                            final String key = marker.getId();
                            if (markerMap.get(key) != null) {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                                final Map<String,Object> richiesta = (Map<String, Object>) markerMap.get(key);
                                final Map<String,Object> passeggero = (Map<String, Object>) richiesta.get("passeggero");



                                nomePass.setText(passeggero.get("name").toString());
                                cognomePass.setText(passeggero.get("surname").toString());
                                telefono.setText(passeggero.get("phone").toString());
                                Map<String,Object> address = (Map<String, Object>) passeggero.get("userAddress");
                                address1.setText(address.get("address").toString());

                                        accept.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                acceptPass(key);
                                                String UidDestinatario = passeggero.get("id").toString() ;
                                                InsertIntoAltervista(UidDestinatario,PASSAGGIOACCETTATO);
                                                //provare il recreate
                                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                                            }
                                        });
                                        reject.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                rejectPass(key);
                                                String UidDestinatario = passeggero.get("id").toString() ;
                                                InsertIntoAltervista(UidDestinatario,PASSAGGIORIFIUTATO);
                                                //provare il recreate
                                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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

    private void rejectPass(String key) {
        Map<String,Object> richiesta = (Map<String, Object>) markerMap.get(key);
        String idPassaggio = richiesta.get("idPassaggio").toString();
        Map<String,Object> passeggero = (Map<String,Object>) richiesta.get("passeggero");
        CollectionReference requests = FirebaseFirestore.getInstance().collection("RideRequests");
        Query aggiorna = requests.whereEqualTo("idAutista",userAuth.getUid())
                .whereEqualTo("idPassaggio",idPassaggio)
                .whereEqualTo("idPasseggero",passeggero.get("id").toString());

        aggiorna.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot d : task.getResult()){
                    HashMap confirm = new HashMap();
                    confirm.put("status","RIFIUTATO");
                    DocumentReference requests = FirebaseFirestore.getInstance().collection("RideRequests").document(d.getId());
                    requests.update(confirm);
                }

            }
        });
    }

    private void acceptPass(String key) {
        /* 1) cambia lo status della richiesta da IN ATTESA a CONFERMATO
           2) crea un subCollection di passeggeri all'interno della collection "Rides"
           3) decrementa i posti disponibili per quel passaggio

         */
        //1)
        Map<String,Object> richiesta = (Map<String, Object>) markerMap.get(key);
        String idPassaggio = richiesta.get("idPassaggio").toString();
        Map<String,Object> passeggero = (Map<String,Object>) richiesta.get("passeggero");
        CollectionReference requests = FirebaseFirestore.getInstance().collection("RideRequests");
        Query aggiorna = requests.whereEqualTo("idAutista",userAuth.getUid())
                                 .whereEqualTo("idPassaggio",idPassaggio)
                                .whereEqualTo("idPasseggero",passeggero.get("id").toString());

        aggiorna.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot d : task.getResult()){
                    HashMap confirm = new HashMap();
                    confirm.put("status","CONFERMATO");
                    DocumentReference requests = FirebaseFirestore.getInstance().collection("RideRequests").document(d.getId());
                    requests.update(confirm);
                }

            }
        });
        //2)
        passeggeri = FirebaseFirestore.getInstance().collection("Rides").document(idPassaggio).collection("Passeggeri");
        String idPasseggero = passeggero.get("id").toString();
        itemPasseggero = passeggeri.document(idPasseggero);
        itemPasseggero.set(passeggero);

        //3)
        final DocumentReference passaggioRf = FirebaseFirestore.getInstance().collection("Rides").document(idPassaggio);
        passaggioRf.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> pass = documentSnapshot.getData();
                Long postiDisponibili = (Long)pass.get("postiDisponibili");
                Long postiOccupati = (Long)pass.get("postiOccupati");

                HashMap<String,Object> aggiorna = new HashMap<>();
                aggiorna.put("postiDisponibili",postiDisponibili-1);
                aggiorna.put("postiOccupati",postiOccupati+1);
                passaggioRf.update(aggiorna);

            }
        });






    }



    public void InsertIntoAltervista(String idCode,String message){
        Log.i("altervista","startAltet");

        String url = "http://carpoolings.altervista.org/InsertNote.php?TokenID="+idCode+"&Note="+message;
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        RequestParams params = new RequestParams();
        Log.i("altervista","param");
        client.get(url, params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                Log.i("altervista","onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i("altervista","toast");
                Toast.makeText(getApplicationContext(), "Insert success", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("altervista","faill");
                Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_LONG).show();
            }
        });
    }
}



