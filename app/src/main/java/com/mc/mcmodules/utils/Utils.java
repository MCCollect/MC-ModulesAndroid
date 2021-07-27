package com.mc.mcmodules.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.Objects;
import java.util.regex.Pattern;

public class Utils {


    public static Spanned fromHtml(String html) {

        return HtmlCompat.fromHtml(
                html,
                HtmlCompat.FROM_HTML_MODE_COMPACT
        );
    }

    public static void snackbar(View view, String message, int length) {
        Snackbar.make(view, message, length).show();
    }

    public static void hideKeyboard(@NonNull Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            view.clearFocus();
        }
    }


    public static Bitmap decodeFromBase64(String encodedImage) {
        try {
            byte[] decodedString = Base64.decode(encodedImage.getBytes());
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            return null;
        }
    }


    public static boolean checkField(@NotNull TextInputLayout inputLayout) {
        inputLayout.setError(null);

        if (Objects.requireNonNull(inputLayout.getEditText()).getText().toString().equals("")) {
            inputLayout.setError("Campo obligatorio");
            return false;

        } else {
            return true;
        }

    }

    public static boolean checkURL(CharSequence input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean isURL = URL_PATTERN.matcher(input).matches();
        if (!isURL) {
            String urlString = input + "";
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    new URL(urlString);
                    isURL = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return isURL;
    }


    public static void freeMemory() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.runFinalization();
                Runtime.getRuntime().freeMemory();
                Runtime.getRuntime().gc();
                System.gc();
            }
        }).start();
    }


}
