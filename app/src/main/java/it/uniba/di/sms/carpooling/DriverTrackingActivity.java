package it.uniba.di.sms.carpooling;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.PersistableBundle;
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
import android.widget.TextView;
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
import com.google.common.collect.Maps;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DriverTrackingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Button btnStart;
    ArrayList<LatLng> listPoints;
    LatLng yourPosition;
    public int nPerson=1;
    double[] latitude_array;
    double[] longitude_array;
    String[] array_IMEI;
    String idPassaggio;
    boolean isNear=false;
    boolean isTrackingNow=false;
    TextView textView;
    String via ;
    String citta ;

    private  final int REQUEST_MAPS_CODE=697;
    private  final int REQUEST_MAPS_CODE_ALONE=698;

    BluetoothAdapter mBluetoothAdapter;
    IntentFilter intentFilter;
    private final BroadcastReceiver deviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("TAG","ACTION: "+action);

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                if(!isTrackingNow)
                    BluetoothAdapter.getDefaultAdapter().startDiscovery();
            }

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("TAG","Device name: "+device.getName()+" IMEI: "+device.getAddress());

                for (String IMEI: array_IMEI) {
                    //Toast.makeText(DriverTrackingActivity.this,IMEI+" = "+device.getAddress(),Toast.LENGTH_SHORT).show();
                    if(device.getAddress().compareTo(IMEI)==0)
                    {
                        isNear=true;
                        textView.setText(getString(R.string.tracking_valid));
                        textView.setVisibility(View.VISIBLE);
                        isTrackingNow=true;
                        StartTravel();
                    }
                }
            }
            if( !isNear  ||  nPerson!=1 ) {
               // Toast.makeText(DriverTrackingActivity.this,getString(R.string.imei_driver_not_found),Toast.LENGTH_LONG).show();
                Log.i("TAG","nessun IMEI rilevato");
                textView.setText(getString(R.string.explanation_tracking));
                textView.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("CHECK", isNear);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean isNear = savedInstanceState.getBoolean("CHECK");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        textView = (TextView) findViewById(R.id.tracking_validity);
        idPassaggio = getIntent().getStringExtra("IdPassaggio");
        array_IMEI = getIntent().getExtras().getStringArray("STRING_ARRAY_IMEI");

        listPoints = new ArrayList<>();
        listPoints.clear();
        yourPosition=new LatLng(41,16);
        getDeviceLocation();


        latitude_array = getIntent().getDoubleArrayExtra("STRING_ARRAY_DOUBLE_LAT");
        longitude_array = getIntent().getDoubleArrayExtra("STRING_ARRAY_DOUBLE_LON");
        nPerson = getIntent().getIntExtra("STRING_NPERSON",5);


        launchCheckDriverNear();
        if(nPerson==1)
        {
            via = getIntent().getStringExtra("STRING_STREAT_ADDRESS");
            textView.setVisibility(View.GONE);
            //citta = getIntent().getStringExtra("STRING_STREAT_CITY");
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("google.navigation:q="+via));
            startActivityForResult(intent, REQUEST_MAPS_CODE_ALONE);
        }

        /*btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nPerson==1)
                {
                    via = getIntent().getStringExtra("STRING_STREAT_ADDRESS");
                    textView.setVisibility(View.GONE);
                    //citta = getIntent().getStringExtra("STRING_STREAT_CITY");
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("google.navigation:q="+via));
                    startActivityForResult(intent, REQUEST_MAPS_CODE_ALONE);
                }
                else
                {
                    StartTravel();
                }
            }
        });*/

        latitude_array = getIntent().getDoubleArrayExtra("STRING_ARRAY_DOUBLE_LAT");
        longitude_array = getIntent().getDoubleArrayExtra("STRING_ARRAY_DOUBLE_LON");
        nPerson = getIntent().getIntExtra("STRING_NPERSON",1);

        try {
            for (int k = 0; k<latitude_array.length; k++){
                listPoints.add(new LatLng(latitude_array[k],longitude_array[k]));
            }
        }catch (NullPointerException error){
            Log.i("TAG","array lat_lon vuoti");
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);

        for (LatLng point:listPoints) {
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).position(point));
        }



        if (nPerson>1){
            String url = getRequestUrl();
            TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
            taskRequestDirections.execute(url);

        }

    }

    private String getRequestUrl(  ) {
        via = getIntent().getStringExtra("STRING_STREAT_ADDRESS");
        citta = getIntent().getStringExtra("STRING_STREAT_CITY");
        //Value of origin
        String str_org = "origin=" + yourPosition.latitude +","+yourPosition.longitude;
        //Value of destination
        String str_dest = "destination=" + via + "," + citta;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Set waypont
        String str_way = "";
        if (listPoints.size()>1){
            str_way = "waypoints=";
            for (int i=0; i<listPoints.size()-1; i++){
                str_way = str_way + listPoints.get(i).latitude + "," + listPoints.get(i).longitude + "|";
                // str_way.concat(listPoints.get(i).latitude + "," + listPoints.get(i).longitude + "|");
            }
            str_way = str_way + listPoints.get(listPoints.size()-1).latitude + "," + listPoints.get(listPoints.size()-1).longitude;
        }
        //Build the full param
        String param = str_org +"&" + str_dest + "&" + str_way + "&" + sensor+"&" +mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        Log.i("TAG",url);
        return url;
    }

    private void StartTravel(){
        String str_org = "origin=" + yourPosition.latitude +","+yourPosition.longitude;
        String str_dest = "destination=" + via;
        String str_way = "";
        if (nPerson>=2){
            str_way = "waypoints=";
            for (int i=0; i<listPoints.size()-1; i++){
                str_way = str_way + listPoints.get(i).latitude + "," + listPoints.get(i).longitude + "|";
                Log.i("TAG","Inside waypoint "+i+" "+listPoints.get(i).latitude + "," + listPoints.get(i).longitude);
            }
            str_way = str_way + listPoints.get(listPoints.size()-1).latitude + "," + listPoints.get(listPoints.size()-1).longitude;
        }
        //Create url to request
        String url = "https://www.google.com/maps/dir/?api=1&json?" + str_org + "&" + str_dest + "&" + str_way + "&mode=driving";
        Log.i("TAG","\n"+url+"\n");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setPackage("com.google.android.apps.maps");
        startActivityForResult(intent, REQUEST_MAPS_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.i("TAG","Result "+requestCode+"  "+resultCode);
        if (REQUEST_MAPS_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                Log.i("TAG", "Result finish track with passengers");
                assegnaPunteggio(idPassaggio);
                Intent intent = new Intent(DriverTrackingActivity.this, MainActivity.class);
                startActivity(intent);
            }
           /*
            launchCheckDriverNear();
           if(isNear){
                Intent intent = new Intent(DriverTrackingActivity.this,RatingActivity.class);
                intent.putExtra("CHECK",isNear);
                intent.putExtra("ALONE","false");
                startActivity(intent);
            }
           */
            if (resultCode == RESULT_CANCELED) {
                Intent intent = new Intent(DriverTrackingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }


        if (REQUEST_MAPS_CODE_ALONE==requestCode){
            if (resultCode==RESULT_OK){
                Log.i("TAG","Result finish maps track but alone");
                assegnaPunteggio(idPassaggio);
                Intent intent = new Intent(DriverTrackingActivity.this, MainActivity.class);
                startActivity(intent);}
            if (resultCode == RESULT_CANCELED) {
                Intent intent = new Intent(DriverTrackingActivity.this, MainActivity.class);
                startActivity(intent);
            }

           /*
           if(isNear){
                Intent intent = new Intent(DriverTrackingActivity.this,RatingActivity.class);
                intent.putExtra("CHECK",isNear);
                intent.putExtra("ALONE","true");
                startActivity(intent);
            }
           */
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

            if(lists != null) {
                for (List<HashMap<String, String>> path : lists) {
                    points = new ArrayList();
                    if (yourPosition.latitude == 41 && yourPosition.longitude == 16) {
                        Log.i("TAG", "posizione non aggiornata in tempo");
                    } else {
                        points.add(yourPosition);
                    }
                    polylineOptions = new PolylineOptions();

                    for (HashMap<String, String> point : path) {
                        double lat = Double.parseDouble(point.get("lat"));
                        double lon = Double.parseDouble(point.get("lon"));

                        points.add(new LatLng(lat, lon));
                    }
                    polylineOptions.addAll(points);
                    polylineOptions.width(15);
                    polylineOptions.color(Color.BLUE);
                    polylineOptions.geodesic(true);
                }
            }
            else Log.i("TAG","<<<lista nulla>>>");


            if (polylineOptions!=null && nPerson>2) {
                mMap.addPolyline(polylineOptions);
            }

        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: found location!");
                        if (location==null){
                            Log.i("TAG","getDeviceLoc ha dato null");
                        }
                        else {
                            Location currentLocation = (Location) task.getResult();
                            if(currentLocation!=null)
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),DEFAULT_ZOOM);

                            yourPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        }
                    }else{
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(DriverTrackingActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void launchCheckDriverNear(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(deviceReceiver, intentFilter);
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    private void assegnaPunteggio(final String idPassaggio) {
        DocumentReference passaggioRf = FirebaseFirestore.getInstance().collection("Rides").document(idPassaggio);
        passaggioRf.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int count = 0;
                Map<String,Object> map = documentSnapshot.getData();
                if(map.get("passeggeri")!= null){
                    HashMap<String,Object> passeggeri = (HashMap<String, Object>) map.get("passeggeri");
                    count = passeggeri.size();
                    final int puntiPasseggero = 5*count;
                    final int puntiAutista = puntiPasseggero +10;
                    //creo il database per il tracking dove inserisco anche lo storico dei punteggi
                    CollectionReference tracking = FirebaseFirestore.getInstance().collection("Tracking");
                    DocumentReference pass = tracking.document(idPassaggio);
                    HashMap<String,Object> autista = (HashMap<String, Object>) map.get("autista");
                    HashMap<String,Object> autistaTracking = new HashMap<>();
                    autistaTracking.put("id",autista.get("id"));
                    autistaTracking.put("punti",puntiAutista);
                    pass.update("autista",autistaTracking);
                    final DocumentReference userAutista = FirebaseFirestore.getInstance().collection("Users").document(autista.get("id").toString());
                    userAutista.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String,Object> userMap = documentSnapshot.getData();
                            if(userMap.get("punti") != null){
                               int punti = Integer.valueOf(userMap.get("punti").toString());
                                punti += puntiAutista;
                                userAutista.update("punti",punti);
                            }
                            else{
                                userAutista.update("punti",puntiAutista);
                            }

                        }
                    });

                    //aggiungo i punti dei passeggeri
                    HashMap<String,Object> passeggeriTracking = new HashMap<>();
                    for(String id : passeggeri.keySet()){
                        passeggeriTracking.put(id,puntiPasseggero);
                        final DocumentReference user = FirebaseFirestore.getInstance().collection("Users").document(id);
                        //aggiungo il totale dei punti passeggero al database degli user
                        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Map<String,Object> userMap = documentSnapshot.getData();
                                if(userMap.get("punti") != null){
                                    int punti = (int) userMap.get("punti");
                                    punti += puntiPasseggero;
                                    user.update("punti",punti);
                                }
                                else{
                                    user.update("punti",puntiPasseggero);
                                }

                            }
                        });
                    }
                    pass.update("passeggeri",passeggeriTracking);




                }

            }
        });

    }

}



