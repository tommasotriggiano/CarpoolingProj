package it.uniba.di.sms.carpooling;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    public int nPerson=9;//Persone a bordo, Almeno 2

    double[] latitude_array;
    double[] longitude_array;

    String[] array_IMEI;
    boolean isNear=false;
    TextView textView;


    BluetoothAdapter mBluetoothAdapter;
    IntentFilter intentFilter;
    private final BroadcastReceiver deviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("TAG","ACTION: "+action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("TAG","Device name: "+device.getName()+" IMEI: "+device.getAddress());

                for (String IMEI: array_IMEI) {
                    if(device.getAddress().compareTo(IMEI)==0)
                    {
                        isNear=true;
                    }
                    else {
                        isNear=false;//TODO vedi se va bene un toast o va bloccata l'activity
                        Toast.makeText(DriverTrackingActivity.this,"IMEI conducente non rilevato",Toast.LENGTH_LONG).show();
                        Log.i("TAG","nessun IMEI rilevato");

                        textView.setText("Finchè non avrai passeggeri\n non potrai avviare il tracking\nriprova");
                        textView.setVisibility(View.VISIBLE);

                    }
                    textView.setText("Connected to "+device.getName());
                    textView.setVisibility(View.VISIBLE);
                }


            }
        }
    };



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

        TextView textView = (TextView) findViewById(R.id.tracking_validity);
        array_IMEI = getIntent().getStringArrayExtra("STRING_ARRAY_IMEI");

        listPoints = new ArrayList<>();
        listPoints.clear();
        yourPosition=new LatLng(0,0);
        getDeviceLocation();



        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //launchCheckDriverNear();//TODO togli e metti check per emulatore
                //isNear=true;
                if(nPerson==listPoints.size()+1 && isNear)
                    StartTravel();
            }
        });


        Log.i("TAG","Get Intent ");
        latitude_array = getIntent().getDoubleArrayExtra("STRING_ARRAY_DOUBLE_LAT");
        longitude_array = getIntent().getDoubleArrayExtra("STRING_ARRAY_DOUBLE_LON");
        nPerson = getIntent().getIntExtra("STRING_NPERSON",5);

        try {
            for (int k = 0; k<latitude_array.length; k++){
                Log.i("TAG","OnCreate add "+latitude_array[k]+" "+longitude_array[k]);
                listPoints.add(new LatLng(latitude_array[k],longitude_array[k]));
            }
        }catch (NullPointerException error){
            Log.i("TAG","array lat_lon vuoti");
        }





    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("TAG","onMapReady");
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
        if (listPoints.size() == nPerson) {
            btnStart.setBackgroundColor(5);
        }
        for (LatLng point:listPoints) {
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).position(point));
        }
        for (LatLng p: listPoints             ) {
            Log.i("TAG","listponit onMapReady Lat:"+p.latitude+" Lon:"+p.longitude);
        }
        Log.i("TAG","nPerson:"+nPerson);
        //Create the URL to get request from first marker to second marker
        String url = getRequestUrl();
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(url);




    }

    private String getRequestUrl(  ) {
        //Value of origin
        String str_org = "origin=" + listPoints.get(0).latitude +","+listPoints.get(0).longitude;
        //Value of destination
        String str_dest = "destination=" + listPoints.get(listPoints.size()-1).latitude+","+listPoints.get(listPoints.size()-1).longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Set waypont

        String str_way = "";
        if (listPoints.size()>2){
            str_way = "waypoints=";
            for (int i=1; i<nPerson-2; i++){
                str_way = str_way + listPoints.get(i).latitude + "," + listPoints.get(i).longitude + "|";
                // str_way.concat(listPoints.get(i).latitude + "," + listPoints.get(i).longitude + "|");
            }
            str_way = str_way + listPoints.get(nPerson-2).latitude + "," + listPoints.get(nPerson-2).longitude;
        }
        //Build the full param
        String param = str_org +"&" + str_dest + "&" + str_way + "&" + sensor+"&" +mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private void StartTravel(){

        //Value of origin
        String str_org = "origin=" + listPoints.get(0).latitude +","+listPoints.get(0).longitude;
        //Value of destination
        String str_dest = "destination=" + listPoints.get(listPoints.size()-1).latitude+","+listPoints.get(listPoints.size()-1).longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Set waypont

        String str_way = "";
        if (listPoints.size()>2){
            str_way = "waypoints=";
            for (int i=1; i<nPerson-2; i++){
                str_way = str_way + listPoints.get(i).latitude + "," + listPoints.get(i).longitude + "|";
                // str_way.concat(listPoints.get(i).latitude + "," + listPoints.get(i).longitude + "|");
            }
            str_way = str_way + listPoints.get(nPerson-2).latitude + "," + listPoints.get(nPerson-2).longitude;
        }
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + str_way + "&"  + mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://www.google.com/maps/dir/?api=1&" + output + "?" + param;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setPackage("com.google.android.apps.maps");
        startActivity(intent);
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
                points.add(yourPosition);
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
                        Location currentLocation = (Location) task.getResult();;
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                DEFAULT_ZOOM);
                        LatLng pos= new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                        //listPoints.add(pos);
                        yourPosition=pos;
                        mMap.addMarker(new MarkerOptions().position(pos));

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
        Log.i("TAG", "Mio mac: "+mBluetoothAdapter.getAddress());
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(deviceReceiver, intentFilter);
        if (mBluetoothAdapter.isDiscovering()) {
            Log.i("TAG", "Stava cercando, cancello");
            mBluetoothAdapter.cancelDiscovery();
        }
        Log.i("TAG", "Avvio ricerca");
        mBluetoothAdapter.startDiscovery();
    }

}



