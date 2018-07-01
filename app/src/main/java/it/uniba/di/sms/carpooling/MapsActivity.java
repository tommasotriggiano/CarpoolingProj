package it.uniba.di.sms.carpooling;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
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

import org.json.JSONException;
import org.json.JSONObject;

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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //ArrayList di posizione per il marker
    ArrayList<LatLng> markerPosList=new ArrayList<LatLng>();
    private static final int LOCATION_REQUEST = 500;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    static LatLng currentPosition;

    private GoogleMap mMap;
    private String tipoViaggio,data,ora,nome;
    CollectionReference passaggi;
    DocumentReference user;
    FirebaseUser userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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

        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        passaggi = FirebaseFirestore.getInstance().collection("Rides");

        //riferimento al documento dell'user loggato
        user = FirebaseFirestore.getInstance().collection("Users").document(userAuth.getUid());




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getDeviceLocation();
        //Per test riempio io
        markerPosList.add(new LatLng(41.3196635,16.2838207));
        markerPosList.add(new LatLng(41.3180000,16.2838207));
        markerPosList.add(new LatLng(41.3100000,16.2838207));
        markerPosList.add(new LatLng(41.3196635,16.2898207));
        markerPosList.add(new LatLng(41.3196635,16.2828207));
        markerPosList.add(new LatLng(41.3196635,16.2818207));


        for (LatLng position:markerPosList){
            //Create marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(position);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerOptions.snippet("Stringa da passare");
            markerOptions.title("Titolo");
            //markerOptions.icon(mettete qui l'icona della faccia));
            mMap.addMarker(markerOptions);
            /*
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    Context mContext = getBaseContext();

                    LinearLayout info = new LinearLayout(mContext);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(mContext);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());
                    title.setSingleLine(false);

                    TextView snippet = new TextView(mContext);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());
                    snippet.setSingleLine(false);

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
            */

        }


        readData(new FirestoreCallback() {
            @Override
            public void onCallback(Map<String, Object> userCompany) {
                Query findRides;
                String userNameCompany = (String) userCompany.get("name");
                final LatLng lavoro = new LatLng((Double)userCompany.get("latitude"),(Double)userCompany.get("longitude"));

                /*final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                Date date= null;
                try{date = dateFormat.parse(ora);
                }
                catch(ParseException p){

                }
                final Date oraMin= date;
                oraMin.setMinutes(date.getMinutes()-10);
                final Date oraMax= date;
                oraMax.setMinutes(date.getMinutes()+10);*/

                if((nome.isEmpty())){
                    findRides = passaggi
                            .whereEqualTo("dataPassaggio",data)
                            .whereEqualTo("ora",ora)
                            .whereEqualTo("tipoViaggio",tipoViaggio)
                            .whereEqualTo("autista.userCompany.name",userNameCompany);
                }
                else{
                    findRides = passaggi
                            .whereEqualTo("dataPassaggio",data)
                            .whereEqualTo("tipoViaggio",tipoViaggio)
                            .whereEqualTo("ora",ora)
                            .whereEqualTo("autista.surname",nome)
                            .whereEqualTo("autista.userCompany.name",userNameCompany);}


                findRides.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //marker del luogo di lavoro
                        mMap.addMarker(new MarkerOptions().position(lavoro).title("Marker in ..."));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lavoro, DEFAULT_ZOOM));
                        mMap.animateCamera(CameraUpdateFactory.zoomIn());

                        for(DocumentSnapshot document : task.getResult()){
                            Map<String,Object> passaggio = document.getData();
                            Map<String,Object> autista = (Map<String,Object>) passaggio.get("autista");
                            Map<String,Object> address = (Map<String,Object>) autista.get("userAddress");
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
                            if(!(userAuth.getUid().equals(autista.get("id")))){
                                LatLng indirizzo = new LatLng(latitude,longitude);

                                //marker delle case degli utenti selezionati dalla query
                                mMap.addMarker(new MarkerOptions().position(indirizzo).title("Marker in ..."));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(indirizzo));}




                        }
                    }
                });

            }
        });

/************/

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                StartTravel();
            }
        });
        }

        //metodo per leggere all'esterno dell'On Succes Listener
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


    }




