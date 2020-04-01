package freaktemplate.kingburger.observableLayer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import freaktemplate.kingburger.R;
import freaktemplate.kingburger.getSet.MenuGetSet;
import freaktemplate.kingburger.getSet.SubMenuGetSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private MutableLiveData<List<MenuGetSet>> menuGetSetMutableLiveData;
    private MutableLiveData<List<SubMenuGetSet>> subMenuGetSetMutableLiveData;


    public HomeViewModel(@NonNull Application application) {
        super(application);

        if (menuGetSetMutableLiveData == null) {
            menuGetSetMutableLiveData = new MutableLiveData<>();
            LoadMenuList();
        }

        if (subMenuGetSetMutableLiveData == null) {
            subMenuGetSetMutableLiveData = new MutableLiveData<>();
          //  LoadSubMenu(application.getSharedPreferences(application.getString(R.string.app_name), Context.MODE_PRIVATE).getString("subCategoryId", "77"));
        }

    }


    public MutableLiveData<List<SubMenuGetSet>> getSubMenuList() {
        return subMenuGetSetMutableLiveData;
    }

    public MutableLiveData<List<MenuGetSet>> getMenuList() {
        return menuGetSetMutableLiveData;
    }



    /*
     * =============================================================
     * ================ Getting Data From Server ===================
     * =============================================================
     */


    private void LoadMenuList() {
        //creating a string request to send request to the url

        String hp = getApplication().getString(R.string.link) + "api/menu_category.php";
        hp = hp.replace(" ", "%20");
        Log.e("url", hp);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Response", response);
                        try {
                            JSONObject jo_main = new JSONObject(response);
                            JSONArray ja_menuData = jo_main.optJSONArray("menu_category");

                            List<MenuGetSet> tempArray = new ArrayList<>();
                            MenuGetSet temp;

                            for (int i = 0; i < ja_menuData.length(); i++) {
                                temp = new MenuGetSet();
                                JSONObject jo_menuData = ja_menuData.optJSONObject(i);
                                temp.setCategoryName(jo_menuData.optString("cat_name"));
                                temp.setCategoryImage(jo_menuData.optString("cat_icon"));
                                temp.setMenuId(jo_menuData.optString("id"));
                                tempArray.add(temp);
                            }
                            menuGetSetMutableLiveData.postValue(tempArray);

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
                            Toast.makeText(getApplication(),getApplication().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    public void LoadSubMenu(String subCategoryId) {
        System.out.println("paser dans loadSubMenu");
        //creating a string request to send request to the url
        String hp = getApplication().getString(R.string.link) + "api/sub_category.php?category="+subCategoryId;
        hp = hp.replace(" ", "%20");
        Log.e("url", hp);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Response", response);
                        try {
                            JSONObject jo_main = new JSONObject(response);
                            JSONArray ja_menuData = jo_main.optJSONArray("sub_category");
                            List<SubMenuGetSet> tempArray = new ArrayList<>();
                            SubMenuGetSet temp;
                            for (int i = 0; i < ja_menuData.length(); i++) {
                                temp = new SubMenuGetSet();
                                JSONObject jo_menuData = ja_menuData.optJSONObject(i);
                                temp.setCatId(jo_menuData.optString("category"));
                                temp.setItemDescription(jo_menuData.optString("description"));
                                temp.setItemId(jo_menuData.optString("id"));
                                temp.setItemImage(jo_menuData.optString("menu_image"));
                                temp.setItemName(jo_menuData.optString("menu_name"));
                                temp.setItemPrice(jo_menuData.optString("price"));
                                tempArray.add(temp);
                            }

                            subMenuGetSetMutableLiveData.postValue(tempArray);

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
                            Toast.makeText(getApplication(), getApplication().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}
