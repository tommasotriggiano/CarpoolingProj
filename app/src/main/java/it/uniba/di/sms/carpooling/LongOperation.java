package it.uniba.di.sms.carpooling;



import android.os.AsyncTask;
import android.util.Log;

public class LongOperation extends AsyncTask<Void, Void, String> {
    @Override
    protected String doInBackground(Void... params) {
        try {

            GMailSender sender = new GMailSender("mobilitymanagercarpooling@gmail.com", "sms18carpooling");
            sender.sendMail("Richiesta affiliazione Carpooling",
                  "Un nuovo utente si è registrato all'applicazione carpooling. " +
                          "Accedi all'app per confermare o rifiutare la sua iscrizione, " +
                          "clicca su questo link https://carpooling.page.link/app","tommaso.triggiano@gmail.com",
                    "mobmanager.grifo@gmail.com");

        } catch (Exception e) {
            Log.e("error", e.getMessage(), e);
            return "Email Not Sent";
        }
        return "Richiesta inviata";
    }

    @Override
    protected void onPostExecute(String result) {

        Log.e("LongOperation",result+"");
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
