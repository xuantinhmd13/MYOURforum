package myour.myourforum;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import myour.myourforum.api.RESTfulAPIConfig;
import myour.myourforum.api.RESTfulAPIRequest;
import myour.myourforum.model.Category;
import myour.myourforum.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Program {
    public static User user;
    public static List<Category> categoryList;
    //
    public static RESTfulAPIRequest request = RESTfulAPIConfig.getServer();

    public static String ERR_API_FAILURE = "Lỗi kết nối máy chủ!";
    public static String ERR_API_SERVER = "Lỗi máy chủ!";

    public static int REQUEST_CODE_LOGIN = 2;

    public static String getDateTimeNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeNow = dateFormat.format(Calendar.getInstance().getTime());
        return timeNow;
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
            return networkCapabilities != null
                    && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }
}
