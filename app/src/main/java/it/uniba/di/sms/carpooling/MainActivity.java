package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OfferRideFragment.OnShowRideOfferedListener, MyRidesFragment.OnAddRideOfferedListener{
    private ImageView profile;
    private String urlImageProfile;
    private FirebaseUser user;
    private DatabaseReference refUser;
    private String adminUid;
    private DatabaseReference adminRef;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = FirebaseAuth.getInstance().getCurrentUser();
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        adminRef = FirebaseDatabase.getInstance().getReference("admin");

        adminRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   /*se nel database degli admin esiste l'uid dell'utente che si è autenticato allora
                   quell'utente è un admin e,oltre a vedere tutte le opzioni visibili ad utenti normali,
                    potrà vedere la voce del menu Affiliazioni
                    */
                   navigationView.getMenu().findItem(R.id.nav_approvazione).setVisible(true);
                   navigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);
               }
               else{
                   /*se nel database degli admin non esiste l'uid dell'utente autenticato
                   allora vorrà dire che l'utente non è un admin
                    */
                   navigationView.getMenu().findItem(R.id.nav_approvazione).setVisible(false);
                   refUser = FirebaseDatabase.getInstance().getReference("users");
                   refUser.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                           if (dataSnapshot.exists()){
                               navigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);
                               //è possibile visionare solo l'opzione per modificare il profilo ma non viene più
                               // mandata la richiesta al mobility manager
                               //navigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
                           }
                           else{
                               //se l'utente non ha registrato i suoi dati non può fare nient'altro se non
                               //fare il logout.
                               navigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
                               navigationView.getMenu().findItem(R.id.nav_myrides).setVisible(false);
                               navigationView.getMenu().findItem(R.id.nav_searchride).setVisible(false);
                               navigationView.getMenu().findItem(R.id.nav_offeraride).setVisible(false);
                               navigationView.getMenu().findItem(R.id.nav_points).setVisible(false);
                               navigationView.getMenu().findItem(R.id.nav_approvazione).setVisible(false);

                           }


                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //instanzio l'oggetto per l'header della navigation view
        View header = navigationView.getHeaderView(0);

        profile = (CircleImageView) header.findViewById(R.id.imageProfile);
        //creo il riferimento per l'utente autenticato
        user = FirebaseAuth.getInstance().getCurrentUser();
        //creo il riferimento al database passaggi
        refUser = FirebaseDatabase.getInstance().getReference("users");

        //cerco nelle informazioni dell'utente autenticato l'url dell'immagine di profilo.
        refUser.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                    //se l'url è null significa che al momento della registrazione l'utente non ha caricato nessuna immagine
                    if(map.get("urlProfileImage")!= null){
                        urlImageProfile = map.get("urlProfileImage").toString();
                        //attraverso la libreria picasso carico l'immagine nella ImageView preimpostata
                        Picasso.with(MainActivity.this).load(urlImageProfile).into(profile);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //if(adminUid.equals(user.getUid())){
            //navigationView.getMenu().findItem(R.id.nav_approvazione).setVisible(true);
        //}
       // else{
        //    navigationView.getMenu().findItem(R.id.nav_approvazione).setVisible(false);
        //}
       // navigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);
        /*getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new OfferRideFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_offeraride);*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_profile) {
            fragment = new RegistrationForm();
        } else if (id == R.id.nav_myrides) {
            fragment= new MyRidesFragment();
        } else if (id == R.id.nav_searchride) {
            fragment= new SearchRideFragment();
        } else if (id == R.id.nav_points) {

        } else if (id == R.id.nav_offeraride) {
          fragment= new OfferRideFragment();

        } else if (id == R.id.nav_settings) {

        }else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }

        //replacing the fragment
        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onShowRideOffered() {
        MyRidesFragment myRidesFragment= new MyRidesFragment();
        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
        ft2.replace(R.id.content_frame,myRidesFragment);
        ft2.commit();
    }

    @Override
    public void onAddRideOffered() {
        OfferRideFragment offerRideFragment= new OfferRideFragment();
        FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
        ft3.replace(R.id.content_frame,offerRideFragment,null);
        ft3.addToBackStack(null);
        ft3.commit();
    }
}
