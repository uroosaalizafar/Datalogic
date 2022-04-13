package am.datalogicbarcode.volley;

import android.content.Context;
import android.content.res.Resources;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import am.datalogicbarcode.cert.HttpCertificate;

public class VolleyServices {
    IResult mResultCallback = null;
    Context mContext;
    HttpCertificate httpCertificate= new HttpCertificate();

    public VolleyServices(IResult resultCallback, Context context) {
        mResultCallback = resultCallback;
        mContext = context;
    }

    public void postDataVolley(final String requestType, String url, JSONObject sendObj,HurlStack hurlStack) {
        try {

            RequestQueue queue = Volley.newRequestQueue(mContext, hurlStack);

            JsonObjectRequest jsonObj = new JsonObjectRequest(url, sendObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (mResultCallback != null)
                        mResultCallback.notifySuccess(requestType, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (mResultCallback != null)
                        mResultCallback.notifyError(requestType, error);
                }
            });

            queue.add(jsonObj);

        } catch (Exception e) {

        }
    }

    public void getDataVolley(final String requestType, String url,HurlStack hurlStack) {
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext, hurlStack);

            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (mResultCallback != null)
                                mResultCallback.notifySuccess(requestType, response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (mResultCallback != null)
                                mResultCallback.notifyError(requestType, error);
                        }
                    });

            queue.add(jsonObj);

        } catch (Exception e) {

        }
    }
}
