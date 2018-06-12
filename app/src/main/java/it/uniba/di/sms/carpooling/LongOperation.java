package it.uniba.di.sms.carpooling;



import android.os.AsyncTask;
import android.util.Log;

public class LongOperation extends AsyncTask<Void, Void, String> {
    @Override
    protected String doInBackground(Void... params) {
        try {
            GMailSender sender = new GMailSender("mobilitymanagercarpooling@gmail.com", "sms18carpooling");
            sender.sendMail("This is a testing mail",
                    "This is Body of testing mail","devilsparks1171@gmail.com",
                    "devilsparks1171@gmail.com,mobilitymanagercarpooling@gmail.com");

        } catch (Exception e) {
            Log.e("error", e.getMessage(), e);
            return "Email Not Sent";
        }
        return "Email Sent";
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
