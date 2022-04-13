package am.datalogicbarcode.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import am.datalogicbarcode.R;
import am.datalogicbarcode.adapter.PalletAdapter;
import am.datalogicbarcode.broadcaster.NetworkchangeReceiver;
import am.datalogicbarcode.cert.HttpCertificate;
import am.datalogicbarcode.cert.HttpsTrustManager;
import am.datalogicbarcode.model.Pallet;
import am.datalogicbarcode.uri.URL;
import am.datalogicbarcode.volley.IResult;
import am.datalogicbarcode.volley.VolleyServices;

public class BarcodeActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_save;
    TextView tv_pallet_no, tv_total_meters, tv_total_rolls;
    EditText pallet_et, roll_et;
    ProgressBar progressBar;
    RelativeLayout rl_count;
    BottomNavigationView bottomNavigationView;
    ListView datalistview;
    private BroadcastReceiver mNetworkReceiver;
    String androidID;
    private String TAG = "MainActivity";
    IResult mResultCallback = null;
    VolleyServices mVolleyService;
    JSONObject sendObj = null;
    HttpCertificate httpCertificate;
    HurlStack hurlStack;
    String device_name;
    TextView tv_device_id;
    PalletAdapter palletAdapter;
    List<Pallet> palletList;
    String pallet_number;
    String roll_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        initViews(savedInstanceState);
        RegisteredBroadcast();
        androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        initVolleyCallback();
        mVolleyService = new VolleyServices(mResultCallback, this);
        mVolleyService.getDataVolley("Device", URL.getDispatchDeviceId(androidID), hurlStack);
    }

    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
                try {
                    if (requestType.equals("Device")) {

                        device_name = response.getString("div_name");
                        tv_device_id.setText(device_name);

                    } else if (requestType.equals("PalletNumber")) {

                        Pallet pallet_model;
                        progressBar.setVisibility(View.GONE);
                        JSONArray obj = response.getJSONArray("items");
                        Log.e("obj", obj.toString());
                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject jsonObject = obj.getJSONObject(i);
                            String lotno = jsonObject.getString("lotno");
                            String rollno = jsonObject.getString("rollno");
                            String prodqty = jsonObject.getString("prodqty");
                            String article_name = jsonObject.getString("article_name");
                            pallet_model = new Pallet(lotno, article_name, rollno, prodqty);
                            palletList.add(pallet_model);
                        }
                        palletAdapter = new PalletAdapter(getApplicationContext(), R.layout.pallet_data_list_text, palletList);
                        datalistview.setAdapter(palletAdapter);
                        datalistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> myAdapter, View myView, int position, long mylng) {
                                Pallet pallet_model1 = (Pallet) myAdapter.getItemAtPosition(position);
                                roll_no = pallet_model1.getRoll_no();
                                //delete(roll_no, pallet_et.getText().toString().trim(), androidID);

                            }
                        });

                    } else if (requestType.equals("PalletCount")) {

                        JSONArray obj = response.getJSONArray("items");
                        Log.e("obj", obj.toString());
                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject jsonObject = obj.getJSONObject(i);
                            int tot_rolls = jsonObject.getInt("tot_rolls");
                            int tot_mtr = jsonObject.getInt("tot_mtr");
                            tv_total_rolls.setText(String.valueOf(tot_rolls));
                            if (tot_mtr >= 1000) {
                                tv_total_meters.setText(String.valueOf(tot_mtr));
                                tv_total_meters.setTextColor(getResources().getColor(R.color.colorRed));
                                tv_total_meters.setTypeface(tv_total_meters.getTypeface(), Typeface.BOLD);
                                tv_total_meters.setTextSize(20);

                            } else if (String.valueOf(tot_mtr) == "null") {
                                tv_total_meters.setText("0");
                                tv_total_meters.setTextSize(15);
                            } else {
                                tv_total_meters.setText(String.valueOf(tot_mtr));
                                tv_total_meters.setTextColor(getResources().getColor(R.color.black));
                                tv_total_meters.setTextSize(15);
                            }
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
            }
        };
    }

    public void RegisteredBroadcast() {
        mNetworkReceiver = new NetworkchangeReceiver();
        registerNetworkBroadcastForNougat();
    }

    public static void dialog(boolean value) {

        if (value) {
            //Connected

            Handler handler = new Handler();
            Runnable delayrunnable = new Runnable() {
                @Override
                public void run() {
                    //3sec after connected
                }
            };
            handler.postDelayed(delayrunnable, 3000);
        } else {
            //not connected
        }
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    private void initViews(Bundle savedInstanceState) {
        //initializing views and objects
        palletList = new ArrayList<>();
        rl_count = findViewById(R.id.rl_count);
        progressBar = findViewById(R.id.progressBar1);
        tv_pallet_no = findViewById(R.id.tv_pallet_no);
        tv_device_id = findViewById(R.id.tv_device_id);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        datalistview = findViewById(R.id.data_listview);
        pallet_et = findViewById(R.id.edit_pallet_no);
        roll_et = findViewById(R.id.edit_roll_no);
        tv_total_meters = findViewById(R.id.tv_total_meters);
        tv_total_rolls = findViewById(R.id.tv_total_rolls);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_pallet);
        HttpsTrustManager.allowAllSSL();
        httpCertificate = new HttpCertificate();
        hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(java.net.URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(httpCertificate.getSSLSocketFactory(getResources()));
                    httpsURLConnection.setHostnameVerifier(httpCertificate.getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_new_pallet:
                        Intent intent = new Intent(BarcodeActivity.this, BarcodeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.action_history:
                        Intent history_intent = new Intent(BarcodeActivity.this, HistoryActivity.class);
                        history_intent.putExtra("device_name", device_name);
                        history_intent.putExtra("device", androidID);
                        startActivity(history_intent);
                        break;

                }
                return true;
            }
        });
        pallet_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    roll_et.setVisibility(View.GONE);
                    btn_save.setVisibility(View.GONE);
                    tv_total_meters.setText("0");
                    tv_total_rolls.setText("0");
                    palletList.clear();
                } else {
                    palletList.clear();
                    tv_total_meters.setText("0");
                    tv_total_rolls.setText("0");
                    pallet_number = s.toString().trim();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub7
                int et_pallet_count = pallet_et.getText().length();
                if (et_pallet_count == 4) {
                    rl_count.setVisibility(View.VISIBLE);
                    roll_et.setVisibility(View.VISIBLE);
                    roll_et.clearFocus();
                    roll_et.requestFocus();
                    mVolleyService.getDataVolley("PalletNumber", URL.getDispatchPalletNo(pallet_et.getText().toString().trim()), hurlStack);
                    mVolleyService.getDataVolley("PalletCount", URL.getDispatchPalletCount(pallet_et.getText().toString().trim()), hurlStack);
                }


            }
        });

        roll_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    pallet_et.setVisibility(View.GONE);
                    tv_pallet_no.setVisibility(View.VISIBLE);
                    tv_pallet_no.setText(pallet_number);
                }
            }
        });
        roll_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (s.toString().trim().length() >= 1) {
                    btn_save.setVisibility(View.VISIBLE);
                } else {
                    btn_save.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public void onClick(View v) {

    }
    
      @Override
    protected void onResume() {
        super.onResume();

        Log.i(LOGTAG, "onResume");

        // If the decoder instance is null, create it.
        if (decoder == null) {
            // Remember an onPause call will set it to null.
            Log.i("Decodeer", "onResume");
            decoder = new BarcodeManager();
        }

        // From here on, we want to be notified with exceptions in case of errors.
        ErrorManager.enableExceptions(true);

        try {

            // Create an anonymous class.
            listener = new ReadListener() {


                // Implement the callback method.
                @Override
                public void onRead(DecodeResult decodeResult) {
                    Log.i("Decode Result", decodeResult.getText());
                    // Change the displayed text to the current received result.
                    roll_et.setText(decodeResult.getText());
//                    GET(decodeResult.getText());
                    postDataUsingVolley(decodeResult.getText(), pallet_et.getText().toString().trim(), "", androidID);
                }

            };

            // Remember to add it, as a listener.
            decoder.addReadListener(listener);

        } catch (DecodeException e) {
            Log.e(LOGTAG, "Error while trying to bind a listener to BarcodeManager", e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i(LOGTAG, "onPause");

        // If we have an instance of BarcodeManager.
        if (decoder != null) {
            try {
                // Unregister our listener from it and free resources.
                decoder.removeReadListener(listener);

                // Let the garbage collector take care of our reference.
                decoder = null;
            } catch (Exception e) {
                Log.e(LOGTAG, "Error while trying to remove a listener from BarcodeManager", e);
            }
        }
    }

}
