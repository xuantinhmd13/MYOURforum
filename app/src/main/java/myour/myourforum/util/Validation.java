package myour.myourforum.util;

import android.util.Patterns;

public class Validation {
    private static final String warnNoEmpty = "Không được để trống!";

    public static String getErrEmail(String email) {
        if (email.isEmpty()) return warnNoEmpty;
        if (email.length() > 100) return "Email quá dài!";
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "Không đúng định dạng\n VD: example@mail.com";
        return null;
    }

    public static String getErrPassword(String password, boolean isRegister) {
        if (password.isEmpty()) return warnNoEmpty;
        if (isRegister) {
            if (password.length() < 8) return "Độ dài mật khẩu >= 8 ký tự!";
            if (password.length() > 500) return "Mật khẩu quá dài!";
        }
        return null;
    }

    public static String getErrUsername(String userName) {
        if (userName.isEmpty()) return warnNoEmpty;
        if (userName.length() > 50) return "Tên người dùng quá dài!";
        return null;
    }

    public static String getErrTitlePost(String title) {
        if (title.isEmpty()) return warnNoEmpty;
        if (title.length() < 30) return "Tiêu đề không được quá ngắn!";
        return null;
    }

    public static String getErrContentPost(String content) {
        if (content.isEmpty()) return warnNoEmpty;
        if (content.length() < 100) return "Nội dung không được quá ngắn!";
        return null;
    }

    public static String getErrPhoneNumber(String phoneNumber) {
        if (phoneNumber.isEmpty()) return warnNoEmpty;
        if (phoneNumber.length() < 10) return "Đây không phải số điện thoại!";
        return null;
    }

    public static String getErrDescription(String description) {
        if (description.isEmpty()) return warnNoEmpty;
        if (description.length() > 500) return "Mô tả quá dài!";
        return null;
    }
}
