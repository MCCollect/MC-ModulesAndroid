package com.mc.mcmodules.view.scanine;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mc.mcmodules.R;
import com.mc.mcmodules.Utils.Utils;

import java.io.ByteArrayOutputStream;


public class ActWebView extends AppCompatActivity {

    private WebView webViewINE;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_web_view);

        webViewINE = (WebView) findViewById(R.id.webViewINE);
        webViewINE.getSettings().setLoadsImagesAutomatically(true);

        webViewINE.getSettings().setJavaScriptEnabled(true);
        webViewINE.getSettings().setDomStorageEnabled(true);

        findViewById(R.id.btnClave).setOnClickListener(v -> {
            if (getIntent().getStringExtra("flag").equals("1")) {
                copy(getIntent().getStringExtra("claveElector"));
            }
        });

        findViewById(R.id.btnEmision).setOnClickListener(v -> {
            if (getIntent().getStringExtra("flag").equals("1")) {
                copy(getIntent().getStringExtra("emision"));
            }
        });

        findViewById(R.id.btnOCR).setOnClickListener(v -> {
            if (getIntent().getStringExtra("flag").equals("1")) {
                copy(getIntent().getStringExtra("codOCR"));
            }
        });


        if(getIntent().getStringExtra("flag").equals("1")){
            findViewById(R.id.btnCapture).setVisibility(View.VISIBLE);
            findViewById(R.id.btnCapture).setOnClickListener(v -> takeScreenshot());

        }





        webViewINE.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        if (getIntent().getStringExtra("flag").equals("1")) {

            WebSettings webSettings = webViewINE.getSettings();
            webSettings.setBuiltInZoomControls(true);
            webSettings.setJavaScriptEnabled(true);
            webViewINE.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

            webViewINE.getSettings().setBuiltInZoomControls(true);
            webViewINE.getSettings().setUseWideViewPort(true);
            webViewINE.getSettings().setLoadWithOverviewMode(true);

            webViewINE.loadUrl("https://listanominal.ine.mx/scpln/");
            webViewINE.clearCache(true);
            webViewINE.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.getSettings().setLoadsImagesAutomatically(true);
                    webViewINE.setVisibility(View.VISIBLE);
                    Log.v("after load", view.getUrl());

                    String js = "javascript:var x =document.getElementById('claveElector').value = '" + getIntent().getStringExtra("claveElector") + "';" +
                            "var y=document.getElementById('numeroEmision').value = '" + getIntent().getStringExtra("emision") + "';" +
                            "var z=document.getElementById('ocr').value = '" + getIntent().getStringExtra("codOCR") + "';";
                    //"var w=document.getElementById('ocr').focus();";

                    view.evaluateJavascript(js, s -> {

                    });

                    view.loadUrl(js);


                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {

                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(ActWebView.this, description, Toast.LENGTH_SHORT).show();
                    Log.e("error", description);
                }
            });

            /*webViewINE.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    String js = "javascript:var x =document.getElementById('claveElector').value = '" + getIntent().getStringExtra("claveElector") + "';" +
                            "var y=document.getElementById('numeroEmision').value = '"+ getIntent().getStringExtra("emision") + "';" +
                            "var z=document.getElementById('ocr').value = '"+ getIntent().getStringExtra("codOCR") + "';" +
                            "var w=document.getElementById('ocr').focus();";

                    if (Build.VERSION.SDK_INT >= 19) {
                        view.evaluateJavascript(js, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {

                            }
                        });
                    } else {
                        view.loadUrl(js);
                    }
                    view.loadUrl(js);
                }
            });*/

        } else if (getIntent().getStringExtra("flag").equals("2")) {
            System.out.println("soy ultimo modelo 2");
            webViewINE.loadUrl(getIntent().getStringExtra("url"));
            webViewINE.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    if(getIntent().getStringExtra("flag").equals("2")){
                        saveWebView(view);
                    }

                }
            });
        }
    }

    private void copy(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", text);
        clipboard.setPrimaryClip(clip);
    }


    private void saveWebView(WebView webView) {
        /*
        tipoFoto = "-5";
        System.out.println("MEtdodood");
        SQLiteDatabase db = getApplicationContext().openOrCreateDatabase(DB.DB_NAME, Context.MODE_PRIVATE, null);

        try {
            DateFormat dateFormatFechaHora = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            Date dateFechaHora = new Date();
            Cursor c = db.rawQuery("SELECT * FROM " + DB.TABLE_NAME_IMAGENES + " WHERE " +
                    DB.TMP_TIPO + "='" + tipoFoto + "' AND " +
                    DB.PR_SO_NUMSOLICITUD + "='" + Utils.noSol + "'", null);


            Picture snapShot = webView.capturePicture();
            Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(),
                    snapShot.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            snapShot.draw(canvas);


            String serializable = convertBitmapToStringRedi(rediImagePorTamanio(bmp, 70));


            if (c.moveToFirst()) {
                System.out.println("ACTUALIZAR");

                try {

                    ContentValues update = new ContentValues();
                    update.put(DB.TMP_FECHA_VISITA, dateFormatFechaHora.format(dateFechaHora));
                    update.put(DB.TMP_IMAGEN, serializable);
                    db.update(DB.TABLE_NAME_IMAGENES, update,
                            DB.TMP_TIPO + "='" + tipoFoto + "' AND " +
                                    DB.PR_SO_NUMSOLICITUD + "='" + Utils.noSol + "'", null);

                    update.put(DB.TMP_IMAGEN, serializable);

                    db.update(DB.TABLE_NAME_IMAGENES_AUX, update,
                            DB.TMP_TIPO + "='" + tipoFoto + "' AND " +
                                    DB.PR_SO_NUMSOLICITUD + "='" + Utils.noSol + "'", null);

                } catch (SQLException ex) {
                    System.out.println("Error al actualizar la foto: " + ex);
                }

            } else {
                try {
                    ContentValues values = new ContentValues();
                    values.put(DB.TMP_CREDITO, Utils.noSol);
                    values.put(DB.PR_SO_NUMSOLICITUD, Utils.noSol);
                    values.put(DB.TMP_FECHA_VISITA, dateFormatFechaHora.format(dateFechaHora));
                    values.put(DB.TMP_IMAGEN, serializable);
                    values.put(DB.TMP_TIPO, tipoFoto);
                    values.put(DB.TMP_ID, Utils.noSol);
                    db.insert(DB.TABLE_NAME_IMAGENES, null, values);
                    values.put(DB.TMP_IMAGEN, serializable);
                    db.insert(DB.TABLE_NAME_IMAGENES_AUX, null, values);

                    System.out.println("--Imagen Insertada");
                } catch (SQLException ex) {
                    System.out.println("Error al insertar la foto: " + ex);
                }
            }
            c.close();
        } catch (Exception ex) {
            Log.e("Error", ex.toString());
        } finally {
            db.close();
        }
*/
    }

    private Bitmap rediImagePorTamanio(Bitmap image, int porcentaje) {
        Bitmap reply = null;
        try {
            int width = image.getWidth();
            int height = image.getHeight();
            int newWidth = (width * porcentaje) / 100;
            int newHeight = (height * porcentaje) / 100;

            System.out.println(newWidth + "/" + width);
            System.out.println(newHeight + "/" + height);

            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            reply = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);

        } catch (Exception e) {
            System.out.println("Error al intentar reducir el tamaño de la imagen");
        } finally {
            Utils.freeMemory();
        }
        return reply;
    }

    private String convertBitmapToStringRedi(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int newWidth = (width * 50) / 100;
        int newHeight = (height * 50) / 100;

        System.out.println(newWidth + "/" + width);
        System.out.println(newHeight + "/" + height);

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap a = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        a.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byte_arr = stream.toByteArray();
        return Base64.encodeToString(byte_arr, Base64.DEFAULT);
    }

    private void takeScreenshot() {
/*
        try {
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);



            String serializable = convertBitmapToStringRedi(rediImagePorTamanio(bitmap, 70));

            SQLiteDatabase db = getApplicationContext().openOrCreateDatabase(DB.DB_NAME, Context.MODE_PRIVATE, null);

            DateFormat dateFormatFechaHora = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            Date dateFechaHora = new Date();

            Cursor c = db.rawQuery("SELECT * FROM " + DB.TABLE_NAME_IMAGENES + " WHERE " +
                    DB.TMP_TIPO + "='" + tipoFoto + "' AND " +
                    DB.PR_SO_NUMSOLICITUD + "='" + Utils.noSol + "'", null);


            if (c.moveToFirst()) {
                System.out.println("ACTUALIZAR");

                try {

                    ContentValues update = new ContentValues();
                    update.put(DB.TMP_FECHA_VISITA, dateFormatFechaHora.format(dateFechaHora));
                    update.put(DB.TMP_IMAGEN, serializable);
                    db.update(DB.TABLE_NAME_IMAGENES, update,
                            DB.TMP_TIPO + "='" + tipoFoto + "' AND " +
                                    DB.PR_SO_NUMSOLICITUD + "='" + Utils.noSol + "'", null);

                    update.put(DB.TMP_IMAGEN, serializable);

                    db.update(DB.TABLE_NAME_IMAGENES_AUX, update,
                            DB.TMP_TIPO + "='" + tipoFoto + "' AND " +
                                    DB.PR_SO_NUMSOLICITUD + "='" + Utils.noSol + "'", null);

                } catch (SQLException ex) {
                    System.out.println("Error al actualizar la foto: " + ex);
                }

            } else {
                try {
                    ContentValues values = new ContentValues();
                    values.put(DB.TMP_CREDITO, Utils.noSol);
                    values.put(DB.PR_SO_NUMSOLICITUD, Utils.noSol);
                    values.put(DB.TMP_FECHA_VISITA, dateFormatFechaHora.format(dateFechaHora));
                    values.put(DB.TMP_IMAGEN, serializable);
                    values.put(DB.TMP_TIPO, tipoFoto);
                    values.put(DB.TMP_ID, Utils.noSol);
                    db.insert(DB.TABLE_NAME_IMAGENES, null, values);
                    values.put(DB.TMP_IMAGEN, serializable);
                    db.insert(DB.TABLE_NAME_IMAGENES_AUX, null, values);

                    System.out.println("--Imagen Insertada");
                } catch (SQLException ex) {
                    System.out.println("Error al insertar la foto: " + ex);
                }
            }
            c.close();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
*/
    }
}
