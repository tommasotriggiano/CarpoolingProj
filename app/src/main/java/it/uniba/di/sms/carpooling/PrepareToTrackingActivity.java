package it.uniba.di.sms.carpooling;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class PrepareToTrackingActivity extends AppCompatActivity {

    //TODO DA SOSTITUIRE CON POSIZIONE DESTINAZIONE
    private final double DESTINAZIONE_LATITUDE = 41.3196635f;
    private final double DESTINAZIONE_LONGITUDE = 16.2838207f;
    private final String LATITUDE = "LAT_DEST";
    private final String LONGITUDE = "LON_DEST";
    boolean GpsStatus = false;


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_to_tracking);



        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        Button button = (Button) findViewById(R.id.btn_passenger);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkLocationPermission();
                LocationManager locationManager = (LocationManager)PrepareToTrackingActivity.this.getSystemService(Context.LOCATION_SERVICE);
                boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if(!GpsStatus){
                    Intent intentReqGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intentReqGPS);
                }
                if(GpsStatus) {
                    Intent intent = new Intent(PrepareToTrackingActivity.this, PassengerActivity.class);
                    intent.putExtra(LATITUDE, DESTINAZIONE_LATITUDE);
                    intent.putExtra(LONGITUDE, DESTINAZIONE_LONGITUDE);
                    startActivity(intent);
                }
            }
        });

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
                        Log.i("TAG", "permissionsGranted()");
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Log.w("TAG", "permissionsDenied()");
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

public boolean checkGPS (){
    LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
    boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    return gpsStatus;
}

public void startTrackingIntent(){
        Intent intent2 = new Intent(PrepareToTrackingActivity.this, PassengerActivity.class);
        intent2.putExtra(LATITUDE,DESTINAZIONE_LATITUDE);
        intent2.putExtra(LONGITUDE,DESTINAZIONE_LONGITUDE);
        startActivity(intent2);
}



}
