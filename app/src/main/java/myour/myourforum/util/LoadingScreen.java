package myour.myourforum.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.util.Objects;

import myour.myourforum.R;

public class LoadingScreen {
    private static AlertDialog dialogLoading;

    public static void show(Context context) {
        hide();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.dialog_loading_screen);
        builder.setCancelable(false);

        dialogLoading = builder.create();
        Objects.requireNonNull(dialogLoading.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogLoading.show();
    }

    public static void hide() {
        if (dialogLoading != null && dialogLoading.isShowing()) dialogLoading.dismiss();
    }
}
