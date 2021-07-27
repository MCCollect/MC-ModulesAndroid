package com.mc.mcmodules.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
