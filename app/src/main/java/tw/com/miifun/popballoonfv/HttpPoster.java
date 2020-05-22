package tw.com.miifun.popballoonfv;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by yhorn on 2016/3/13.
 */
public class HttpPoster {

    final static private String appTag = "HttpPoster";
    private static final String MF_ACL_WEBAPI_URL = "http://aclwapi.miifun.com.tw/acl/ws.php";
    private static final String MF_VERSION_WEBAPI_URL = "http://aclwapi.miifun.com.tw/acl/version.php";

    Listener mListener;

    public interface Listener {
        void onComplete(String recv, boolean succeed);
    }

    public void setListener( Listener listener ) {
        mListener = listener;
    }

    private void callback( String recv, boolean succeed ) {
        if ( mListener != null )
            mListener.onComplete( recv, succeed );
    }

    public boolean getLatestVersion( String packagename, boolean block ) {

        // MF_VERSION_WEBAPI_URL
        // SEND {"packagename":"com.miifun.myadal }
        // RECV {"error":0,"version":"1.01.01","song":10}

        JSONObject json = new JSONObject();

        try {
            json.put("packagename", packagename );

        } catch (JSONException e) {
            Log.e( appTag, e.toString() );
            callback( null, false );
            return false;
        }

        if ( block ) {
            String recv = new RequestManager( MF_VERSION_WEBAPI_URL, json.toString()).runInBlockMode();

            if ( recv == null ) {
                callback( null, false );
                return false;
            }
            else {
                callback( recv, true );
                return true;
            }
        }
        else {
            Thread thread = new Thread(new RequestManager( MF_VERSION_WEBAPI_URL, json.toString()));
            thread.start();
            return true;
        }
    }

    public boolean sendCrashLog( String packagename, String version, String crashlog, boolean block ) {

        // MF_ACL_WEBAPI_URL
        // {"packagename":"com.miifun.myadal","version":"1.42.12","crashlog":"asdasdas.edwedwe"}
        JSONObject json = new JSONObject();

        try {
            json.put( "packagename", packagename );
            json.put( "version", version );
            json.put( "crashlog", crashlog );
        } catch (JSONException e) {
            Log.e( appTag, e.toString() );
            callback( null, false );
            return false;
        }

        if ( block ) {
            String recv = new RequestManager( MF_ACL_WEBAPI_URL, json.toString()).runInBlockMode();

            if ( recv == null ) {
                callback( null, false );
                return false;
            }
            else {
                callback( recv, true );
                return true;
            }
        }
        else {
            Thread thread = new Thread(new RequestManager( MF_ACL_WEBAPI_URL, json.toString()));
            thread.start();
            return true;
        }
    }

    private class RequestManager implements Runnable {

        private String mPostData;
        private String mUrl;

        public RequestManager(String url, String data) {
            mUrl = url;
            mPostData = data;
        }

        public String runInBlockMode() {

            if (mPostData == null || mPostData.length() <= 0) {
                Log.e( appTag, "err: post null data" );
                return null;
            }

            // do http post request
            try {
                return postHttpData(mPostData);
            } catch (URISyntaxException e) {
                Log.e( appTag, e.toString() );
                return null;
            } catch (IOException e) {
                Log.e( appTag, e.toString() );
                return null;
            } catch (Exception e) {
                Log.e( appTag, e.toString() );
                return null;
            } catch (OutOfMemoryError e) {
                Log.e( appTag, e.toString() );
                return null;
            }
        }

        @Override
        public void run() {
            String recv = runInBlockMode();

            if ( recv == null ) {
                callback( null, false );
            }
            else {
                callback( recv, true );
            }
        }

        private String postHttpData(String postData)
                throws URISyntaxException, IOException, OutOfMemoryError {

            // Log.i(appName, "post: " + postData);

            String response = "";
            URL url = new URL( mUrl );
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            conn.setFixedLengthStreamingMode(postData.getBytes("UTF-8").length);
            // conn.setAllowUserInteraction(false);

            // conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            BufferedOutputStream writer = new BufferedOutputStream(conn.getOutputStream());
            writer.write(postData.getBytes("UTF-8"));
            writer.flush();
            writer.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    response = response + line + "\r\n";
                }

                reader.close();
            } else {
                Log.w(appTag, "bad responseCode = " + responseCode);
                response = "";
            }

            conn.disconnect();

            // Log.i(appTag, "recv: " + response);
            return response;
        }
    }
}
