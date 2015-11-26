package io.github.pratik98.reddit_oauth;

import android.content.Context;
import android.content.SharedPreferences;
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
    Context context;
    private static String CLIENT_ID = "YOUR CLIENT_ID";
    private static String CLIENT_SECRET ="";
    private static final String BASE_URL = "https://www.reddit.com/api/v1/";
	private static String REDIRECT_URI="YOUR reddit_uri(as per your reddit app preferences)";
    RedditRestClient(Context cnt){
        context = cnt;
    }
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

    public void getToken(String relativeUrl,String grant_type,String device_id) throws JSONException {
        client.setBasicAuth(CLIENT_ID,CLIENT_SECRET);
        pref = context.getSharedPreferences("AppPref",Context.MODE_PRIVATE);
        String code =pref.getString("Code", "");

        RequestParams requestParams = new RequestParams();
        requestParams.put("code",code);
        requestParams.put("grant_type",grant_type);
        requestParams.put("redirect_uri", REDIRECT_URI);

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
                Log.i("statusCode", "" + statusCode);


            }
        });

    }


    public void revokeToken()
    {
        client.setBasicAuth(CLIENT_ID,CLIENT_SECRET);
        pref = context.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        String access_token = pref.getString("token","");

        RequestParams requestParams = new RequestParams();
        requestParams.put("token",access_token);
        requestParams.put("token_type_hint","access_token");

        post("revoke_token",requestParams,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i("response", response.toString());
                SharedPreferences.Editor edit = pref.edit();
                edit.remove(token);
                edit.commit();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i("statusCode", "" + statusCode);
            }
        });



    }

}
