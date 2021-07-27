package com.mc.mcmodules.model.classes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.mc.mcmodules.R;
import com.mc.mcmodules.utils.Utils;

import java.util.concurrent.Callable;

public class CustomAlert {

    private static final String TAG = CustomAlert.class.getName();

    private final Context act;

    private final View view;
    private final Dialog alertDialog;

    @SuppressLint("InflateParams")
    public CustomAlert(Context act) {

        LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.act = act;
        this.view = inflater.inflate(R.layout.custom_alert, null);

        alertDialog = new Dialog(act);
        alertDialog.setOnDismissListener(dialogInterface -> {
            ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(null);
            ((ImageView) view.findViewById(R.id.close)).setImageDrawable(null);
            view.findViewById(R.id.buttonLeft).setBackground(null);
            view.findViewById(R.id.buttonRight).setBackground(null);
        });
        view.findViewById(R.id.close).setOnClickListener(v -> alertDialog.dismiss());
    }

    public void addView(View viewCustom) {
        view.findViewById(R.id.text).setVisibility(View.GONE);
        ((LinearLayout) view.findViewById(R.id.Content)).addView(viewCustom);
    }

    public LinearLayout getContent() {
        return view.findViewById(R.id.Content);
    }

    public void ajustar(final int height) {

        final RelativeLayout layout = view.findViewById(R.id.relativeInfo);
        final ViewGroup.LayoutParams params = layout.getLayoutParams();

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                layout.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalHeight = layout.getMeasuredHeight();
                //Log.i(TAG, "Size display: " + height);
                //Log.i(TAG, "Size layout_domicilio_visitado:  " + finalHeight);

                if ((finalHeight + 250) > (height - 100)) {
                    //Log.i(TAG, "RECORTAR LAYOUT");
                    params.height = height - 400;
                    layout.setLayoutParams(params);
                }
                return true;
            }
        });
    }

    public void setTypeCustom(Drawable icon, String title, String text) {
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(icon);
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        ((TextView) view.findViewById(R.id.text)).setText(text);
        view.findViewById(R.id.linearButtons).setVisibility(View.GONE);
        view.findViewById(R.id.view).setVisibility(View.GONE);
    }

    public void setTypeCustom(Drawable icon, String title, String text, String textOneButton) {
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(icon);
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        ((TextView) view.findViewById(R.id.text)).setText(Utils.fromHtml(text));
        ((Button) view.findViewById(R.id.buttonLeft)).setText(textOneButton);
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
    }

    public void setTypeCustom(Drawable icon, String title, String text, String txtLeft, String txtRight) {
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(icon);
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        ((TextView) view.findViewById(R.id.text)).setText(text);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(txtLeft);
        ((Button) view.findViewById(R.id.buttonRight)).setText(txtRight);
    }

    public void setTypeCustomBtn(Drawable icon, String title, String textOneButton) {
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(icon);
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        view.findViewById(R.id.text).setVisibility(View.GONE);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(textOneButton);
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
    }

    public void setTypeCustomView(Drawable icon, String title, String textButtonLeft) {
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(icon);
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        view.findViewById(R.id.text).setVisibility(View.GONE);
        view.findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(textButtonLeft);
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
    }

    public void setTypeWarning(String title, String text, String textButtonLeft) {
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_warning));
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        ((TextView) view.findViewById(R.id.text)).setText(text);
        view.findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(textButtonLeft);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
    }

    public void setTypeWarning(String title, String text, String textButtonLeft, String textButtonRight) {
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_warning));
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        ((TextView) view.findViewById(R.id.text)).setText(text);
        view.findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        view.findViewById(R.id.viewVertical).setVisibility(View.VISIBLE);
        view.findViewById(R.id.buttonRight).setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(textButtonLeft);
        ((Button) view.findViewById(R.id.buttonRight)).setText(textButtonRight);
    }

    public void setTypeError(String title, String texto, String OneButton) {
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        ((TextView) view.findViewById(R.id.text)).setText(Utils.fromHtml(texto));
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        view.findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(OneButton);
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_error);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_close));
    }

    public void setTypeError(String title, String text, String textButtonLeft, String textButtonRight) {
        view.findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        view.findViewById(R.id.buttonRight).setVisibility(View.VISIBLE);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        ((TextView) view.findViewById(R.id.text)).setText(text);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(textButtonLeft);
        view.findViewById(R.id.viewVertical).setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.buttonRight)).setText(textButtonRight);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_error);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_close));
    }

    public void setTypeProgress(String title, String text, String textOneButton) {
        view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        ((TextView) view.findViewById(R.id.text)).setText(text);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(textOneButton);
        view.findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
    }

    public void setTypeProgress(String title) {
        view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(null);
    }

    public void setTypeView(Drawable icon, String title, String txtLeft, String txtRight) {
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        view.findViewById(R.id.viewVertical).setVisibility(View.VISIBLE);
        view.findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        view.findViewById(R.id.buttonRight).setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(txtLeft);
        ((Button) view.findViewById(R.id.buttonRight)).setText(txtRight);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(icon);
    }

    public void setTypeView(Drawable icon, String title, String txtLeft) {
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        view.findViewById(R.id.viewVertical).setVisibility(View.VISIBLE);
        view.findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        view.findViewById(R.id.buttonRight).setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(txtLeft);
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(icon);
    }

    public void setTypeView(Drawable icon, String txtLeft) {
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(txtLeft);
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(icon);
    }

    public void setTypeView(String title, String txtLeft) {
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(txtLeft);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_person));
    }

    public void setTypeView(String title, String txtLeft, String txtRight) {
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        ((TextView) view.findViewById(R.id.buttonLeft)).setText(txtLeft);
        ((TextView) view.findViewById(R.id.buttonRight)).setText(txtRight);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_person));
    }

    public void setTypeSuccess(String title, String text, String textLeftButton, String textRightButton) {
        (view.findViewById(R.id.buttonRight)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.buttonLeft)).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        ((TextView) view.findViewById(R.id.text)).setText(text);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(textLeftButton);
        ((Button) view.findViewById(R.id.buttonRight)).setText(textRightButton);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_success);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_done));
    }

    public void setTypeSuccess(String title, String text, String textLeftButton) {

        (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
        (view.findViewById(R.id.buttonRight)).setVisibility(View.GONE);
        (view.findViewById(R.id.buttonLeft)).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.buttonLeft)).setText(textLeftButton);
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        ((TextView) view.findViewById(R.id.text)).setText(text);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_success);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_done));
    }

    public void setTypeSearch(String title, String textLeft) {

        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        view.findViewById(R.id.linearButtons).setVisibility(View.VISIBLE);
        view.findViewById(R.id.text).setVisibility(View.GONE);
        // TODO
        /*(view.findViewById(R.id.filter)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.filter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(contexto, view.findViewById(R.id.filter));
                popupMenu.getMenuInflater().inflate(R.menu.order_by, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        LinearLayout contenedor = view.findViewById(R.id.Content);
                        contenedor.removeAllViews();
                        View view = inflater.inflate(R.layout_domicilio_visitado.alert_dialog_search, contenedor, true);
                        RecyclerView recycler = view.findViewById(R.id.recyclerSearch);
                        LinearLayoutManager linearManager = new LinearLayoutManager(view.getContext());
                        recycler.setLayoutManager(linearManager);
                        linearManager.setOrientation(RecyclerView.VERTICAL);
                        switch (item.getItemId()) {
                            case R.id.nav_nombre:
                                AdapterCard adapterNombre = new AdapterCard(view.getContext(), buscarClientes(DB.PR_CD_NOMBRE));
                                recycler.setAdapter(adapterNombre);
                                break;
                            case R.id.nav_cliente:
                                AdapterCard adapterClientes = new AdapterCard(view.getContext(), buscarClientes(DB.CLIENTE));
                                recycler.setAdapter(adapterClientes);
                                break;
                            case R.id.nav_producto:
                                AdapterCard adapterClientesP = new AdapterCard(view.getContext(), buscarClientes(DB.PRODUCTO));
                                recycler.setAdapter(adapterClientesP);
                                break;
                            case R.id.nav_credito:
                                AdapterCard adapterClientesC = new AdapterCard(view.getContext(), buscarClientes(DB.CREDITO));
                                recycler.setAdapter(adapterClientesC);
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });*/
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(textLeft);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_search));
    }

    public void setTypePhoto(String title, String textOneButton) {

        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        //TODO (view.findViewById(R.id.filter)).setVisibility(View.GONE);
        view.findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(textOneButton);
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_alert);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_camera));
    }

    public void setTypeSuccessSync(String title, String textLeftButton, String textRightButton) {
        view.findViewById(R.id.close).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(textLeftButton);
        ((Button) view.findViewById(R.id.buttonRight)).setText(textRightButton);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_success);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_done));
    }

    public void setTypeReady(String text, String btnText) {
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        view.findViewById(R.id.circleView).setBackground(ContextCompat.getDrawable(act, R.drawable.circle_success));
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_done));
        ((TextView) view.findViewById(R.id.titulo)).setTextSize(19);
        ((TextView) view.findViewById(R.id.titulo)).setText(text);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(btnText);
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
    }

    public void setTypeViewError(String title, String OneButton) {
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(OneButton);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_error);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_close));
    }

    public void setTypeFail(String title, String text, String textonebutton) {

        (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
        ((TextView) view.findViewById(R.id.text)).setText(text);
        ((Button) view.findViewById(R.id.buttonLeft)).setText(textonebutton);
        view.findViewById(R.id.viewVertical).setVisibility(View.GONE);
        view.findViewById(R.id.buttonRight).setVisibility(View.GONE);
        view.findViewById(R.id.circleView).setBackgroundResource(R.drawable.circle_error);
        ((ImageView) view.findViewById(R.id.circleView)).setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ic_close));
    }

    public void setAll(int v) {
        view.findViewById(R.id.all).setVisibility(v);
    }

    public ImageView getAll() {
        return view.findViewById(R.id.all);
    }

    public ImageView getClose() {
        return view.findViewById(R.id.close);
    }

    public Button getBtnLeft() {
        return view.findViewById(R.id.buttonLeft);
    }

    public Button getBtnRight() {
        return view.findViewById(R.id.buttonRight);
    }

    public CheckBox getSelectAll() {
        return view.findViewById(R.id.selectAll);
    }

    public void setTitle(String title) {
        ((TextView) view.findViewById(R.id.titulo)).setText(title);
    }

    public TextView getTitle() {
        return view.findViewById(R.id.titulo);
    }

    public ImageView getIconView() {
        return view.findViewById(R.id.circleView);
    }

    public void setCancelable(boolean status) {
        alertDialog.setCancelable(status);
    }

    public void show() {
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(view);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        alertDialog.show();
    }

    public void show(ProgressDialog pd) {
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(view);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        alertDialog.show();

        if (pd != null)
            pd.dismiss();
    }

    public TextView getText() {
        return view.findViewById(R.id.text);
    }

    public void close() {
        alertDialog.dismiss();
    }

    public void setKeyBackPressed(final Callable<Integer> func) {
        alertDialog.setOnKeyListener((dialog, keyCode, event) -> {
            try {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    func.call();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
    }
}