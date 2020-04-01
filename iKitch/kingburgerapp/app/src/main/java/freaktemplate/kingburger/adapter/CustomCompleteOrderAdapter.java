package freaktemplate.kingburger.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import freaktemplate.kingburger.R;
import freaktemplate.kingburger.activity.CompleteOrder;
import freaktemplate.kingburger.activity.DetailPage;
import freaktemplate.kingburger.activity.Home;
import freaktemplate.kingburger.observableLayer.AppDatabase;
import freaktemplate.kingburger.observableLayer.Cart;
import freaktemplate.kingburger.observableLayer.CartItemTopping;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomCompleteOrderAdapter extends RecyclerView.Adapter<CustomCompleteOrderAdapter.ViewHolderCompleteOrder> {
    private final Context context;
    private final List<Cart> dataList;
    private final AppDatabase appDatabase;
    String ima="Menuitem_1583718401.png";

    public CustomCompleteOrderAdapter(Context context, List<Cart> dataList) {
        this.context = context;
        this.dataList = dataList;
        appDatabase = AppDatabase.getAppDatabase(context);

    }
    private String getItemImage(int categoryId, final String itemId, int position) {
        //creating a string request to send request to the url
        String hp = context.getString(R.string.link) + "api/sub_category.php?category=" + categoryId;
        hp = hp.replace(" ", "%20");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Responsesjmflkdsf'gf", response);

                        try {
                            JSONObject jo_object = new JSONObject(response);
                            JSONArray ja_ingredient = jo_object.optJSONArray("sub_category");

                            for (int i=0;i<ja_ingredient.length();i++){
                                System.out.println("ID: "+ ja_ingredient.getJSONObject(i).getString("id") +"idParam: "+itemId);
                                if(ja_ingredient.getJSONObject(i).getString("id").equals(itemId)){
                                    System.out.println("TROUVEEEEEHIEHEEUEIHEHEHIEHUEIHEIE");
                                   ima = ja_ingredient.getJSONObject(i).getString("menu_image");
                                }
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
                        }
                    }
                });
        stringRequest.setShouldCache(false);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //adding the string request to request queue

        requestQueue.add(stringRequest);

        return ima;

    }

    @NonNull
    @Override
    public ViewHolderCompleteOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_complete_order_list, parent, false);
        return new ViewHolderCompleteOrder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCompleteOrder holder, int position) {
        holder.tv_item_name.setTypeface(Home.tf_main_bold);
        holder.tv_item_price.setTypeface(Home.tf_main_bold);
        holder.tv_subItem_list.setTypeface(Home.tf_main_regular);
        holder.tv_item_name.setText(String.format(Locale.getDefault(),"%s %s %d", dataList.get(position).getItemName(),context.getString(R.string.dash), dataList.get(position).getItemQuantity()));
holder.getItemId();



        double priceWithQuantity = dataList.get(position).getItemPriceWithTopping()*dataList.get(position).getItemQuantity();
        holder.tv_item_price.setText(String.format(Locale.ENGLISH, "%s %.2f",context.getString(R.string.currency), priceWithQuantity));

        holder.iv_delete.setTag(dataList.get(position).getId());

        List<CartItemTopping> cartItemToppings = appDatabase.cartDao().getTopping(dataList.get(position).getId());
        StringBuilder stringBuilder = new StringBuilder();
        if (cartItemToppings.size() > 0) {
            for (CartItemTopping data : cartItemToppings) {
                stringBuilder.append(data.getToppingName()).append(", ");
            }
            holder.tv_subItem_list.setText(stringBuilder.toString().substring(0, stringBuilder.length() - 2));
        } else {
            holder.tv_subItem_list.setText(R.string.no_ingredients);
        }
        String imageUrl = "https://managemyresto.fr/test/images/menu_item_icon/" + getItemImage(dataList.get(position).getItemCategoryId(),dataList.get(position).getItemId()+"",position);
        System.out.println("voici image url: "+imageUrl);
        Glide.with(context).load(imageUrl).into(holder.image_item);
    }

    @Override
    public int getItemCount() {
        if (dataList == null)
            return 0;
        return dataList.size();
    }

    public double getTotalPrice() {
        Double tPrice = 0.00;
        if (dataList.size() > 0) {
            for (Cart c : dataList) {
                tPrice = tPrice + (c.getItemPriceWithTopping()*c.getItemQuantity());
            }
        }
        return tPrice;
    }
    public List<Cart> getListe(){
        return dataList;
    }

    public String getJsonOrderFormat() {
        JSONObject jsonObject = new JSONObject();
        JSONArray ja_order = new JSONArray();
        JSONObject jo_item_detail;
        JSONArray ja_item_ingredient = null;
        JSONObject jo_item_ingredient;


        if (dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                jo_item_detail = new JSONObject();
                List<CartItemTopping> cartItemToppings = appDatabase.cartDao().getTopping(dataList.get(i).getId());
                if (cartItemToppings.size() > 0) {
                    ja_item_ingredient = new JSONArray();
                    for (int y = 0; y < cartItemToppings.size(); y++) {
                        try {
                            jo_item_ingredient = new JSONObject();
                            jo_item_ingredient.put("id", String.valueOf(cartItemToppings.get(y).getToppingId()));
                            jo_item_ingredient.put("category", String.valueOf(dataList.get(i).getItemCategoryId()));
                            jo_item_ingredient.put("item_name", cartItemToppings.get(y).getToppingName());
                            jo_item_ingredient.put("type", String.valueOf(cartItemToppings.get(y).getType()));
                            jo_item_ingredient.put("price", String.valueOf(cartItemToppings.get(y).getToppingPrice()));
                            jo_item_ingredient.put("menu_id", String.valueOf(cartItemToppings.get(y).getItemId()));
                            ja_item_ingredient.put(jo_item_ingredient);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    jo_item_detail.put("ItemId", String.valueOf(dataList.get(i).getItemId()));
                    jo_item_detail.put("ItemName", String.valueOf(dataList.get(i).getItemName()));
                    jo_item_detail.put("ItemQty", String.valueOf(dataList.get(i).getItemQuantity()));
                    jo_item_detail.put("ItemAmt", String.valueOf(dataList.get(i).getItemPriceWithTopping()));

                    //calculate item price with topping and its quantity
                    double tPrice = dataList.get(i).getItemPriceWithTopping()*dataList.get(i).getItemQuantity();
                    jo_item_detail.put("ItemTotalPrice",String.valueOf(tPrice));

                    if (ja_item_ingredient == null) {
                        jo_item_detail.putOpt("Ingredients",new JSONArray());
                    } else
                        jo_item_detail.putOpt("Ingredients", ja_item_ingredient);
                        ja_item_ingredient=null;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ja_order.put(jo_item_detail);
            }
        }
        try {
            jsonObject.put("Order", ja_order);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    class ViewHolderCompleteOrder extends RecyclerView.ViewHolder {
        final TextView tv_item_name;
        final TextView tv_item_price;
        final TextView tv_subItem_list;
        final ImageView image_item;
        final ImageView iv_delete;

        ViewHolderCompleteOrder(View itemView) {
            super(itemView);
            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            tv_item_price = itemView.findViewById(R.id.tv_item_price);
            tv_subItem_list = itemView.findViewById(R.id.tv_subItem_list);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            context);
                    alert.setTitle(R.string.alert);
                    alert.setIcon(context.getResources().getDrawable(R.drawable.ic_deletecart));
                    alert.setMessage(R.string.delete_item_cart);
                    alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do your work here
                            appDatabase.cartDao().deleteCartItem((Integer) v.getTag());
                            appDatabase.cartDao().deleteCartToppingItem((Integer) v.getTag());
                            dialog.dismiss();
                        }
                    });
                    alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            });
            image_item = itemView.findViewById(R.id.image_item);
        }
    }


}
