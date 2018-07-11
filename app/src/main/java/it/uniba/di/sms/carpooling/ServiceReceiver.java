package it.uniba.di.sms.carpooling;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
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
import java.net.URL;

import cz.msebera.android.httpclient.Header;

/***/

public class ServiceReceiver extends IntentService {

    private final String TAG = "altervista";
    private final String ACCETTATO = "Accettato";
    private final String RIFIUTATO = "Rifiutato";
    private final String REQUEST = "TiChiedonoUnPassaggio;";
    private final String PASSAGGIOACCETTATO = "PassaggioAccettato";
    private final String PASSAGGIORIFIUTATO = "PassaggioRifiutato";

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
        while (n++<2){
            try{
                //TODO per ora bloccato a solo 2 tentativi causa limite altervista a 3000 richieste, da togliere
                Thread.sleep(5000L);
                CheckNote(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }catch (Exception e){}
        }
    }



    public void sendNotify(String Title, String Text, int identifier) {
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
        notificationManager.notify(identifier, n.build());
    }

    public void sendNotify(String Title, String Text, int identifier,boolean sendToMyRides) {
        Intent intentNoti=new Intent(this,MainActivity.class);
        if(sendToMyRides) {
            intentNoti=new Intent(this,MyRidesFragment.class);
        }
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
        notificationManager.notify(identifier, n.build());
    }

    public void CheckNote(String userUid) throws IOException {

        final String idCode = userUid;
        URL paginaURL = new URL("http://carpoolings.altervista.org/QueryNote.php?TokenID="+idCode);
        HttpURLConnection client  = (HttpURLConnection) paginaURL.openConnection();
        InputStream risposta = new BufferedInputStream(client.getInputStream());
        String datiLetti = mostroDati(risposta);
        Toast.makeText(getApplicationContext(), datiLetti, Toast.LENGTH_LONG).show();
        Gson gson = new Gson();
        if(datiLetti.compareTo("null")!=0){        //SE TROVA ALMENO UNA NOTA
            String founderJson = datiLetti;
            NotificaAltervista[] founderArray = gson.fromJson(founderJson, NotificaAltervista[].class);

            for (NotificaAltervista matchFound:founderArray ) {
                if(matchFound.toString().compareTo(ACCETTATO)==0){        //SE IN UNA NOTA C'E' "Accettato"
                    Toast.makeText(getApplicationContext(), "Founded a note", Toast.LENGTH_LONG).show();
                    sendNotify("Carpooling","Sei stato accettato, puoi usufruire dei servizi",0);
                    Log.i(TAG,"nota inviata");
                    deleteNote(ACCETTATO,userUid);
                }
                if(matchFound.toString().compareTo(RIFIUTATO)==0){        //SE IN UNA NOTA C'E' "Accettato"
                    Toast.makeText(getApplicationContext(), "Founded a note", Toast.LENGTH_LONG).show();
                    sendNotify("Carpooling","Non sei stato accettato, spiacente",0);
                    Log.i(TAG,"nota non accettato inviata");
                    deleteNote(ACCETTATO,userUid);
                }
                if(matchFound.toString().contains(REQUEST)) {        //SE IN UNA NOTA C'E' "Ti chiedono un passaggio"
                    Toast.makeText(getApplicationContext(), "Founded a note", Toast.LENGTH_LONG).show();
                    String richiedente=matchFound.toString();
                    String nomeRichiedente = richiedente.substring(richiedente.indexOf(';')+1);
                    sendNotify("Richiesta da " + nomeRichiedente.trim() ,"Vuoi dargli un passaggio?",1,true);
                    Log.i(TAG,REQUEST+nomeRichiedente);
                    deleteNote(richiedente,userUid);
                }
                if(matchFound.toString().compareTo(PASSAGGIOACCETTATO)==0){
                    Toast.makeText(getApplicationContext(), "Founded a note", Toast.LENGTH_LONG).show();
                    sendNotify("Richiesta","La tua richiesta di passaggio è stata accettata",2);
                    Log.i(TAG,"nota passaggio accettato ricevuta");
                    deleteNote(PASSAGGIOACCETTATO,userUid);
                }
                if(matchFound.toString().compareTo(PASSAGGIORIFIUTATO)==0){
                    Toast.makeText(getApplicationContext(), "Founded a note", Toast.LENGTH_LONG).show();
                    sendNotify("Richiesta","La tua richiesta di passaggio è stata rifiutata",2);
                    Log.i(TAG,"nota passaggio rifiutato ricevuta");
                    deleteNote(PASSAGGIORIFIUTATO,userUid);
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


    private void deleteNote(String Note, String TokenID){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        try {
            URL paginaURL = new URL("http://carpoolings.altervista.org/DeleteNote.php?TokenID="+TokenID+"&Note='"+Note+"'");
            String url = "http://carpoolings.altervista.org/DeleteNote.php?TokenID="+TokenID+"&Note="+Note;
            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            RequestParams params = new RequestParams();
            client.get(url, params, new TextHttpResponseHandler() {

                @Override
                public void onStart() {
                    Log.i(TAG,"onStart");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.i(TAG,"succ");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i(TAG,"faill");
                }
            });
        } catch (IOException e) {
            Log.i(TAG,"errore try");
        }

    }
}
