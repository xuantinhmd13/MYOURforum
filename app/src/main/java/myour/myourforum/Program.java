package myour.myourforum;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import myour.myourforum.model.Category;
import myour.myourforum.model.User;

public class Program {
    //
    public static final int REQUEST_CODE_REGISTER = 2;
    public static final int REQUEST_CODE_PICK_IMAGE = 3;
    public static final int BCRYPT_LOG_ROUND = 13;
    public static final String ERR_API_FAILURE = "Lỗi kết nối máy chủ!";
    public static final String ERR_API_SERVER = "Lỗi máy chủ!";
    public static final String[] listStorgePermission = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static User user;
    public static List<Category> categoryList;

    public static String getDateTimeNow() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(Calendar.getInstance().getTime());
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

    public static void requestPermission(Context context, String[] listPermission, PermissionListener permissionListener) {
        TedPermission.with(context)
                .setPermissionListener(permissionListener)
                .setPermissions(listPermission)
                .setDeniedTitle("Mở cài đặt quyền truy cập của ứng dụng")
                .setDeniedMessage("Cấp quyền truy cập cho ứng dụng bằng cách vào [Cài đặt] -> [Quyền truy cập ứng dụng] ")
                .check();
    }
}
