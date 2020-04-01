package freaktemplate.kingburger.activity.deliveryBoySection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import freaktemplate.kingburger.R;
import freaktemplate.kingburger.activity.Home;
import freaktemplate.kingburger.observableLayer.CartFav;
import freaktemplate.kingburger.utils.Constants;
import freaktemplate.kingburger.utils.GPSTracker;
import freaktemplate.kingburger.utils.InternetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeliveryBoyOrderDetail extends AppCompatActivity {

    private ImageView iv_status;
    private TextView txt_orderAmount;
    private TextView txt_orderQuantity;
    private TextView txt_orderPaymentStyle;
    private TextView txt_orderDateTime;
    private TextView txt_name;
    private TextView txt_address;
    private TextView txt_contact;
    private TextView txt_header;
    private RecyclerView list_order;
    private Button btn_picked;
    private String orderNo, orderAmount, orderItem, orderDate, orderStatus, orderPayment, orderName, orderAddress, orderContact, orderLat, orderLon, responseStr, postSuccess, isPick;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_delivery_boy_order_detail );

        //get Intents
        gettingIntents();

        //initialing view
        initViews();

        //get data from server
        if (Home.isNetworkConnected( DeliveryBoyOrderDetail.this )) getDataFromServer();
        else new InternetDialog( DeliveryBoyOrderDetail.this, new InternetDialog.onRetry() {
            @Override
            public void onReload() {
                getDataFromServer();
            }
        } ).show();


    }

    private void getDataFromServer() {
        progressBar.setVisibility( View.VISIBLE );
        String url = getString( R.string.link ) + "api/order_item_details.php?order_id=" + orderNo;
        Log.e( getLocalClassName(), url );
        StringRequest stringRequest = new StringRequest( Request.Method.GET, url, new Response.Listener <String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility( View.GONE );
                Log.e( getLocalClassName(), response );
                try {
                    handleDataResponse( response );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText( DeliveryBoyOrderDetail.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
        RequestQueue requestQueue = Volley.newRequestQueue( this );
        stringRequest.setShouldCache( false );
        requestQueue.add( stringRequest );
    }

    private void handleDataResponse(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject( response );
        if (jsonObject.optString( "success" ).equals( "1" )) {
            JSONObject jo_order = jsonObject.optJSONObject( "Order" );
            String order_amount = jo_order.optString( "order_amount" );
            String payment = jo_order.optString( "payment" );
            String date = jo_order.optString( "date" );
            String address = jo_order.optString( "address" );
            String customer_name = jo_order.optString( "customer_name" );
            String phone = jo_order.optString( "phone" );
            String latitude = jo_order.optString( "latitude" );
            String longitude = jo_order.optString( "longitude" );
            JSONArray jsonArray = jo_order.optJSONArray( "item_name" );
            String sizeOfItem = String.valueOf( jsonArray.length() );
            updateMainData( order_amount, payment, date, address, customer_name, phone, sizeOfItem, latitude, longitude );

            ArrayList <CartFav> item = new ArrayList <>();
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    CartFav cartItem = new CartFav();
                    JSONObject jo_item = jsonArray.getJSONObject( i );
                    cartItem.setItemName( jo_item.optString( "name" ) );
                    cartItem.setItemDescription( jo_item.optString( "ingredients_id" ) );
                    cartItem.setItemQuantity( Integer.parseInt( jo_item.optString( "qty" ) ) );
                    cartItem.setItemPrice( Double.parseDouble( jo_item.optString( "amount" ) ) );
                    item.add( cartItem );
                }
                updateListItem( item );
            }
        } else {
            Toast.makeText( this, "Please try Later!", Toast.LENGTH_SHORT ).show();
            onBackPressed();
        }
    }

    private void updateMainData(String order_amount, String payment, String date, String address, String customer_name, String phone, String sizeOfItem, String latitude, String longitude) {
        orderAmount = order_amount;
        orderItem = sizeOfItem;
        orderPayment = payment;
        orderContact = phone;
        orderName = customer_name;
        orderDate = date;
        orderAddress = address;
        orderLat = latitude;
        orderLon = longitude;


        txt_header.setTypeface( Home.tf_main_bold );
        txt_header.setText( getString( R.string.orderno ) + " " + orderNo );
        txt_orderAmount.setText( getString( R.string.order_amount ) + " " + order_amount );
        txt_orderQuantity.setText( getString( R.string.item ) + " " + orderItem );

        // this is the text we'll be operating on
        SpannableString text = new SpannableString( getString( R.string.payment ) + orderPayment );

        // make "Lorem" (characters 0 to 5) red
        int temp = getResources().getColor( R.color.colorGreen );
        ForegroundColorSpan fcs = new ForegroundColorSpan( temp );
        text.setSpan( fcs, getString( R.string.payment ).length(), text.length(), 0 );
        txt_orderPaymentStyle.setText( text );
        txt_orderDateTime.setText( orderDate );
        txt_name.setText( orderName );
        txt_address.setText( orderAddress );
        txt_contact.setText( orderContact );

    }

    private void updateListItem(ArrayList <CartFav> item) {
        CustomDeliveryBoyAdapter customAdapter = new CustomDeliveryBoyAdapter( item );
        list_order.setAdapter( customAdapter );
        list_order.setLayoutManager( new LinearLayoutManager( DeliveryBoyOrderDetail.this ) );
    }

    private void gettingIntents() {
        Intent i = getIntent();
        orderNo = i.getStringExtra( "OrderNo" );
        orderStatus = i.getStringExtra( "isComplete" );
        Log.e( "Order Status", orderStatus );
    }

    private void initViews() {
        ImageView iv_background = findViewById( R.id.iv_background );
        Glide.with( this ).load( R.drawable.ezgif ).into( iv_background );
        iv_status = findViewById( R.id.iv_status );
        txt_header = findViewById( R.id.tv_itemName );
        txt_orderAmount = findViewById( R.id.txt_orderAmount );
        txt_orderQuantity = findViewById( R.id.txt_orderQuantity );
        txt_orderPaymentStyle = findViewById( R.id.txt_orderPaymentStyle );
        txt_orderDateTime = findViewById( R.id.txt_orderDateTime );
        txt_name = findViewById( R.id.txt_name );
        txt_address = findViewById( R.id.txt_address );
        txt_contact = findViewById( R.id.txt_contact );
        list_order = findViewById( R.id.list_order );
        progressBar = findViewById( R.id.progressBar );
        btn_picked = findViewById( R.id.btn_picked );

        switch (orderStatus) {
            case Constants.orderPrepare:
                iv_status.setImageDrawable( getResources().getDrawable( R.drawable.img_orderprocess ) );
                btn_picked.setText( R.string.picked );
                btn_picked.setBackgroundColor( getResources().getColor( R.color.colorBlue ) );
                break;
            case Constants.orderDispatch:
                iv_status.setImageDrawable( getResources().getDrawable( R.drawable.img_orderprocess ) );
                btn_picked.setText( R.string.complete );
                btn_picked.setBackgroundColor( getResources().getColor( R.color.colorGreen ) );
                break;
            case Constants.orderDelivered:
                iv_status.setImageDrawable( getResources().getDrawable( R.drawable.img_ordercomplete ) );
                btn_picked.setVisibility( View.GONE );
                break;
        }


        Button btn_call = findViewById( R.id.btn_call );
        btn_call.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + orderContact;
                Intent i = new Intent( Intent.ACTION_DIAL );
                i.setData( Uri.parse( uri ) );
                startActivity( i );
            }
        } );

        Button btn_map = findViewById( R.id.btn_map );
        btn_map.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gettingGPSLocation();

            }
        } );

        btn_picked.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (orderStatus) {
                    case Constants.orderPrepare:
                        isPick = "pick";
                        if (Home.isNetworkConnected( DeliveryBoyOrderDetail.this ))
                            postData( isPick );
                        else
                            new InternetDialog( DeliveryBoyOrderDetail.this, new InternetDialog.onRetry() {
                                @Override
                                public void onReload() {
                                    postData( isPick );
                                }
                            } ).show();
                        break;
                    case Constants.orderDispatch:
                        isPick = "complete";
                        if (Home.isNetworkConnected( DeliveryBoyOrderDetail.this ))
                            postData( isPick );
                        else
                            new InternetDialog( DeliveryBoyOrderDetail.this, new InternetDialog.onRetry() {
                                @Override
                                public void onReload() {
                                    postData( isPick );
                                }
                            } ).show();
                        break;
                    case Constants.orderDelivered:
                        btn_picked.setVisibility( View.GONE );
                        break;
                }

            }
        } );

    }

    private void gettingGPSLocation() {
        GPSTracker gps = new GPSTracker();
        gps.init( DeliveryBoyOrderDetail.this );
        // check if GPS enabled
        if (gps.canGetLocation()) {
            try {
                Double sLat = gps.getLatitude();
                Double sLong = gps.getLongitude();
                Log.w( "Current Location", "Lat: " + sLat + "Long: " + sLong + "URL:::" + orderLat + "::::" + orderLon );
                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "http://maps.google.com/maps?saddr=" + sLat + "," + sLong + "&daddr=" + orderLat + "," + orderLon ) );
                intent.setClassName( "com.google.android.apps.maps", "com.google.android.maps.MapsActivity" );
                startActivity( intent );
            } catch (NullPointerException | NumberFormatException e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        } else {
            gps.showSettingsAlert();
        }
    }


    private void postData(String isPick) {

        String url = null;
        if (isPick.equals( "pick" )) {
            url = getString( R.string.link ) + "api/order_pick.php?order_id=" + orderNo;
        } else if (isPick.equals( "complete" )) {
            url = getString( R.string.link ) + "api/order_complete.php?order_id=" + orderNo;
        }
        if (url != null) {
            Log.e( getLocalClassName(), url );
            StringRequest stringRequest = new StringRequest( Request.Method.GET, url, new Response.Listener <String>() {
                @Override
                public void onResponse(String response) {
                    Log.e( getLocalClassName(), response );
                    try {
                        handleResponse( response );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText( DeliveryBoyOrderDetail.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                }
            } );
            stringRequest.setShouldCache( false );
            RequestQueue requestQueue = Volley.newRequestQueue( DeliveryBoyOrderDetail.this );
            requestQueue.add( stringRequest );

        }
    }

    private void handleResponse(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject( response );
        if (jsonObject.getString( "success" ).equals( "1" )) {
            switch (orderStatus) {
                case Constants.orderPrepare: {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder( DeliveryBoyOrderDetail.this, R.style.MyDialogTheme );
                    builder1.setMessage( R.string.order_is_picked );
                    builder1.setCancelable( true );
                    builder1.setPositiveButton( R.string.okk, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            btn_picked.setText( R.string.complete );
                            btn_picked.setBackgroundColor( Color.BLACK );
                            onBackPressed();
                        }
                    } );
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    break;
                }
                case Constants.orderDispatch: {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder( DeliveryBoyOrderDetail.this, R.style.MyDialogTheme );
                    builder1.setMessage( R.string.order_is_completed );
                    builder1.setCancelable( true );
                    builder1.setPositiveButton( R.string.okk, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            btn_picked.setVisibility( View.GONE );
                            onBackPressed();
                        }
                    } );
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    break;
                }
                case Constants.orderDelivered:
                    btn_picked.setVisibility( View.GONE );
                    break;
            }
        } else {
            Toast.makeText( DeliveryBoyOrderDetail.this, jsonObject.getString( "error" ), Toast.LENGTH_SHORT ).show();
        }

    }


}

