package freaktemplate.kingburger.activity.deliveryBoySection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import freaktemplate.kingburger.utils.InternetDialog;
import freaktemplate.kingburger.utils.LanguageSelectore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginAsDelivery extends AppCompatActivity {

    private TextInputEditText edit_email;
    private TextInputEditText edit_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageSelectore.setLanguageIfAR( LoginAsDelivery.this );

        setContentView(R.layout.activity_login_as_delivery);

        //initializing views
        initViews();

    }

    private void initViews() {
        edit_email = findViewById(R.id.edit_email);
        edit_pwd = findViewById(R.id.edit_pwd);

        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setTypeface(Home.tf_main_bold);

        ImageView iv_background = findViewById(R.id.iv_background);
        Glide.with(LoginAsDelivery.this).load(R.drawable.ezgif).into(iv_background);

    }


    public void toDeliveryBoyHome(View view) {
        if (emailValidator(edit_email)) {
            if (isValid(edit_pwd)) {
                if (Home.isNetworkConnected(LoginAsDelivery.this))
                    getLoginAfterValidation(edit_email.getText().toString(), edit_pwd.getText().toString());
                else new InternetDialog(LoginAsDelivery.this, new InternetDialog.onRetry() {
                    @Override
                    public void onReload() {
                        getLoginAfterValidation(edit_email.getText().toString(), edit_pwd.getText().toString());
                    }
                }).show();
            } else {
                edit_pwd.setError("Enter Password!");
            }
        } else {
            edit_email.setError("Enter Valid Email Address!");
        }

    }


    private void getLoginAfterValidation(final String email, final String password) {
        //creating a string request to send request to the url
        String hp = getApplication().getString(R.string.link) + "api/deliveryboy_login.php";
        hp = hp.replace(" ", "%20");
        Log.w(getClass().getName(), hp);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject jo_data = obj.getJSONObject("data");
                            if (jo_data.getString("success").equals("1")) {
                                JSONArray ja_login = jo_data.getJSONArray("login");
                                JSONObject jo_detail = ja_login.getJSONObject(0);
                                String id = jo_detail.getString("id");
                                String name = jo_detail.getString("name");
                                String phone = jo_detail.getString("mobile_no");
                                String email = jo_detail.getString("email");
                                String vehicle_no = jo_detail.getString("vehicle_no");
                                String vehicle_type = jo_detail.getString("vehicle_type");
                                saveToSharedPref(id, name, phone, email, vehicle_no, vehicle_type);
                            } else {

                                Toast.makeText(LoginAsDelivery.this, jo_data.optString("login"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplication(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("email",email);
                data.put("password",password);
                String token_id = getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE).getString("token", "");
                data.put("token",token_id);
                data.put("type","android");
                return data;
            }
        };
        stringRequest.setShouldCache(false);
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void saveToSharedPref(String id, String name, String phone, String email, String vehicle_no, String vehicle_type) {
        SharedPreferences.Editor edit = getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE).edit();
        edit.putBoolean("isDeliverAccountActive", true);
        edit.putString("DeliveryUserId", id);
        edit.putString("DeliveryUserName", name);
        edit.putString("DeliveryUserPhone", phone);
        edit.putString("DeliveryUserEmail", email);
        edit.putString("DeliveryUserVNo", vehicle_no);
        edit.putString("DeliveryUserVType", vehicle_type);
        edit.apply();
        Intent i = new Intent(LoginAsDelivery.this, DeliveryBoyHome.class);
        startActivity(i);
        finish();
    }

    private boolean isValid(TextInputEditText editText) {
        return editText.length() != 0;
    }

    private boolean emailValidator(TextInputEditText editText) {
        String email = editText.getText().toString();
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
