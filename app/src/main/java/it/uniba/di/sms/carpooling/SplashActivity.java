package it.uniba.di.sms.carpooling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends Activity {
    int SPLASH_TIME_OUT = 2000;
    Animation up_to_down,downtoup;
    ImageView splash;
    TextView text;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        user = FirebaseAuth.getInstance().getCurrentUser();

        splash=(ImageView)findViewById(R.id.image) ;
        text=(TextView) findViewById(R.id.textView3);
        up_to_down= AnimationUtils.loadAnimation(this,R.anim.up_to_down);
        splash.setAnimation(up_to_down);
        downtoup= AnimationUtils.loadAnimation(this,R.anim.downtoup);
        text.setAnimation(downtoup);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user != null && user.isEmailVerified()) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
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
