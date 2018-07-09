package it.uniba.di.sms.carpooling;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class AltervistaRequest extends AppCompatActivity {

    private final static String TAG = "altervista";
    private FirebaseUser user;

    Context context=this;

    EditText codeField;
    EditText noteField;
    String code;
    String note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altervista_request);

        /**    Guardia temporanea di debug, assicura il login     **/
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.i(TAG,"loggato");
        } else {
            Log.i(TAG," non loggato. Quindi faccio il login  ");
            //TODO elimina questo autologin tanto arrivi qua già loggato
            FirebaseAuth.getInstance().signInWithEmailAndPassword("devilsparks1171@gmail.com","2006germania");
            Log.i(TAG,"Fatto");
            user = FirebaseAuth.getInstance().getCurrentUser();
            Log.i(TAG,"Aggiornato al nuovo utente");
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG,"userRidefinito");
        //codeField.setText(user.getUid());
        Log.i(TAG,"setText");

        /**               Fine Guardia              **/

        ButterKnife.bind(this);

        final EditText codeField =(EditText) findViewById(R.id.codeField);
        final EditText noteField =(EditText) findViewById(R.id.numberField);

        final Button startServiceBtn=(Button) findViewById(R.id.startServiceBtn);
        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(getBaseContext(),ServiceReceiver.class));
            }
        });
        final Button stopServiceBtn=(Button) findViewById(R.id.stopServiceBtn);




        //CheckNote();
/**
 * INSERT BUTTON
 *
 * Inserisce nel server ID e nota nei rispettivi campi codeField e noteField
 */
        Button insert = (Button) findViewById(R.id.InsertButton);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**    Qui vado a settare in automatico il mio token ID     **/
                {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    Log.i(TAG,"userRidefinito");
                    codeField.setText(user.getUid());
                    Log.i(TAG,"setText");
                }

                /**      Invio     **/
               InsertIntoAltervista(codeField.getText().toString(),noteField.getText().toString());

            }
        });

/**
 * SEARCH BUTTON
 *
 * Permette di visualizzare in un Toast le note corrispondenti all'ID in codeField
 */
        Button searchBtn = (Button) findViewById(R.id.buttonSearch);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tokenID=codeField.getText().toString();
                String url = "http://carpoolings.altervista.org/QueryNote.php?TokenID="+tokenID;
                AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                RequestParams params = new RequestParams();
                final ProgressDialog pDialog = new ProgressDialog(AltervistaRequest.this);
                client.get(url, params, new TextHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        pDialog.setMessage("Loading...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(true);
                        pDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Gson gson = new Gson();

                        if(responseString.compareTo("null")!=0){
                            String founderJson = responseString;
                            NotificaAltervista[] founderArray = gson.fromJson(founderJson, NotificaAltervista[].class);

                            pDialog.dismiss();
                            String string="";
                            for (NotificaAltervista matchFound:founderArray ) {
                                string=string+matchFound.toString()+'\n';
                            }
                            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }


    public void InsertIntoAltervista(String idCode,String message){
        Log.i(TAG,"startAltet");

        String url = "http://carpoolings.altervista.org/InsertNote.php?TokenID="+idCode+"&Note="+message;
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        RequestParams params = new RequestParams();
        Log.i(TAG,"param");
        client.get(url, params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                Log.i(TAG,"onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i(TAG,"toast");
                Toast.makeText(getApplicationContext(), "Insert success", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i(TAG,"faill");
                Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_LONG).show();
            }
        });
    }


}
