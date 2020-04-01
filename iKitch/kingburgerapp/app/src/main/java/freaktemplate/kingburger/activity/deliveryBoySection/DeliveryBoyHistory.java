package freaktemplate.kingburger.activity.deliveryBoySection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import freaktemplate.kingburger.R;
import freaktemplate.kingburger.activity.Home;
import freaktemplate.kingburger.adapter.DeliveryAdapter;
import freaktemplate.kingburger.getSet.DeliveryGetSet;
import freaktemplate.kingburger.utils.InternetDialog;
import freaktemplate.kingburger.utils.LanguageSelectore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DeliveryBoyHistory extends AppCompatActivity {
    private ArrayList<DeliveryGetSet> data;
    private ListView lv_history;
    DeliveryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageSelectore.setLanguageIfAR( DeliveryBoyHistory.this );
        setContentView(R.layout.activity_delivery_boy_history);

        initViews();

    }

    private void initViews() {

        ImageView iv_background  = findViewById(R.id.iv_background);
        Glide.with(DeliveryBoyHistory.this).load(R.drawable.ezgif).into(iv_background);
        lv_history = findViewById(R.id.lv_history);

        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setTypeface(Home.tf_main_bold);



        lv_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(DeliveryBoyHistory.this, DeliveryBoyOrderDetail.class);
                i.putExtra("OrderNo",((DeliveryGetSet)adapter.getItem(position)).getOrderNo());
                i.putExtra("isComplete",((DeliveryGetSet)adapter.getItem(position)).getComplete());
                startActivity(i);
            }
        });
        if (Home.isNetworkConnected(DeliveryBoyHistory.this))
            settingData();
        else new InternetDialog(DeliveryBoyHistory.this, new InternetDialog.onRetry() {
            @Override
            public void onReload() {
                settingData();
            }
        }).show();

    }
    private void settingData() {
        //creating a string request to send request to the url
        String hp = getString(R.string.link)+ "api/order_history.php?deliverboy_id=" + getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE).getString("DeliveryUserId", "");

        Log.w(getClass().getName(), hp);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Response", response);
                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("success").equals("1")) {
                                JSONArray ja_order = obj.getJSONArray("order");
                                DeliveryGetSet getSet;
                                data = new ArrayList<>();
                                for (int i = 0; i < ja_order.length(); i++) {
                                    JSONObject jo_orderDetail = ja_order.getJSONObject(i);
                                    getSet = new DeliveryGetSet();
                                    getSet.setOrderNo(jo_orderDetail.getString("order_no"));
                                    getSet.setOrderTimeDate(jo_orderDetail.getString("date"));
                                    getSet.setOrderQuantity(jo_orderDetail.getString("items"));
                                    getSet.setOrderAmount(jo_orderDetail.getString("total_amount"));
                                    getSet.setComplete(jo_orderDetail.getString("status"));
                                    data.add(getSet);
                                }
                                adapter = new DeliveryAdapter(data,DeliveryBoyHistory.this);
                                lv_history.setAdapter(adapter);

                            } else {
                                Toast.makeText(DeliveryBoyHistory.this, obj.getString("order"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurs

                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.e("Status code", String.valueOf(networkResponse.statusCode));
                            Toast.makeText(getApplicationContext(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
        stringRequest.setShouldCache(false);
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

}
