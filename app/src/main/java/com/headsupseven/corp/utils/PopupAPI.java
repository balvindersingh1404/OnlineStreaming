package com.headsupseven.corp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


public class PopupAPI {

    public interface PopupOkCallback {
        void onOkPress();
    }

    public static void make(final Context c, final String title,
                     final String message) {
        make(c, title, message, null);
    }

    public static void make(final Context c, final String title,
                     final String message,final PopupOkCallback callback) {
        final AlertDialog.Builder aBuilder = new AlertDialog.Builder(c);
        aBuilder.setTitle(title);
        aBuilder.setMessage(message);
        aBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                if(callback != null)
                    callback.onOkPress();
                dialog.dismiss();
            }

        });
        aBuilder.show();
    }
    public static void showtostcustomview(Context context, int layout) {
        LayoutInflater l_Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = l_Inflater.inflate(layout, null, false);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(item);
        toast.show();
    }
    public static void showToast(Context context, String text) {
        Toast toast = Toast.makeText(context,text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
