package myour.myourforum.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AppCompatActivity;

import myour.myourforum.R;

public class LoadingScreen extends AppCompatActivity {
    private static AlertDialog mAlertDialog;

    public static AlertDialog showLoadingDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_loading_screen);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return alertDialog;
    }

    public static void show(Context context) {
        hide();
        mAlertDialog = showLoadingDialog(context);
    }

    public static void hide() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }
}
