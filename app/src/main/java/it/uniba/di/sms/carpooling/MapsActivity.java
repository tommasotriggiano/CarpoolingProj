package it.uniba.di.sms.carpooling;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import java.util.Locale;
import java.util.Map;


import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.net.Uri;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //ArrayList di posizione per il marker
    ArrayList<LatLng> markerPosList=new ArrayList<LatLng>();
    private Map markerMap = new HashMap<String,Object>();
    private static final int LOCATION_REQUEST = 500;
    private static final float DEFAULT_ZOOM = 8.3f;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    static LatLng currentPosition;
    private final String REQUEST = "TiChiedonoUnPassaggio;";
    String receiverUid;

    String nomeRichiedente, cognomeRichiedente;


    private GoogleMap mMap;
    private String tipoViaggio,data,ora,nome,cognome;
    private TextView direzione, dataOra,automobilista,labelAut,nomeAutista,cognomeAutista,dataP,oraP,posti;
    private String urlImageProfile,idAutista;
    private  CoordinatorLayout rootLayout;
    private View bottomSheetDialog;

    CollectionReference passaggi;
    CollectionReference rideRequest;
    DocumentReference user;
    FirebaseUser userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        rootLayout= (CoordinatorLayout)findViewById((R.id.root)) ;
        bottomSheetDialog =findViewById(R.id.bottom_sheet);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Intent receive = getIntent();
        tipoViaggio = receive.getStringExtra("tipoViaggio");
        data = receive.getStringExtra("data");
        ora = receive.getStringExtra("ora");
        nome = receive.getStringExtra("nome");
        cognome = receive.getStringExtra("cognome");

        direzione=(TextView) findViewById(R.id.tipoViaggio) ;
        dataOra=(TextView)findViewById(R.id.textDataOra) ;
        automobilista=(TextView) findViewById(R.id.textAutomobilista);
        labelAut=(TextView) findViewById(R.id.automobilista);
        direzione.setText(tipoViaggio);

        dataOra.setText(data + " "+ ora);
        if (nome == null && cognome == null){
            labelAut.setVisibility(View.GONE);
            automobilista.setVisibility(View.GONE);
        }else
        {
            automobilista.setText(nome+" "+cognome);
        }

        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        passaggi = FirebaseFirestore.getInstance().collection("Rides");
        rideRequest = FirebaseFirestore.getInstance().collection("RideRequests");

        //riferimento al documento dell'user loggato
        user = FirebaseFirestore.getInstance().collection("Users").document(userAuth.getUid());


        //TODO riempire questo campo con userUID e non il propio
        //receiverUid = FirebaseAuth.getInstance().getCurrentUser().getUid();



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //getDeviceLocation();





        readData(new FirestoreCallback() {
            @Override
            public void onCallback(Map<String, Object> user) {
                Query findRides;
                Map<String, Object> userAddress = (Map<String, Object>) user.get("userAddress");
                final LatLng casa = new LatLng((Double) userAddress.get("latitude"), (Double) userAddress.get("longitude"));
                Map<String, Object> userCompanyAddress = (Map<String, Object>) user.get("userCompany");
                String userNameCompany = (String) userCompanyAddress.get("name");

                Log.i("altervista","richiesta");
                nomeRichiedente=((String)user.get("name"));
                cognomeRichiedente=((String)user.get("surname"));
                Log.i("altervista",nomeRichiedente+cognomeRichiedente);

                final LatLng lavoro = new LatLng((Double) userCompanyAddress.get("latitude"), (Double) userCompanyAddress.get("longitude"));

                if(Locale.getDefault().getLanguage().equals("en")){
                    if(tipoViaggio.equals("Work-Home")){
                        tipoViaggio = "Lavoro-Casa";
                    }
                    else if(tipoViaggio.equals("Home-Work")){
                       tipoViaggio="Casa-Lavoro";
                    }
                    String d[] = data.split("-");
                    String data2 = d[1]+"-"+d[0]+"-"+d[2];
                    data = data2;
                }
                if (nome == null && cognome == null) {
                    findRides = passaggi
                            .whereEqualTo("dataPassaggio", data)
                            .whereEqualTo("ora", ora)
                            .whereEqualTo("tipoViaggio", tipoViaggio)
                            .whereGreaterThan("postiDisponibili",0)
                            .whereEqualTo("autista.userCompany.name", userNameCompany);
                } else {
                    findRides = passaggi
                            .whereEqualTo("dataPassaggio", data)
                            .whereEqualTo("tipoViaggio", tipoViaggio)
                            .whereGreaterThan("postiDisponibili",0)
                            .whereEqualTo("ora", ora)
                            .whereEqualTo("autista.name", nome)
                            .whereEqualTo("autista.surname",cognome)
                            .whereEqualTo("autista.userCompany.name", userNameCompany);
                }


                findRides.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //marker del luogo di lavoro
                        mMap.addMarker(new MarkerOptions().position(lavoro).title(getResources().getString(R.string.Work)));
                        mMap.addMarker(new MarkerOptions().position(casa).title(getResources().getString(R.string.Home)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casa, DEFAULT_ZOOM));
                        //mMap.animateCamera(CameraUpdateFactory.zoomIn());

                        for (DocumentSnapshot document : task.getResult()) {
                            Map<String, Object> passaggio = document.getData();
                            Map<String, Object> autista = (Map<String, Object>) passaggio.get("autista");


                            if (autista.get("urlProfileImage") != null) {
                                urlImageProfile = autista.get("urlProfileImage").toString();
                            }
                            // nomeAutista = (String)autista.get("name");
                            // cognomeAutista=(String) autista.get("surname");
                            Map<String, Object> address = (Map<String, Object>) autista.get("userAddress");
                            Double latitude = (Double) address.get("latitude");
                            Double longitude = (Double) address.get("longitude");

                            //String oraPartenza =(String) passaggio.get("ora");

                            /*Date oraP = null;
                            try {
                                oraP = dateFormat.parse(oraPartenza);
                                Toast.makeText(MapsActivity.this,oraP.toString(),Toast.LENGTH_LONG).show();;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }*/

                            //non mostra sulla mappa il passaggio offerto dall'utente loggato
                            if (!(userAuth.getUid().equals(autista.get("id")))) {
                                LatLng indirizzo = new LatLng(latitude, longitude);
                                //attraverso la libreria picasso carico l'immagine nella ImageView preimpostata
                                // Picasso.with(MapsActivity.this).load(urlImageProfile).into(profile);
                                Marker marker = mMap.addMarker(new MarkerOptions().position(indirizzo)
                                        // .title(" "+nomeAutista + " "+ cognomeAutista)
                                        //.snippet(" "+data + " " +oraPartenza+ "\n " +getResources().getString(R.string.Available).toString() +passaggio.get("postiDisponibili").toString())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                markerMap.put(marker.getId(), passaggio);


                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indirizzo, DEFAULT_ZOOM));
                            }


                        }
                    }
                });

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from( bottomSheetDialog);


                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                   @Override
                   public void onMapClick(LatLng latLng) {
                       bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                   }
               });

                ImageView imgAutista = (ImageView) bottomSheetDialog.findViewById(R.id.immagineProfilo);
                nomeAutista = (TextView) bottomSheetDialog.findViewById(R.id.nomeAut);
                cognomeAutista = (TextView) bottomSheetDialog.findViewById(R.id.cognomeAut);
                dataP = (TextView)  bottomSheetDialog.findViewById(R.id.Data);
                oraP = (TextView)  bottomSheetDialog.findViewById(R.id.Ora);
                posti = (TextView)  bottomSheetDialog.findViewById(R.id.postiDisp);
                Button required = (Button)  bottomSheetDialog.findViewById(R.id.requiredRide);
                //recupero la chiave del marker che ho selezionato
               final String key = marker.getId();
                //prendo il passaggio che è collegato alla chiave del marker selezionato
                if (markerMap.get(key) != null) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    Map<String, Object> pass = (Map<String, Object>) markerMap.get(key);

                    //creo il database delle richieste del passaggio
                    final Map<String, Object> autista1 = (Map<String, Object>) pass.get("autista");
                    if (autista1.get("urlProfileImage") != null) {
                        urlImageProfile = autista1.get("urlProfileImage").toString();
                        Picasso.with(MapsActivity.this).load(urlImageProfile).into(imgAutista);
                    }else {
                        imgAutista.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_profile));
                    }

                    idAutista = autista1.get("id").toString();
                    nomeAutista.setText(autista1.get("name").toString());
                    cognomeAutista.setText(autista1.get("surname").toString());
                    if(Locale.getDefault().getLanguage().equals("en")){
                        String d[] = data.split("-");
                        String data2 = d[1]+"-"+d[0]+"-"+d[2];
                        dataP.setText(data2);
                    }
                    else{
                    dataP.setText(data);}
                    oraP.setText(pass.get("ora").toString());
                    posti.setText(pass.get("postiDisponibili").toString());



                required.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("altervista","id: "+idAutista);
                        InsertIntoAltervista(idAutista,REQUEST+nomeRichiedente+" "+cognomeRichiedente);
                        requiredRide(key);
                    }
                });}
                else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                return false;
            }
        });



        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                StartTravel();
            }
        });




    }
    private void requiredRide(String key){
            //prendo il passaggio che è collegato alla chiave del marker selezionato
            if(markerMap.get(key) != null){
                Map<String,Object> pass = (Map<String,Object>) markerMap.get(key);

                //creo il database delle richieste del passaggio
                Map<String,Object> autista1 = (Map<String,Object>)pass.get("autista");
        final Map<String,Object> requestMap = new HashMap<>();
                /* una richiesta conterrà le informazioni dell'autista
                 * del passaggio e del passeggero
                 */
        //Map<String,Object> autista = new HashMap<>();
        String idAut = autista1.get("id").toString();
        requestMap.put("idAutista",idAut);
        //autista.put("name",autista1.get("name"));
        //autista.put("phone",autista1.get("phone"));
        //autista.put("surname",autista1.get("surname"));
        //autista.put("userAddress",autista1.get("userAddress"));
        //if(autista1.get("urlProfileImage") != null){
            //autista.put("urlProfileImage",autista1.get("urlProfileImage"));
        //}
        //requestMap.put("autista",autista);

        /*Map<String,Object> passaggio = new HashMap<>();
        passaggio.put("data",pass.get("dataPassaggio"));
        passaggio.put("giorno",pass.get("giorno"));
        passaggio.put("ora",pass.get("ora"));
        passaggio.put("tipoViaggio",pass.get("tipoViaggio"));*/
        //String idPassaggio = idAut+"_"+data+"_"+ora;
                String idPassaggio = pass.get("idPassaggio").toString();
        requestMap.put("idPassaggio",idPassaggio);
        //requestMap.put("passaggio",passaggio);

        readData(new FirestoreCallback() {
            @Override
            public void onCallback(Map<String, Object> user) {
                Map<String,Object> passeggero = new HashMap<>();
                requestMap.put("idPasseggero",user.get("id"));
                /*passeggero.put("name",user.get("name"));
                passeggero.put("phone",user.get("phone"));
                passeggero.put("surname",user.get("surname"));
                passeggero.put("userAddress",user.get("userAddress"));
                requestMap.put("passeggero",passeggero);*/
                requestMap.put("status","IN ATTESA");
                rideRequest.add(requestMap);

            }
        });


        String message= "Richiesta inviata";
        Snackbar snackbar= Snackbar.make(rootLayout,message,Snackbar.LENGTH_SHORT);
        snackbar.show();
        startActivity(new Intent(MapsActivity.this,MainActivity.class));
        finish();

    }}


        //metodo per leggere all'esterno dell'On Succes Listener
        private void readData(final FirestoreCallback callback){

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

    //metodo per leggere la posizione corrente del dispositivo
    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Location currentLocation = (Location) task.getResult();
                        LatLng pos= new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                        //Aggiounge la posizione attuale
                        //markerPosList.add(pos);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(pos)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .snippet("Tua posizione");
                        mMap.addMarker(markerOptions);
                        currentPosition=pos;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_ZOOM));
                    }else{
                        Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }



    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void StartTravel(){

        //Value of origin
        String str_org = "origin=" + currentPosition.latitude +","+currentPosition.longitude;
        //Value of destination
        String str_dest = "destination=" + markerPosList.get(markerPosList.size()-1).latitude+","+markerPosList.get(markerPosList.size()-1).longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Set waypont

        String str_way = "";
        int nPerson=markerPosList.size();
        if (markerPosList.size()>2){
            str_way = "waypoints=";
            for (int i=0; i<nPerson-2; i++){
                str_way = str_way + markerPosList.get(i).latitude + "," + markerPosList.get(i).longitude + "|";
                // str_way.concat(listPoints.get(i).latitude + "," + listPoints.get(i).longitude + "|");
            }
            str_way = str_way + markerPosList.get(nPerson-2).latitude + "," + markerPosList.get(nPerson-2).longitude;
        }
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + str_way + "&"  + mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://www.google.com/maps/dir/?api=1&" + output + "?" + param;
        Log.i(TAG,url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setPackage("com.google.android.apps.maps");
        startActivity(intent);
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



