package it.uniba.di.sms.carpooling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class SplashActivity extends Activity {
    int SPLASH_TIME_OUT = 2000;
    Animation up_to_down,downtoup;
    ImageView splash;
    TextView text;
    FirebaseUser user;
    DatabaseReference ref;
    DatabaseReference adminRef;
    String adminUid = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("users");
        adminRef = FirebaseDatabase.getInstance().getReference("admin");

        splash=(ImageView)findViewById(R.id.image) ;
        text=(TextView) findViewById(R.id.textView3);
        up_to_down= AnimationUtils.loadAnimation(this,R.anim.up_to_down);
        splash.setAnimation(up_to_down);
        downtoup= AnimationUtils.loadAnimation(this,R.anim.downtoup);
        text.setAnimation(downtoup);

        final ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //se l'istanza del database esiste, quindi l'utente ha già inserito le sue informazioni, si passa all'activity principale
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }
                else{
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    finish();}
            }

            final ValueEventListener adminListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        finish();
                    }
                    else{

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"Chiamata On cancelled"+databaseError.getDetails());}

        };

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user != null && user.isEmailVerified()) {
                    adminRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            } else {
                                ref.child(user.getUid()).addValueEventListener(listener);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    /*
                    if(adminUid.equals(user.getUid())){
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    }
                    else{

                    ref.child(user.getUid()).addValueEventListener(listener);}}*/
                }
                    else{
                        Intent i = new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(i);
                        finish();
                        }
                        }
        }, SPLASH_TIME_OUT);
    }
}
