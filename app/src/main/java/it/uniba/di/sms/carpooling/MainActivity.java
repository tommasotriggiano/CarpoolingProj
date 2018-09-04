package it.uniba.di.sms.carpooling;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.sms.carpooling.rankUser.Rank;
import it.uniba.di.sms.carpooling.todayRide.TodayMyRidesFragment;
import it.uniba.di.sms.carpooling.userApproved.ApproveFragment;

import static it.uniba.di.sms.carpooling.R.drawable.badge_background;
import static it.uniba.di.sms.carpooling.R.drawable.ic_homewhite;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OfferRideFragment.OnShowRideOfferedListener, MyRidesFragment.OnAddRideOfferedListener,OfferRideFragment.GoToProfileListener,
        MyRidesFragment.ShowRideRequiredListener, UserNotRegister.OpenProfileListener{
    private ImageView profile;
    private TextView hello;
    private TextView affiliation;
    private TextView affiliation2;
    private String urlImageProfile;
    private ProgressBar progressBar;
    private FirebaseAuth authInstance = FirebaseAuth.getInstance();
    private DocumentReference rfUser;
    DocumentReference adminrf;
    private NavigationView navigationView;
    private ListenerRegistration listenerRegistrationAdmin;
    private ListenerRegistration listenerRegistrationUser;
    private ListenerRegistration listenerRegistrationImage;
    private ListenerRegistration listenerRegistrationUserCount;
    FirebaseUser userAuth;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userAuth = authInstance.getCurrentUser();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //instanzio l'oggetto per l'header della navigation view
        View header = navigationView.getHeaderView(0);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        affiliation2 = (TextView)  findViewById(R.id.hamburger_count);
        progressBar=(ProgressBar)header.findViewById(R.id.progressBar);
        navigationView.setNavigationItemSelectedListener(this);
        startService(new Intent(getBaseContext(),ServiceReceiver.class));

        profile = (CircleImageView) header.findViewById(R.id.imageProfile);
        hello = (TextView)header.findViewById(R.id.hello);
        affiliation = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_approvazione));
        if(getIntent() != null && getIntent().getExtras() != null) {
            String required=getIntent().getExtras().getString("REQUIRED");
            if (getIntent().getExtras().getString("REQUIRED")!= null){
                showRideRequired();}
            /*if (getIntent().getExtras().getString("sendToMyRides")!= null){
                onShowRideOffered();}*/
        }

        if (userAuth != null) {
            rfUser = FirebaseFirestore.getInstance().collection("Users").document(userAuth.getUid());
            adminrf = FirebaseFirestore.getInstance().collection("Admin").document(userAuth.getUid());
        }




        listenerRegistrationAdmin = adminrf.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    initializeBadge();
                    progressBar.setVisibility(View.INVISIBLE);
                    navigationView.setCheckedItem(R.id.nav_home);
                    navigationView.getMenu().findItem(R.id.nav_home).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_myrides).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_searchride).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_offeraride).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_points).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_approvazione).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_settings).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);

                }
                else
                    listenerRegistrationUser = rfUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                            if (!(documentSnapshot.exists())) {
                                UserNotRegister userNotRegisterFragment= new UserNotRegister();
                                FragmentTransaction userNotRegisterFT = getSupportFragmentManager().beginTransaction();
                                userNotRegisterFT.replace(R.id.content_frame,userNotRegisterFragment);
                                userNotRegisterFT.commit();

                                progressBar.setVisibility(View.INVISIBLE);
                                navigationView.getMenu().findItem(R.id.nav_registration).setVisible(true);
                                navigationView.getMenu().findItem(R.id.nav_settings).setVisible(true);
                                navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);

                            } else {
                                Map<String, Object> user = documentSnapshot.getData();
                                String name = (String) user.get("name");
                                hello.setText(name);
                                boolean ap = (boolean) user.get("approved");
                                if (!ap) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    navigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_settings).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    navigationView.setCheckedItem(R.id.nav_home);
                                    navigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_home).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_myrides).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_searchride).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_offeraride).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_points).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_settings).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
                                }
                            }

                        }
                    });

            }
        });

        listenerRegistrationImage = rfUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(e != null){
                    Log.e(TAG,e.toString());
                }
                if(documentSnapshot.exists()){
                    Map<String,Object> map = documentSnapshot.getData();
                    if(map.get("urlProfileImage")!= null){
                        urlImageProfile = map.get("urlProfileImage").toString();
                        //attraverso la libreria picasso carico l'immagine nella ImageView preimpostata
                        Picasso.with(MainActivity.this).load(urlImageProfile).into(profile);
                    }


            }
        }});
    }

    private void initializeBadge() {
        CollectionReference user = FirebaseFirestore.getInstance().collection("Users");
        listenerRegistrationUserCount = user.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.e(TAG,e.toString());
                }
                int count = 0;
                for(DocumentSnapshot d : documentSnapshots.getDocuments()){
                    String id = d.getData().get("id").toString();
                    if(!(id.equals(userAuth.getUid()))){
                        boolean ap = (boolean) d.getData().get("approved");
                        if(!ap){
                            count++;
                        }
                    }
                }
                if(count != 0){
                        affiliation2.setVisibility(View.VISIBLE);
                        affiliation2.setText(String.valueOf(count));
                        affiliation.setVisibility(View.VISIBLE);
                        affiliation.setText(String.valueOf(count));
                        affiliation.setTextColor(getResources().getColor(R.color.colorAccent));
                        affiliation.setTypeface(null,Typeface.BOLD);
                        affiliation.setTextSize(16);
                        affiliation.setGravity(Gravity.CENTER_VERTICAL);
                }
                else{
                    affiliation.setVisibility(View.GONE);
                    affiliation2.setVisibility(View.GONE);
                }


            }
        });
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        switch(id){
            case (R.id.nav_home):
            {
                fragment=new TodayMyRidesFragment();
                break;
            }
            case (R.id.nav_registration):
            {
                fragment=new RegistrationForm();
                break;
            }
            case R.id.nav_profile :
            {
                fragment = new EditProfile();
                break;
            }
            case R.id.nav_myrides:
            {
                fragment= new MyRidesFragment();
                break;
            }
            case R.id.nav_searchride : {
                fragment = new SearchRideFragment();
                break;
            }
            case R.id.nav_points: {
                fragment= new Rank();
                break;
            }
            case R.id.nav_offeraride: {
                fragment = new OfferRideFragment();
                break;
            }
            case R.id.nav_approvazione: {
                fragment = new ApproveFragment();
                break;
            }
            case R.id.nav_settings: {
                break;
            }
            case R.id.nav_logout: {
                if(listenerRegistrationAdmin != null){
                listenerRegistrationAdmin.remove();}
                if(listenerRegistrationUser != null){
                listenerRegistrationUser.remove();}
                if(listenerRegistrationImage != null){
                listenerRegistrationImage.remove();}
                if(listenerRegistrationUserCount != null){
                    listenerRegistrationUserCount.remove();
                }
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                finish();
                break;
            }
            default: {
                break;
            }
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
        navigationView.setCheckedItem(R.id.nav_myrides);
    }

    @Override
    public void onAddRideOffered() {
        OfferRideFragment offerRideFragment= new OfferRideFragment();
        FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
        ft3.replace(R.id.content_frame,offerRideFragment,null);
        ft3.addToBackStack(null);
        ft3.commit();
        navigationView.setCheckedItem(R.id.nav_offeraride);
    }


    @Override
    public void goToProfile() {
        EditProfile editProfileFragment = new EditProfile();
        FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
        ft4.replace(R.id.content_frame, editProfileFragment, null);
        ft4.addToBackStack(null);
        ft4.commit();
        navigationView.setCheckedItem(R.id.nav_profile);
    }

    @Override
    public void showRideRequired() {
        MyRidesFragment myRidesFragment = new MyRidesFragment();
        String required= getIntent().getStringExtra("REQUIRED");
        Bundle data= new Bundle();
        data.putString("REQUIRED",required);
        myRidesFragment.setArguments(data);
        FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
        ft5.replace(R.id.content_frame, myRidesFragment, null);
        ft5.commit();
        navigationView.setCheckedItem(R.id.nav_myrides);
    }

    @Override
    public void openProfile() {
        RegistrationForm registrationForm= new RegistrationForm();
        FragmentTransaction ft6 = getSupportFragmentManager().beginTransaction();
        ft6.replace(R.id.content_frame, registrationForm, null);
        ft6.commit();
        navigationView.setCheckedItem(R.id.nav_profile);
    }
}

