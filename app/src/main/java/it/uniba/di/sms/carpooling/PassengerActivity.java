package it.uniba.di.sms.carpooling;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PassengerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng currentPosition;
    private Location start_location;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    String IMEIDriver;
    String idPassaggio;
    private double DESTINATION_LAT = 41.3196635f;
    private double DESTINATION_LoN = 16.2838207f;
    private LocationCallback mLocationCallback;
    protected Location mLocationToConvert; //object containing the latitude and longitude that you want to convert to an address.
    private final String TAG = "TAG";
    final int MAX_DISTANCE_UPDATE= 5;
    final int MAX_INTERVAL= 2000;
    final int MAX_FASTEST_INTERVAL= 1000;
    float[] resultArray = {0,0,0};
    float distanzaIniziale ;
    float distanzaRimanente ;
    float ultimaDistanza = Float.MAX_VALUE ;
    static TextView txt_driver_dist ;
    TextView txt_luogo ;
    TextView txt_distanza ;
    ProgressBar progressBar ;
    Marker markerPosition;
    boolean isNear = false;
    BluetoothAdapter mBluetoothAdapter;
    IntentFilter intentFilter;
    static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    private final BroadcastReceiver deviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("TAG","ACTION: "+action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("TAG","Device name: "+device.getName()+" IMEI: "+device.getAddress());
                if(device.getAddress().compareTo(IMEIDriver)==0)
                {
                    isNear=true;
                }
                else {
                    isNear=false;
                    //Toast.makeText(PassengerActivity.this,"IMEI conducente non rilevato",Toast.LENGTH_LONG).show();
                    Log.i("TAG","IMEI conducente non rilevato");
                }
                setTxt_driver_dist( getString(R.string.tracking_valid) );
                //setTxt_driver_dist( "Connected to " + device.getName() );
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_passenger);
        mapFragment.getMapAsync(this);
        IMEIDriver= getIntent().getStringExtra("ImeiAutista");

        DESTINATION_LAT = getIntent().getDoubleExtra("LAT_DEST",41);
        DESTINATION_LoN = getIntent().getDoubleExtra("LON_DEST",16);
        txt_driver_dist = (TextView) findViewById(R.id.text_driverdist) ;
        txt_luogo = (TextView) findViewById(R.id.txt_location);
        txt_distanza = (TextView) findViewById(R.id.txt_distance);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.i("TAG","onLocationUpdate");
                if (locationResult == null) {
                    Log.i("TAG","locRes null");
                }//all'update fa questo....
                for (Location location : locationResult.getLocations()) {
                    if(location==null)
                        continue;
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
                    Log.i("TAG","Lat:"+location.getLatitude()+" Log:"+location.getLongitude());
                    currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
                    if (markerPosition!=null){
                        markerPosition.setPosition( currentPosition );
                    }
                    mLocationToConvert=new Location("");//provider name is unnecessary
                    mLocationToConvert.setLatitude(location.getLatitude());//your coords of course
                    mLocationToConvert.setLongitude(location.getLongitude());
                    retriveAddress(mLocationToConvert);

                    Location.distanceBetween(mLocationToConvert.getLatitude(),mLocationToConvert.getLongitude(),
                            DESTINATION_LAT, DESTINATION_LoN, resultArray);
                    Log.i(TAG, "Distance"+String.valueOf(resultArray[0]) );
                    txt_distanza.setText( "Distance"+String.valueOf(resultArray[0]) );

                    distanzaRimanente = resultArray[0];
                    if (ultimaDistanza >= distanzaRimanente){
                        ultimaDistanza = distanzaRimanente;
                        float percentuale =  (1-(distanzaRimanente / (distanzaIniziale)))*100;
                        Log.i(TAG,"UltimaDist "+ (ultimaDistanza));
                        Log.i(TAG,"distanzaRima "+ (distanzaRimanente));
                        Log.i(TAG,"Percentuale "+ (percentuale));
                        progressBar.setProgress(  (int)(percentuale) );
                    }


                    if(ultimaDistanza<1000){
                        Log.i(TAG, "Sei arrivato" );
                        Toast.makeText(PassengerActivity.this,
                                "Sei arrivato, controlla il tuo punteggio",
                                Toast.LENGTH_SHORT  ).show();
                        Intent intent = new Intent(PassengerActivity.this, MainActivity.class);
                        //intent.putExtra("VIAGGIO_COMPLETATO",true);
                        startActivity(intent);
                    }
                    launchCheckDriverNear();
                }
            }
        };



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //startService(new Intent(this,LocationService.class));
        LatLng destination = new LatLng(DESTINATION_LAT, DESTINATION_LoN);
        mMap.addMarker(new MarkerOptions().position(destination).title("DESTINAZIONE"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
        if( checkLocationPermission() ){
            getDeviceLocation();
        }
        locationRequest = createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        checkLocationPermission();
        /**
         * Once a location request is in place you can start the regular updates
         * by calling requestLocationUpdates()., the fused location provider either
         * invokes the LocationCallback.onLocationChanged() callback method and
         * passes it a list of Location objects
         */
        mFusedLocationClient.requestLocationUpdates(locationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "permissionsGranted()");
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Log.w(TAG, "permissionsDenied()");
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    /**
     * Trova la posizione attuale, aggiorna currentPosition, e muove su di essa la telecamera
     * Aggiunge un marker sulla posizione attuale
     */
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation()");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try{

           Task task = mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location == null) {
                                Log.i("TAG","task.getResult ha dato null");
                               // Toast.makeText(PassengerActivity.this, "task.getResult ha dato null", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Location currentLocation = location;
                                LatLng pos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                //Aggiounge la posizione attuale
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(pos)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                        .snippet("Tua posizione");
                                markerPosition = mMap.addMarker(markerOptions);
                                currentPosition = pos;
                                start_location = currentLocation;
                                Location.distanceBetween(start_location.getLatitude(), start_location.getLongitude(),
                                        DESTINATION_LAT, DESTINATION_LoN, resultArray);
                                distanzaIniziale = resultArray[0];
                                Log.i(TAG, "DistIniziale=" + distanzaIniziale);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14));
                            }

                        }
                    });

        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    protected LocationRequest createLocationRequest() {
        Log.i(TAG,"create locationRequest");
        locationRequest = new LocationRequest()
                .setInterval(MAX_INTERVAL)
                .setFastestInterval(MAX_FASTEST_INTERVAL)
                .setSmallestDisplacement(MAX_DISTANCE_UPDATE)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    /**
     * Return the String address
     * @param locationToConvert
     * @return the address in format  Via Fraggianni, 79, 76121 Barletta BT, Italy
     */
    public String retriveAddress(Location locationToConvert){
        // Get the location passed to this service through an extra.
        Location location = locationToConvert;
        List<Address> addresses = null;
        String retrivedAddress="";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);// In this sample, get just a single address.
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            Toast.makeText(PassengerActivity.this, "service_not_available",Toast.LENGTH_LONG);
            Log.i(TAG, "service_not_available");
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            //Toast.makeText(PassengerActivity.this, "invalid_lat_long_used " + "Lat= " +
                    //location.getLatitude() + ", Long = " + location.getLongitude(), Toast.LENGTH_LONG);
            Log.i(TAG, "invalid_lat_long_used " + "Lat= " + location.getLatitude() + ", Long = " + location.getLongitude());
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            //Toast.makeText(PassengerActivity.this, "no_address_found",Toast.LENGTH_LONG);
            Log.e(TAG, "no_address_found");
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            retrivedAddress=address.getAddressLine(0);
            //Log.i(TAG,"address_found");
            //Log.i(TAG,address.getLocality());
            Log.i(TAG,address.getAddressLine(0));
            //Toast.makeText(PassengerActivity.this, address.getAddressLine(0),Toast.LENGTH_LONG);
            txt_luogo.setText( address.getAddressLine(0) );
            if(address.getSubLocality() != null)
                Log.i(TAG,address.getSubLocality());
        }
        return retrivedAddress;
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



    static public void setTxt_driver_dist(String txt) {
        txt_driver_dist.setText(txt);
    }

    @Override
    protected void onStop() {
        if (deviceReceiver != null) {
            unregisterReceiver(deviceReceiver);
        }
        super.onStop();
    }

}