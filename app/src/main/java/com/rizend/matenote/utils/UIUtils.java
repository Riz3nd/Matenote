package com.rizend.matenote.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.rizend.matenote.R;

public class UIUtils {
    private Context mContext;
    public UIUtils(Context context){
        this.mContext = context;
    }

    public Dialog dialogCreateNote(){
        Dialog mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.dialog_create_note);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.create();
        mDialog.show();
        return mDialog;
    }

    public Dialog dialogEditNote(){
        Dialog mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.dialog_create_note);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView titleDialog = mDialog.findViewById(R.id.dialogTitle);
        titleDialog.setText("Editar Nota");
        mDialog.create();
        mDialog.show();
        return mDialog;
    }


    public Dialog dialogUser(){
        Dialog mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.dialog_user);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.create();
        mDialog.show();
        return mDialog;
    }

}
