package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequiredMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    public LinearLayout richiesti;
    public LinearLayout richiesti2;
    public RelativeLayout rich;
    public CircleImageView immagine;
    public TextView data,giorno,ora,casa,telefono,nome,cognome,status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_required_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent receive = getIntent();
        HashMap<String,Object> richiesta = (HashMap<String,Object>)receive.getSerializableExtra("richiestaPassaggio");

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

        Map<String,Object> passaggio = (Map<String,Object>) richiesta.get("passaggio");
        data.setText((String)passaggio.get("data"));
        giorno.setText((String)passaggio.get("giorno"));
        ora.setText((String)passaggio.get("ora"));
        casa.setText((String)passaggio.get("tipoViaggio"));
        Map<String,Object> autista = (Map<String,Object>) richiesta.get("autista");
        Map<String,Object>address =(Map<String,Object>) autista.get("userAddress");

        Map<String,Object>passeggero =(Map<String,Object>) richiesta.get("passeggero");
        Map<String,Object>addressP =(Map<String,Object>) passeggero.get("userAddress");

        telefono.setText((String)autista.get("phone"));
        cognome.setText((String)autista.get("surname"));
        nome.setText((String)autista.get("name"));
        status.setText((String)richiesta.get("status"));
        if(autista.get("urlProfileImage") != null){
            Picasso.with(RequiredMapsActivity.this).load(autista.get("urlProfileImage").toString()).into(immagine);}
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
        HashMap<String,Object> richiesta = (HashMap<String,Object>)receive.getSerializableExtra("richiestaPassaggio");
        Map<String,Object> autista = (Map<String,Object>) richiesta.get("autista");
        Map<String,Object>address =(Map<String,Object>) autista.get("userAddress");
        Map<String,Object> passeggero = (Map<String,Object>) richiesta.get("passeggero");
        Map<String,Object>addressP =(Map<String,Object>) passeggero.get("userAddress");

        LatLng indirizzoAutista = new LatLng((Double) address.get("latitude"),(Double)address.get("longitude"));
        LatLng indirizzoPassegero = new LatLng((Double) addressP.get("latitude"),(Double)addressP.get("longitude"));

        mMap.addMarker(new MarkerOptions().position(indirizzoAutista).title("autista").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.addMarker(new MarkerOptions().position(indirizzoPassegero).title(getResources().getString(R.string.Home)));

    }
}
