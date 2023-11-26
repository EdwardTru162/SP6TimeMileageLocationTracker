package com.mileage.tracker.Helper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.mileage.tracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class MiscHelper {
    Context context;
    public MiscHelper(Context context) {
        this.context = context;
    }
    public String currentDate(){
          return  new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());
       }
    public String currentTime(){
        return  new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }
    public Dialog openNetLoaderDialog() {
       Dialog dialogP=new Dialog(context);
        dialogP.setContentView(R.layout.dialog_loading);
        dialogP.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogP.setCancelable(false);
        dialogP.show();
        return dialogP;
    }
    public boolean isEmailValid(String email_string)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email_string == null)
            return false;
        return pat.matcher(email_string).matches();
    }

}
