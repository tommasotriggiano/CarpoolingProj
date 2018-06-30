package it.uniba.di.sms.carpooling;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

/***/

public class ServiceReceiver extends IntentService {

    private final String TAG = "altervista";

    public ServiceReceiver() {
        super("ServiceReceiver");
        Log.i(TAG,"Created Service");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG,"Service gestito");
           work();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"Service terminato");
    }


    public void work(){
        Log.i(TAG,"on work");
        int n=0;
        while (n++<1){
            try{
                Thread.sleep(2000L);
                Log.i(TAG,"Service second="+n);
NotifyIfAccepted();
            }catch (Exception e){}
        }
    }



    public void sendNotify(String Title, String Text) {
        Intent intentNoti=new Intent(this,MainActivity.class);
        PendingIntent pendingIntentNoti=PendingIntent.getActivity(this, 0, intentNoti, 0);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder n  = new NotificationCompat.Builder(this)
                //.setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(Title)
                .setContentText(Text)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntentNoti)
                .setSound(sound)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n.build());
    }

    public void NotifyIfAccepted() throws IOException {

        Log.i(TAG,"NotifyIfAcceptedStart");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG,"currUser");
        final String idCode = currentUser.getUid();
        Log.i(TAG,"getUid");
        Log.i(TAG,currentUser.getUid());
        URL paginaURL = new URL("http://carpoolings.altervista.org/QueryNote.php?TokenID="+idCode);
        HttpURLConnection client  = (HttpURLConnection) paginaURL.openConnection();
        InputStream risposta = new BufferedInputStream(client.getInputStream());
        String datiLetti = mostroDati(risposta);
        Toast.makeText(getApplicationContext(), datiLetti, Toast.LENGTH_LONG).show();
        Log.i(TAG,datiLetti);
        Gson gson = new Gson();
        Log.i(TAG,"response: "+datiLetti);
        if(datiLetti.compareTo("null")!=0){        //SE TROVA ALMENO UNA NOTA
            String founderJson = datiLetti;
            NotificaAltervista[] founderArray = gson.fromJson(founderJson, NotificaAltervista[].class);

            for (NotificaAltervista matchFound:founderArray ) {
                if(matchFound.toString().compareTo("Accettato")==0){        //SE IN UNA NOTA C'E' "Accettato"
                    Toast.makeText(getApplicationContext(), "Founded a note", Toast.LENGTH_LONG).show();
                    sendNotify("Titolo","Questa è una nota e tu sei stato accettato");
                    Log.i(TAG,"nota inviata");
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Nessuna nota trovata", Toast.LENGTH_LONG).show();
            Log.i(TAG,"nota non trovata");
        }


    }

    private static String mostroDati(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {
            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
