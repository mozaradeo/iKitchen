package freaktemplate.kingburger.activity.deliveryBoySection;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import freaktemplate.kingburger.R;
import freaktemplate.kingburger.activity.Home;
import freaktemplate.kingburger.utils.LanguageSelectore;

public class DeliveryBoyProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageSelectore.setLanguageIfAR( DeliveryBoyProfile.this );

        setContentView(R.layout.activity_delivery_boy_profile);


        initViews();


    }

    private void initViews() {
        ImageView iv_background = findViewById(R.id.iv_background);
        Glide.with(DeliveryBoyProfile.this).load(R.drawable.ezgif).into(iv_background);
        TextView txt_header = findViewById(R.id.txt_title);
        txt_header.setTypeface(Home.tf_main_bold);
        TextView txt_name_tittle = findViewById(R.id.txt_name_tittle);
        txt_name_tittle.setTypeface(Home.tf_main_regular);
        TextView txt_name = findViewById(R.id.txt_name);
        txt_name.setTypeface(Home.tf_main_regular);
        TextView txt_contact_tittle = findViewById(R.id.txt_contact_tittle);
        txt_contact_tittle.setTypeface(Home.tf_main_regular);
        TextView txt_contact = findViewById(R.id.txt_contact);
        txt_contact.setTypeface(Home.tf_main_regular);
        TextView txt_email_tittle = findViewById(R.id.txt_email_tittle);
        txt_email_tittle.setTypeface(Home.tf_main_regular);
        TextView txt_email = findViewById(R.id.txt_email);
        txt_email.setTypeface(Home.tf_main_regular);
        TextView txt_vehicle_no_tittle = findViewById(R.id.txt_vehicle_no_tittle);
        txt_vehicle_no_tittle.setTypeface(Home.tf_main_regular);
        TextView txt_vehicle_no = findViewById(R.id.txt_vehicle_no);
        txt_vehicle_no.setTypeface(Home.tf_main_regular);
        TextView txt_vehicle_type_tittle = findViewById(R.id.txt_vehicle_type_tittle);
        txt_vehicle_type_tittle.setTypeface(Home.tf_main_regular);
        TextView txt_vehicle_type = findViewById(R.id.txt_vehicle_type);
        txt_vehicle_type.setTypeface(Home.tf_main_regular);



        SharedPreferences sp = getSharedPreferences(getString(R.string.shared_pref_name),MODE_PRIVATE);
        txt_name.setText(sp.getString("DeliveryUserName", ""));
        txt_contact.setText(sp.getString("DeliveryUserPhone", ""));
        txt_email.setText(sp.getString("DeliveryUserEmail", ""));
        txt_vehicle_no.setText(sp.getString("DeliveryUserVNo", ""));
        txt_vehicle_type.setText(sp.getString("DeliveryUserVType", ""));
    }
}
