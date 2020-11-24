package myour.myourforum.loginandregister;

import android.util.Patterns;

public class Validation {
    private static final String warnNoEmpty = "Không được để trống!";

    public static String getEmailErr(String email) {
        if (email.isEmpty()) return warnNoEmpty;
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "Không đúng định dạng\n VD: example@mail.com";
        return null;
    }

    public static String getPasswordErr(String password, boolean isRegister) {
        if (isRegister) {
            if (password.length() < 8) return "Độ dài mật khẩu >= 8 ký tự!";
        }
        if (password.isEmpty()) return warnNoEmpty;
        return null;
    }

    public static String getUsernameErr(String userName) {
        //TODO:
        if (userName.isEmpty()) return warnNoEmpty;
        return null;
    }
}
