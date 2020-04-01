package freaktemplate.kingburger.activity.deliveryBoySection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DeliveryBoyHome extends AppCompatActivity {

    private String status, DeliveryBoyId;
    private ArrayList<DeliveryGetSet> data;
    private ListView lv_orderList;
    DeliveryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy_home);
        LanguageSelectore.setLanguageIfAR( DeliveryBoyHome.this );

        DeliveryBoyId = getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE).getString("DeliveryUserId", null);
    }

    private void iniViews() {
        ImageView iv_background = findViewById(R.id.iv_background);
        Glide.with(DeliveryBoyHome.this).load(R.drawable.ezgif).into(iv_background);

        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setTypeface(Home.tf_main_bold);



        Button btn_my_profile = findViewById(R.id.btn_my_profile);
        btn_my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DeliveryBoyHome.this, DeliveryBoyProfile.class);
                startActivity(i);
            }
        });

        Button btn_order_history = findViewById(R.id.btn_order_history);
        btn_order_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DeliveryBoyHome.this, DeliveryBoyHistory.class);
                startActivity(i);
            }
        });

        TextView txt_name = findViewById(R.id.txt_name);
        txt_name.setText(getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE).getString("DeliveryUserName", ""));

        SwitchCompat sw_radius_onOff = findViewById(R.id.Sw_radius_onoff);
        sw_radius_onOff.setChecked(getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE).getBoolean("isPresent", false));

        sw_radius_onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    status = "yes";
                } else {
                    status = "no";
                }
                if (Home.isNetworkConnected(DeliveryBoyHome.this))
                    sendPresence(status);
                else new InternetDialog(DeliveryBoyHome.this, new InternetDialog.onRetry() {
                    @Override
                    public void onReload() {
                        sendPresence(status);
                    }
                }).show();
            }
        });


        Button btn_signOut = findViewById(R.id.btn_signout);

        btn_signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Home.isNetworkConnected(DeliveryBoyHome.this))
                    sendPresence("no");
                else new InternetDialog(DeliveryBoyHome.this, new InternetDialog.onRetry() {
                    @Override
                    public void onReload() {
                        sendPresence("no");
                    }
                }).show();
                SharedPreferences.Editor edit = getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE).edit();
                edit.putBoolean("isDeliverAccountActive", false);
                edit.putString("DeliveryUserId", "");
                edit.putString("DeliveryUserName", "");
                edit.putString("DeliveryUserPhone", "");
                edit.putString("DeliveryUserEmail", "");
                edit.putString("DeliveryUserVNo", "");
                edit.putString("DeliveryUserVType", "");
                edit.putBoolean("isPresent", false);
                edit.apply();
                Intent i = new Intent(DeliveryBoyHome.this, Home.class);
                startActivity(i);
                finish();

            }
        });

        lv_orderList = findViewById(R.id.lv_orderList);
        lv_orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(DeliveryBoyHome.this, DeliveryBoyOrderDetail.class);
                i.putExtra("OrderNo",((DeliveryGetSet)adapter.getItem(position)).getOrderNo());
                i.putExtra("isComplete",((DeliveryGetSet)adapter.getItem(position)).getComplete());
                startActivity(i);
            }
        });

        if (Home.isNetworkConnected(DeliveryBoyHome.this))
            settingData();
        else new InternetDialog(DeliveryBoyHome.this, new InternetDialog.onRetry() {
            @Override
            public void onReload() {
                settingData();
            }
        }).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        iniViews();
    }

    private void settingData() {
        //creating a string request to send request to the url
        String hp = getString(R.string.link) + "api/deliveryboy_order.php?deliverboy_id=" + DeliveryBoyId;
        Log.w(getClass().getName(), hp);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Response123", response);

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
                                    Log.d("CheckStatus", "" + jo_orderDetail.getString("status"));
                                    data.add(getSet);
                                }
                                adapter = new DeliveryAdapter(data, DeliveryBoyHome.this);
                                lv_orderList.setAdapter(adapter);

                            } else {
                                Toast.makeText(DeliveryBoyHome.this, obj.getString("order"), Toast.LENGTH_SHORT).show();
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
    private void sendPresence(final String status) {
        //creating a string request to send request to the url
        String hp = getString(R.string.link) +"api/deliveryboy_presence.php";
        Log.w(getClass().getName(), hp);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Response", response);
                        try {
                            JSONObject jo_main = new JSONObject(response);
                            JSONObject jo_data = jo_main.getJSONObject("data");
                            if (jo_data.getString("success").equals("1")) {
                                String isPresent = jo_data.getString("presence");
                                getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE).edit().putBoolean("isPresent", returnBool(isPresent)).apply();
                            } else {
                                Toast.makeText(DeliveryBoyHome.this, jo_data.getString("presence"), Toast.LENGTH_SHORT).show();
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
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("status", status); //Add the data you'd like to send to the server.
                MyData.put("deliverboy_id",getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE).getString("DeliveryUserId", ""));
                return MyData;
            }
        };
        stringRequest.setShouldCache(false);

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    private boolean returnBool(String status) {
        return Objects.equals(status, "yes");
    }

    private void showExitDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(DeliveryBoyHome.this, R.style.MyDialogTheme);
        builder1.setTitle(getString(R.string.Quit));
        builder1.setMessage(getString(R.string.statementquit));
        builder1.setCancelable(true);
        builder1.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finishAffinity();
            }
        });
        builder1.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    @Override
    public void onBackPressed() {
        showExitDialog();

    }
}
