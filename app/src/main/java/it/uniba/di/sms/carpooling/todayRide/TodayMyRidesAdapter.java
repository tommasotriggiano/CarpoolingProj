package it.uniba.di.sms.carpooling.todayRide;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.carpooling.DriverTrackingActivity;
import it.uniba.di.sms.carpooling.PassengerActivity;
import it.uniba.di.sms.carpooling.PassengerAdapter;
import it.uniba.di.sms.carpooling.Passeggero;
import it.uniba.di.sms.carpooling.PrepareToTrackingActivity;
import it.uniba.di.sms.carpooling.R;

/**
 * Created by loiodice on 09/07/2018.
 */

public class TodayMyRidesAdapter extends RecyclerView.Adapter<TodayMyRidesViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {
    private ArrayList<Map<String,Object>> itemTodayRide;
    private Context context;
    private ArrayList passenger;
    FirebaseUser user;
    String[] imeiPasseggeri;
    double[] arrayLat;
    double[] arrayLong;
    private int i;
    private int j;
    boolean GpsStatus = false;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public TodayMyRidesAdapter(ArrayList<Map<String,Object>> itemTodayRide, Context context){
        this.itemTodayRide = itemTodayRide;
        this.context = context;
    }
    @Override
    public TodayMyRidesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_rides, parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        TodayMyRidesViewHolder ride = new TodayMyRidesViewHolder(layoutView,context,itemTodayRide);
        return ride;
    }

    @Override
    public void onBindViewHolder(final TodayMyRidesViewHolder holder, final int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        Context context1 = holder.immagine.getContext();
        final Map<String, Object> passaggio = itemTodayRide.get(position);
        holder.data.setText((String) passaggio.get("dataPassaggio"));
        holder.giorno.setText((String) passaggio.get("giorno"));
        holder.ora.setText((String) passaggio.get("ora"));
        holder.casa.setText((String) passaggio.get("tipoViaggio"));

        final Map<String, Object> autista = (Map<String, Object>) passaggio.get("autista");
        holder.telefono.setText((String) autista.get("phone"));
        holder.cognome.setText((String) autista.get("surname"));
        holder.nome.setText((String) autista.get("name"));
        if (autista.get("urlProfileImage") != null) {
            Picasso.with(context1).load(autista.get("urlProfileImage").toString()).into(holder.immagine);
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.cardView2.getVisibility() == View.GONE && holder.listView.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(holder.cardView2);
                    holder.cardView2.setVisibility(View.VISIBLE);
                    if (itemTodayRide.get(position).get("passeggeri") != null) {
                        datiPasseggeri(position, holder.listView);
                        holder.passengers.setVisibility(View.VISIBLE);
                        holder.listView.setVisibility(View.VISIBLE);
                    }
                    holder.startTracking.setVisibility(View.VISIBLE);
                } else if (holder.cardView2.getVisibility() == View.VISIBLE && holder.listView.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(holder.cardView2);
                    holder.cardView2.setVisibility(View.GONE);
                    if (itemTodayRide.get(position).get("passeggeri") != null) {
                        holder.passengers.setVisibility(View.GONE);
                        holder.listView.setVisibility(View.GONE);
                    }
                    holder.startTracking.setVisibility(View.GONE);
                }
            }
        });

        holder.startTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.enable();
                }

                if (autista.get("id").toString().equals(user.getUid())) {
                    checkLocationPermission();
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (!GpsStatus) {
                        Intent intentReqGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intentReqGPS);
                    }
                    if (GpsStatus) {
                        final ArrayList<Double> array_lon = new ArrayList<>();
                        final ArrayList<String> IMEIPasseggeri = new ArrayList<>();
                        final Intent intent = new Intent(context, DriverTrackingActivity.class);
                        if(itemTodayRide.get(position).get("passeggeri") != null){
                        final Map<String,Object> passeggeri = (Map<String, Object>) itemTodayRide.get(position).get("passeggeri");
                            arrayLat = new double[passeggeri.size()];
                            arrayLong = new double[passeggeri.size()];
                            imeiPasseggeri = new String[passeggeri.size()];
                            Log.i("TAG",""+passeggeri.size());
                            if(passaggio.get("tipoViaggio").equals(context.getResources().getString(R.string.HomeWork))){
                              i = 0;
                                for(String id: passeggeri.keySet()){
                                    DocumentReference user = FirebaseFirestore.getInstance().collection("Users").document(id);
                                    user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Map<String,Object> userAddress = (Map<String, Object>) documentSnapshot.getData().get("userAddress");
                                        arrayLat[i] = (Double)userAddress.get("latitude");
                                        arrayLong[i] = (Double)userAddress.get("longitude");
                                        imeiPasseggeri[i]=documentSnapshot.getData().get("IMEI").toString();
                                        i++;

                                        intent.putExtra("STRING_ARRAY_DOUBLE_LAT", arrayLat);
                                        intent.putExtra("STRING_ARRAY_DOUBLE_LON", arrayLong);
                                        intent.putExtra("STRING_ARRAY_IMEI", imeiPasseggeri);
                                        //Log.i("TAG","START ACTIVITY: "+imeiPasseggeri.toString());
                                        if (i == passeggeri.size()){
                                            int nPerson = passeggeri.size()+1;
                                            intent.putExtra("STRING_NPERSON", nPerson);
                                            Map<String,Object> companyAddress = (Map<String, Object>) autista.get("userCompany");
                                            intent.putExtra("STRING_STREAT_ADDRESS", companyAddress.get("address").toString());
                                            String idPassaggio = passaggio.get("idPassaggio").toString();
                                            intent.putExtra("IdPassaggio",idPassaggio);
                                            context.startActivity(intent);
                                        }
                                    }

                                });
                            }
                            }
                            else{
                                j =0;
                                for(String id: passeggeri.keySet()){
                                    DocumentReference user = FirebaseFirestore.getInstance().collection("Users").document(id);
                                    user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Map<String,Object> userCompany = (Map<String, Object>) documentSnapshot.getData().get("userCompany");
                                        arrayLat[j] = (Double)userCompany.get("latitude");
                                        arrayLong[j] = (Double)userCompany.get("longitude");
                                        imeiPasseggeri[j]=documentSnapshot.getData().get("IMEI").toString();
                                        j++;

                                        intent.putExtra("STRING_ARRAY_DOUBLE_LAT", arrayLat);
                                        intent.putExtra("STRING_ARRAY_DOUBLE_LON", arrayLong);
                                        intent.putExtra("STRING_ARRAY_IMEI", imeiPasseggeri);
                                    }
                                });
                            }
                            if(j == passeggeri.size()){
                                int nPerson = passeggeri.size()+1;
                                intent.putExtra("STRING_NPERSON", nPerson);
                                Map<String,Object> autistaAddress = (Map<String, Object>) autista.get("userAddress");
                                intent.putExtra("STRING_STREAT_ADDRESS", autistaAddress.get("address").toString());
                                String idPassaggio = passaggio.get("idPassaggio").toString();
                                intent.putExtra("IdPassaggio",idPassaggio);
                                context.startActivity(intent);
                            }}


                        }
                        else{

                            int nPerson = 1;
                            if(passaggio.get("tipoViaggio").equals(context.getResources().getString(R.string.HomeWork))){
                                Map<String,Object> companyAddress = (Map<String, Object>) autista.get("userCompany");
                                intent.putExtra("STRING_STREAT_ADDRESS", companyAddress.get("address").toString());
                                intent.putExtra("STRING_NPERSON", nPerson);
                                String idPassaggio = passaggio.get("idPassaggio").toString();
                                intent.putExtra("IdPassaggio",idPassaggio);
                                context.startActivity(intent);

                            }else {
                                intent.putExtra("STRING_NPERSON", nPerson);
                                Map<String,Object> autistaAddress = (Map<String, Object>) autista.get("userAddress");
                                intent.putExtra("STRING_STREAT_ADDRESS", autistaAddress.get("address").toString());
                                String idPassaggio = passaggio.get("idPassaggio").toString();
                                intent.putExtra("IdPassaggio",idPassaggio);
                                context.startActivity(intent);

                            }

                        }


                    }
                } else {
                    checkLocationPermission();
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (!GpsStatus) {
                        Intent intentReqGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intentReqGPS);
                    }
                    if (GpsStatus) {
                        final Intent intent = new Intent(context, PassengerActivity.class);
                        if(passaggio.get("tipoViaggio").equals(context.getResources().getString(R.string.HomeWork))){
                           HashMap<String,Object> userCompany = (HashMap<String, Object>) autista.get("userCompany");
                           double dest_lat = (Double) userCompany.get("latitude");
                           double dest_lon = (Double) userCompany.get("longitude");
                            intent.putExtra("LAT_DEST", dest_lat);
                            intent.putExtra("LON_DEST", dest_lon);
                            String imeiAutista = autista.get("IMEI").toString();
                            intent.putExtra("ImeiAutista",imeiAutista);
                            context.startActivity(intent);
                        }
                        else{
                            DocumentReference userRf = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
                            userRf.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Map<String,Object> userAddress = (Map<String, Object>) documentSnapshot.getData().get("userAddress");
                                    double dest_lat = (Double) userAddress.get("latitude");
                                    double dest_lon = (Double) userAddress.get("longitude");
                                    intent.putExtra("LAT_DEST", dest_lat);
                                    intent.putExtra("LON_DEST", dest_lon);
                                    String imeiAutista = autista.get("IMEI").toString();
                                    intent.putExtra("ImeiAutista",imeiAutista);
                                    context.startActivity(intent);
                                }
                            });

                        }
                    }
                }


            }
        });
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }



    }

    private void datiPasseggeri(int position, final ListView listView) {
        passenger = new ArrayList<Passeggero>();
        if(itemTodayRide.get(position).get("passeggeri") != null){
        final Map<String,Object> passeggeri = (Map<String,Object>) itemTodayRide.get(position).get("passeggeri");
            for(String key: passeggeri.keySet()){
            DocumentReference pass = FirebaseFirestore.getInstance().collection("Users").document(key);
                pass.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String,Object> map = documentSnapshot.getData();
                    String nome = map.get("name").toString();
                    String cognome = map.get("surname").toString();
                    String telefono = map.get("phone").toString();
                    String urlProfile = null;
                    if(map.get("urlProfileImage") != null){
                    urlProfile = map.get("urlProfileImage").toString();}
                    Passeggero psg = new Passeggero(nome,cognome,telefono,urlProfile);
                    passenger.add(psg);
                    PassengerAdapter passengerAdapter = new PassengerAdapter(context,R.layout.list_passenger,passenger);
                    listView.setAdapter(passengerAdapter);
                }
            });
            }
    }
    }


    @Override
    public int getItemCount() {

        return itemTodayRide.size();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(context,
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
}
