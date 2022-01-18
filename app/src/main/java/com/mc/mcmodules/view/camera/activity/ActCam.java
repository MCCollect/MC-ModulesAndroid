package com.mc.mcmodules.view.camera.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.canhub.cropper.CropImageView;
import com.mc.alternativasur.api.interfaces.ActivityResultHandler;
import com.mc.mcmodules.R;
import com.mc.mcmodules.model.classes.data.DataCamera;
import com.mc.mcmodules.model.classes.library.Acelerometro;
import com.mc.mcmodules.model.classes.library.SharePreference;
import com.mc.mcmodules.utils.Utils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tapadoo.alerter.Alerter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.zelory.compressor.Compressor;

public class ActCam extends AppCompatActivity implements ActivityResultHandler {

    public static final int CODIGO_OK_CAMERA = 104;

    /* DECLARACION DE OBJETOS */

    private Acelerometro acelerometro;

    private SurfaceView camera_preview;     // Preview de la camara
    private ImageView button_capture;       // Tomar la fotografia
    private ImageView btnNo;                // Cancelar la foto / volver a tomar
    private ImageView btnFlash;             // Configurar flash
    private ImageView rotateLeft;           // Rotar la imagen a la izquierda
    private ImageView rotateRight;          // Rotar la imagen a la izquierda

    private SurfaceHolder previewHolder = null;
    public Camera camera;
    private Camera.PictureCallback mPicture;

    private ScaleGestureDetector SGD;
    private Camera.Parameters parameters;
    private File pictureFile;

    private Bitmap detectDarknessBitmap;
    private File dir;
    private Target target;

    public static CropImageView cropImageView;

    /* DECLARACIOON DE VARIABLES GLOBALES */

    int currentZoomLevel = 0;
    private int maxZoomLevel;
    private boolean flagFlash = true;

    private SharePreference preference;

    private boolean flagOk = false;
    private int tipoFlash = 1;

