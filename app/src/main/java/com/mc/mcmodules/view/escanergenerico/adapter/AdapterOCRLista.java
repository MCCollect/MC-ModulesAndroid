package com.mc.mcmodules.view.escanergenerico.adapter;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mc.mcmodules.R;

import java.util.List;


public class AdapterOCRLista extends RecyclerView.Adapter<AdapterOCRLista.ViewHolder> {
    private final List<String> texto;
    private final int Layout;
    private final OnClicked Clicked;
    private final Activity activity;

    public AdapterOCRLista(List<String> texto, int layout, Activity activity, OnClicked clicked) {
        this.texto = texto;
        Layout = layout;
        Clicked = clicked;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(Layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(texto.get(position), Clicked);
    }

    @Override
    public int getItemCount() {
        return texto.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView tvTexto;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvTexto = itemView.findViewById(R.id.txtNombreItem);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(final String texto, final OnClicked clicked) {
            this.tvTexto.setText(texto);
            itemView.setOnClickListener(v -> clicked.onItemClicked(texto, getAdapterPosition()));
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.delete_item) {
                texto.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                return true;
            }
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Acción");
            MenuInflater inflater = activity.getMenuInflater();
            inflater.inflate(R.menu.menu_cfe, menu);
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setOnMenuItemClickListener(this);
            }
        }
    }

    public interface OnClicked {
        void onItemClicked(String texto, int posicion);
    }
}