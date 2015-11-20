package io.github.pratik98.reddit_oauth;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
/**
 * Created by Pratik Agrawal on 11/20/2015.
 */
public class RedditRestClient {
    SharedPreferences pref;
    String token;
    private static final String BASE_URL = "https://www.reddit.com/api/v1/";

        private static AsyncHttpClient client = new AsyncHttpClient();

        public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.get(getAbsoluteUrl(url), params, responseHandler);
        }

        public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

            client.post(getAbsoluteUrl(url), params, responseHandler);
        }

        private static String getAbsoluteUrl(String relativeUrl) {
            return BASE_URL + relativeUrl;
        }

    public void getToken(String relativeUrl,String grant_type,String device_id,Context context) throws JSONException {
        client.setBasicAuth("Your Client ID","");
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        String code =pref.getString("Code", "");


        RequestParams requestParams = new RequestParams();
        requestParams.put("code",code);
        requestParams.put("grant_type",grant_type);
        requestParams.put("device_id", device_id);

        post(relativeUrl, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("response",response.toString());
                try {
                    token = response.getString("access_token").toString();
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("token",token);
                    edit.commit();
                    Log.i("Access_token",pref.getString("token",""));
                }catch (JSONException j)
                {
                    j.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i("statusCode",""+statusCode);


            }

        });


    }

}