class CustomDeliveryBoyAdapter extends RecyclerView.Adapter <CustomDeliveryBoyAdapter.ViewHolderCompleteOrder> {
    private final List <CartFav> dataList;
    private Context context;

    public CustomDeliveryBoyAdapter(List <CartFav> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public CustomDeliveryBoyAdapter.ViewHolderCompleteOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from( parent.getContext() ).inflate( R.layout.cell_custom_order, parent, false );
        context = parent.getContext();
        return new ViewHolderCompleteOrder( itemView );
    }

    @Override
    public void onBindViewHolder(@NonNull CustomDeliveryBoyAdapter.ViewHolderCompleteOrder holder, int position) {
        holder.iv_next.setVisibility( View.GONE );
        holder.tv_item_name.setTypeface( Home.tf_main_bold );
        holder.tv_item_price.setTypeface( Home.tf_main_bold );
        holder.tv_subItem_list.setTypeface( Home.tf_main_regular );
        holder.tv_item_name.setText( String.format( Locale.ENGLISH, "%s %s %d", dataList.get( position ).getItemName(), context.getString( R.string.dash ), dataList.get( position ).getItemQuantity() ) );
        holder.tv_item_price.setText( String.format( Locale.ENGLISH, "%s %.2f", context.getString( R.string.currency ), dataList.get( position ).getItemPrice() ) );
        if (!dataList.get( position ).getItemDescription().isEmpty())
            holder.tv_subItem_list.setText( dataList.get( position ).getItemDescription() );
        else holder.tv_subItem_list.setText( context.getString( R.string.no_ingredients ) );
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }


    class ViewHolderCompleteOrder extends RecyclerView.ViewHolder {
        final TextView tv_item_name;
        final TextView tv_item_price;
        final TextView tv_subItem_list;
        final ImageView iv_next;

        ViewHolderCompleteOrder(View itemView) {
            super( itemView );
            tv_item_name = itemView.findViewById( R.id.tv_item_name );
            tv_item_price = itemView.findViewById( R.id.tv_item_price );
            tv_subItem_list = itemView.findViewById( R.id.tv_subItem_list );
            iv_next = itemView.findViewById( R.id.iv_next );
        }
    }
}
