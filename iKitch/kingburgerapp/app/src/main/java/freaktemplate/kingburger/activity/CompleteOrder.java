package freaktemplate.kingburger.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import freaktemplate.kingburger.R;
import freaktemplate.kingburger.adapter.CustomCompleteOrderAdapter;
import freaktemplate.kingburger.observableLayer.Cart;
import freaktemplate.kingburger.observableLayer.CartListViewModel;
import freaktemplate.kingburger.utils.LanguageSelectore;
import freaktemplate.kingburger.utils.RegisterBeforeOrder;

public class CompleteOrder extends AppCompatActivity {

    private TextView tv_totalAmount;
    private RecyclerView rv_listOrderItems;
    private List <Cart> data;
    private CustomCompleteOrderAdapter customCompleteOrderAdapter;
    private CartListViewModel cartListViewModel;
    private Boolean isCartEmpty = true;
    private TextView tv_noData;
    private static Double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        LanguageSelectore.setLanguageIfAR( CompleteOrder.this );

        setContentView( R.layout.activity_complete_order );

        //assign view model of database for the activity
        cartListViewModel = ViewModelProviders.of( this ).get( CartListViewModel.class );

        //initializing Views
        initViews();


    }



    private void initViews() {
        //setting Background
        ImageView iv_background = findViewById( R.id.iv_background );
        Glide.with( this ).load( R.drawable.ezgif ).into( iv_background );

        tv_noData = findViewById( R.id.tv_noData );
        tv_noData.setTypeface( Home.tf_main_bold );

        tv_totalAmount = findViewById( R.id.tv_totalAmount );
        tv_totalAmount.setTypeface( Home.tf_main_bold );

        rv_listOrderItems = findViewById( R.id.rv_listOrderItems );
        TextView tv_itemName = findViewById( R.id.tv_itemName );

        tv_itemName.setTypeface( Home.tf_main_bold );

        ImageView imageView= findViewById(R.id.image_item);
        //String imageUrl = getString(R.string.link) + "images/menu_item_icon/" + getItemImage();
        //Glide.with(CompleteOrder.this).load(imageUrl).into(imageView);

        cartListViewModel.getCartList().observe( this, new Observer <List <Cart>>() {
            @Override
            public void onChanged(@Nullable List <Cart> carts) {
                data = carts;
                updateUI( data );
            }
        } );


        findViewById( R.id.rl_CompleteOrder ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCartEmpty) {
                    Toast.makeText( CompleteOrder.this, getString( R.string.cart_is_empty ), Toast.LENGTH_SHORT ).show();
                } else {
                    Log.e( "user_id", String.valueOf( getSharedPreferences( getString( R.string.shared_pref_name ), MODE_PRIVATE ).getInt( "userId", -1 ) ) );
                    if ((getSharedPreferences( getString( R.string.shared_pref_name ), MODE_PRIVATE ).getInt( "userId", -1 )) == -1) {
                        RegisterBeforeOrder beforeOrder = new RegisterBeforeOrder( CompleteOrder.this, android.R.style.Theme_NoTitleBar_Fullscreen );
                        beforeOrder.setOnCompleteListener( new RegisterBeforeOrder.onSuccess() {
                            @Override
                            public void onUserId() {
                                Intent intent = new Intent( CompleteOrder.this, PlaceOrderInfo.class );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
                                intent.putExtra( "CartDetail", customCompleteOrderAdapter.getJsonOrderFormat() );
                                intent.putExtra( "CartTotalPrice", String.valueOf( totalPrice ) );
                                startActivity( intent );
                            }
                        } );
                        beforeOrder.show();

                    } else {
                        Intent intent = new Intent( CompleteOrder.this, PlaceOrderInfo.class );
                        intent.addFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
                        intent.putExtra( "CartDetail", customCompleteOrderAdapter.getJsonOrderFormat() );
                        intent.putExtra( "CartTotalPrice", String.valueOf( totalPrice ) );
                        startActivity( intent );
                    }


                }
            }
        } );
    }

    private void updateUI(List <Cart> data) {
        isCartEmpty = false;
        customCompleteOrderAdapter = new CustomCompleteOrderAdapter( CompleteOrder.this, data );
        rv_listOrderItems.setAdapter( customCompleteOrderAdapter );
        totalPrice = customCompleteOrderAdapter.getTotalPrice();
        tv_totalAmount.setText( String.format( Locale.ENGLISH, "%s%s %.2f", getString( R.string.total_amount ), getString( R.string.currency ), totalPrice ) );
        if (data.size() > 0) {
            tv_noData.setVisibility( View.GONE );
        } else {
            isCartEmpty = true;
            tv_noData.setVisibility( View.VISIBLE );
        }
    }
}
