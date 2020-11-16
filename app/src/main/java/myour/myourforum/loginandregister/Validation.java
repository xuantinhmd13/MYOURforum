package myour.myourforum.loginandregister;

import android.util.Patterns;

public class Validation {
    public static String getEmailErr(String email) {
        if (email.isEmpty()) return "Không được để trống!";
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Không đúng định dạng";
        return null;
    }

    public static String getPasswordErr(String password, boolean isRegister) {
        if (isRegister) {
            if (password.length() < 8) return "Độ dài mật khẩu >= 8 ký tự!";
        }
        if (password.isEmpty()) return "Không được để trống!";
        return null;
    }

    public static String getUserNameErr(String userName) {
        //TODO:
        if (userName.isEmpty()) return "Không được để trống!";
        return null;
    }
}