    private ImageView editButton;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* ESTABLECER LA ACTIVIDAD EN PANTALLA COMPLETA */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.act_cam);
        editButton = findViewById(R.id.editButton);

        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                Utils.freeMemory();

                double ejex = acelerometro.getEjex();
                double ejey = acelerometro.getEjey();
                double ejez = acelerometro.getEjez();

                /***************** REDIMENSIONAR LA IMAGEN SEGUN SU TAMAÑO*********************/
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();


                cropImageView.setImageBitmap(bitmap);
                cropImageView.setCropRect(new Rect(0, 0, width, height));


                if ((ejex >= -10 && ejex <= -1) && (ejey >= -1 && ejey <= 2.3) && (ejez >= 1 && ejez <= 10.3)) {
                    System.out.println("horizontal inversa");
                    RotateBitmap(180);
                } else if ((ejex <= 1.6 && ejex >= -1.5) && (ejey >= 1.5 && ejey <= 10) && (ejez >= 0 && ejez <= 10.3)) {
                    System.out.println("vertical");
                    RotateBitmap(90);
                } else if ((ejex >= -0.4 && ejex <= 0.4) && (ejey >= -10 && ejey <= -0.9) && (ejez >= 0 && ejez <= 10.5)) {
                    System.out.println("vertical inversa");
                    RotateBitmap(270);
                }


                (findViewById(R.id.buttonNo)).setVisibility(View.VISIBLE);
                (findViewById(R.id.buttonNo)).setEnabled(true);

                (findViewById(R.id.buttonRotate90pos)).setEnabled(true);
                (findViewById(R.id.buttonRotateM90pos)).setEnabled(true);
                editButton.setEnabled(true);

                button_capture.setImageResource(R.drawable.icon_circle_ok);

                camera_preview.setVisibility(View.INVISIBLE);
                if (camera != null) {
                    camera.stopPreview();
                }

                flagOk = true;
                (findViewById(R.id.button_capture)).setEnabled(true);
                (findViewById(R.id.buttonNo)).setVisibility(View.VISIBLE);
                cropImageView.setVisibility(View.VISIBLE);
                (findViewById(R.id.buttonRotate90pos)).setVisibility(View.VISIBLE);
                (findViewById(R.id.buttonRotateM90pos)).setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                System.out.println(" bitmap error al añadir");
                e.printStackTrace();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }

        };


        /* PREPARAR EL SENSOR ACELEROMETRO */
        acelerometro = new Acelerometro(this);
        preference = SharePreference.getInstance(this);

        /* DECLARACION INSTANCIAS */
        camera_preview = findViewById(R.id.surface);
        button_capture = findViewById(R.id.button_capture);
        btnNo = findViewById(R.id.buttonNo);
        btnFlash = findViewById(R.id.buttonFlash);
        rotateLeft = findViewById(R.id.buttonRotate90pos);
        rotateRight = findViewById(R.id.buttonRotateM90pos);
        cropImageView = findViewById(R.id.cropImageView);
        cropImageView.setAutoZoomEnabled(true);

        btnNo.setVisibility(View.GONE);
        rotateLeft.setVisibility(View.GONE);
        rotateRight.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);

        /* DECLARACIÓN DE EVENTOS */
        camera_PictureCallback();
        rotateLeft_setOnClickListener();
        rotateRiht_setOnClickListener();
        btnNo_setOnClickListener();
        btnFlash_setOnClickListener();
        button_capture_setOnClickListener();

        /* COMPROBAMOS SI EL DISPOSITIVO TIENE FLASH */
        if (!suportFlash()) {
            flagFlash = false;
        }

        if (flagFlash)
            btnFlash.setVisibility(View.VISIBLE);
        else
            btnFlash.setVisibility(View.GONE);

        previewHolder = camera_preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        previewHolder.setFixedSize(1280, 1024);

        SGD = new ScaleGestureDetector(this, new ScaleListener());
    }

    private void rotateLeft_setOnClickListener() {

        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(-90);
                System.out.println("Rotando a la izquierda");

            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //Write file
                    /*Intent in1 = new Intent(Act4Cam.this, Act5EditorBitmap.class);
                    in1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in1);*/

                    Intent intent = new Intent(ActCam.this, ActEditorBitmap.class);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(ActCam.this, R.anim.slide_in_act2, R.anim.slide_out_act2);
                    startActivity(intent, options.toBundle());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void rotateRiht_setOnClickListener() {

        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(90);
                System.out.println("Rotando a la derecha");
            }
        });
    }

    private void btnNo_setOnClickListener() {

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cropImageView.setImageBitmap(null);
                (findViewById(R.id.buttonNo)).setEnabled(false);
                cropImageView.clearImage();

                (findViewById(R.id.buttonRotate90pos)).setVisibility(View.GONE);
                (findViewById(R.id.buttonRotateM90pos)).setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
                if (flagFlash) {
                    (findViewById(R.id.buttonFlash)).setVisibility(View.VISIBLE);
                } else {
                    (findViewById(R.id.buttonFlash)).setVisibility(View.GONE);
                }
                Animation modal_in = AnimationUtils.loadAnimation(ActCam.this, R.anim.modal_in);
                modal_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ((ImageView) findViewById(R.id.button_capture)).setImageResource(R.drawable.icon_circle);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                modal_in.reset();
                (findViewById(R.id.button_capture)).startAnimation(modal_in);

                Animation modal_out = AnimationUtils.loadAnimation(ActCam.this, R.anim.modal_out);
                modal_out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                modal_out.reset();
                (findViewById(R.id.buttonFlash)).startAnimation(modal_out);

                ((ImageView) findViewById(R.id.button_capture)).setImageResource(R.drawable.icon_circle);
                (findViewById(R.id.buttonNo)).setVisibility(View.GONE);
                (findViewById(R.id.button_capture)).setVisibility(View.VISIBLE);
                (findViewById(R.id.button_capture)).setEnabled(true);

                camera_preview.setVisibility(View.VISIBLE);
                cropImageView.setVisibility(View.INVISIBLE);
                try {
                    if (camera == null)
                        Toast.makeText(ActCam.this, "No es posible tomar la fotografía, la cámara no está disponible.", Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                flagOk = false;
                Utils.freeMemory();
            }
        });
    }

    private void btnFlash_setOnClickListener() {

        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * 0 = Apagado
                 * 1 = Automatico
                 * 2 = Encendido
                 * */
                tipoFlash++;
                if (tipoFlash == 3) {
                    tipoFlash = 0;
                }
                switch (tipoFlash) {
                    case 0:
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(parameters);
                        ((ImageView) findViewById(R.id.buttonFlash)).setImageResource(R.drawable.ic_flash_off);
                        break;
                    case 1:
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                        camera.setParameters(parameters);
                        ((ImageView) findViewById(R.id.buttonFlash)).setImageResource(R.drawable.ic_flash_auto);
                        break;
                    case 2:
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        camera.setParameters(parameters);
                        ((ImageView) findViewById(R.id.buttonFlash)).setImageResource(R.drawable.ic_flash_on);
                        break;
                }
            }
        });
    }

    private void button_capture_setOnClickListener() {

        button_capture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                (findViewById(R.id.button_capture)).setEnabled(false);
                (findViewById(R.id.buttonNo)).setEnabled(true);
                try {
                    camera.setParameters(parameters);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                (findViewById(R.id.buttonRotate90pos)).setEnabled(false);
                (findViewById(R.id.buttonRotateM90pos)).setEnabled(false);
                editButton.setEnabled(false);

                Animation modal_out = AnimationUtils.loadAnimation(ActCam.this, R.anim.modal_out);
                modal_out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        (findViewById(R.id.buttonFlash)).setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                modal_out.reset();
                (findViewById(R.id.buttonFlash)).startAnimation(modal_out);

                Animation modal_in = AnimationUtils.loadAnimation(ActCam.this, R.anim.modal_in);
                modal_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                modal_in.reset();
                (findViewById(R.id.buttonNo)).startAnimation(modal_in);

                Animation modal_in_cap = AnimationUtils.loadAnimation(ActCam.this, R.anim.modal_in);
                modal_in_cap.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        ((ImageView) findViewById(R.id.button_capture)).setImageResource(R.drawable.icon_circle_ok);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                modal_in_cap.reset();
                (findViewById(R.id.button_capture)).startAnimation(modal_in_cap);

                if (flagOk) {

                    (findViewById(R.id.button_capture)).setEnabled(false);

                    DateFormat dateFormatFoto = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    Date dateFoto = new Date();
                    final String Fecha = dateFormatFoto.format(dateFoto);
                    Utils.freeMemory();
                    final File file = new File(dir, "ImgBitmap.jpg");


                    OutputStream os;
                    try {
                        os = new BufferedOutputStream(new FileOutputStream(file));
                        cropImageView.getCroppedImage().compress(Bitmap.CompressFormat.JPEG, 70, os);
                        os.close();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    cropImageView.setImageBitmap(null);
                    cropImageView.clearImage();

                    flagOk = false;

                    ActCam.this.overridePendingTransition(R.anim.slide_in_act, R.anim.slide_out_act);
                    if (camera != null)
                        camera.release();

                    handleResult(new DataCamera(file.getPath()));

                } else if (!flagOk) {
                    button_capture.setEnabled(false);
                    try {
                        camera.takePicture(null, null, mPicture);
                    } catch (Exception e) {
                        System.out.println("EX controlada: " + e.toString());
                    }
                }
            }
        });
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    public byte[] getBytes(File file) {

        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Toast.makeText(this, "Error en el getbytes", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Toast.makeText(this, "Error en el getbytes2", Toast.LENGTH_SHORT).show();
        }

        return bytes;

    }


    private void camera_PictureCallback() {

        mPicture = new Camera.PictureCallback() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {

                    dir = new File(getFilesDir(), "MC_picasso");
                    if (!dir.exists())
                        dir.mkdirs();

                    pictureFile = new File(dir, "Img.jpg");

                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();


                    System.out.println("Peso de la imagen antes : " + pictureFile.length());

                    File pictureFileC = new Compressor(ActCam.this).compressToFile(pictureFile);

                    System.out.println("Peso de la imagen despues : " + pictureFileC.length());

                    if (!pictureFileC.exists())
                        pictureFileC.mkdirs();


                    Picasso.get().load(pictureFileC).memoryPolicy(MemoryPolicy.NO_CACHE).into(target);


                } catch (Exception ignored) {
                } finally {
                    Utils.freeMemory();
                }
            }
        };

    }

    private String convertBitmapToString(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] byte_arr = stream.toByteArray();
        return Base64.encodeToString(byte_arr, Base64.DEFAULT);
    }


    public boolean onTouchEvent(MotionEvent ev) {
        SGD.onTouchEvent(ev);
        return true;
    }

    private void RotateBitmap(int angle) {
        try {
            cropImageView.rotateImage(angle);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /* COMPROBAR SI TIENE FLASH */
    private boolean suportFlash() {
        boolean flash;
        try {
            flash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        } catch (Exception e) {
            flash = false;
        }
        return flash;
    }

    @Override
    public void handleResult(@NonNull Parcelable parcelable) {
        Intent intentRegreso = new Intent();
        intentRegreso.putExtra("result_object", parcelable);
        setResult(AppCompatActivity.RESULT_OK, intentRegreso);
        finish();
    }

    private class ScaleListener extends ScaleGestureDetector.
            SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            double scaleFactor = detector.getScaleFactor();
            if (1.0f > scaleFactor) {
                if (currentZoomLevel > 0) {
                    currentZoomLevel--;
                    parameters.setZoom(currentZoomLevel);
                    camera.setParameters(parameters);
                }
            } else {
                if (currentZoomLevel < maxZoomLevel) {
                    currentZoomLevel++;
                    //mCamera.startSmoothZoom(currentZoomLevel);
                    parameters.setZoom(currentZoomLevel);
                    camera.setParameters(parameters);
                }
            }
            return true;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (camera != null)
            camera.release();
        camera = getCameraInstance();
        if (camera == null) {
            Toast.makeText(this, "No es posible tomar la fotografía, la cámara no está disponible.   on resume", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (camera != null)
            camera.release();
        if (camera == null) {
            Toast.makeText(this, "No es posible tomar la fotografía, la cámara no está disponible.   on resume", Toast.LENGTH_LONG).show();
        }
        //finish();
    }

    @Override
    public void onBackPressed() {

        try {

            flagOk = false;
            finish();
            ActCam
                    .this.overridePendingTransition(R.anim.slide_in_act, R.anim.slide_out_act);

            if (camera != null) {
                camera.stopPreview();
                camera.release();
                System.out.println("Sie es diferente de null en el back");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        return (result);
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setParameters(parameters);
            } catch (Throwable ignored) {
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            try {
                if (camera != null) {
                    parameters = camera.getParameters();
                }
                if (parameters != null) {
                    if (parameters.getSupportedFocusModes()
                            .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    }
                    Camera.Size size = getBestPreviewSize(width, height,
                            parameters);
                    if (size != null) {
                        parameters.setPreviewSize(size.width, size.height);
                        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
                        Camera.Size size2 = sizes.get(0);
                        for (int i = 0; i < sizes.size(); i++) {
                            if (sizes.get(i).width > size2.width)
                                size2 = sizes.get(i);
                        }

                        parameters.setPictureSize(size2.width, size2.height);
                        camera.setParameters(parameters);
                        System.out.println("===================Se establecieron lod parametros correctamente");
                        camera.setDisplayOrientation(90);
                        camera.startPreview();

                        if (parameters.isZoomSupported()) {
                            maxZoomLevel = parameters.getMaxZoom();
                        }

                        if (flagFlash) {
                            switch (tipoFlash) {
                                case 0:
                                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                    camera.setParameters(parameters);
                                    ((ImageView) findViewById(R.id.buttonFlash)).setImageResource(R.drawable.ic_flash_off);
                                    break;
                                case 1:
                                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                                    camera.setParameters(parameters);
                                    ((ImageView) findViewById(R.id.buttonFlash)).setImageResource(R.drawable.ic_flash_auto);
                                    break;
                                case 2:
                                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                                    camera.setParameters(parameters);
                                    ((ImageView) findViewById(R.id.buttonFlash)).setImageResource(R.drawable.ic_flash_on);
                                    break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception surfaceChanged: " + e.toString());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(0);
        } catch (Exception ignored) {
        }
        return c;
    }

    private Bitmap rediImagePorTamanio(Bitmap image, int porcentaje) {
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
            return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);

        } catch (Exception e) {
            System.out.println("Error al intentar reducir el tamaño de la imagen");
        } finally {
            Utils.freeMemory();
        }
        return null;
    }

    private void alerter(String title, String text, Drawable icon) {
        Alerter.create(ActCam.this)
                .setTitle(title)
                .setText(text)
                .setIcon(icon)
                .setBackgroundColorRes(R.color.error)
                .setDuration(4000)
                .show();
    }


}
