package freaktemplate.kingburger.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import freaktemplate.kingburger.R;

import static android.content.Context.MODE_PRIVATE;


public  class AlreadyRegisterDialog extends Dialog {
    private String phoneNumber = "";
    private ProgressDialog dialog;


    public AlreadyRegisterDialog(@NonNull final Context context, final int themeResId) {
        super( context,themeResId );
        dialog = new ProgressDialog(context);
        setContentView(R.layout.activity_already_register_dialog);

        if (null != getWindow())
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView txt_register = findViewById(R.id.txt_register);
        TextView edt_mobileNumber = findViewById(R.id.edt_mobileNumber);
        TextView txt_already_register = findViewById(R.id.txt_already_register);
        Button btn_cancel= findViewById( R.id.buttonCancel );
        Button btn_close= findViewById( R.id.btn_close );


         edt_mobileNumber.setText( context.getSharedPreferences(context.getString(R.string.shared_pref_name),MODE_PRIVATE).getString("registeredNumber", ""));

        btn_close.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences mPreferences= context.getSharedPreferences( context.getString(  R.string.shared_pref_name),MODE_PRIVATE );
                SharedPreferences.Editor editor=mPreferences.edit();
                editor.putInt( "userId",-1 );
                editor.apply();
                dismiss();
                Toast.makeText(context, R.string.reg_logout, Toast.LENGTH_SHORT).show();

            }
        } );

        btn_cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        } );
    }
}
