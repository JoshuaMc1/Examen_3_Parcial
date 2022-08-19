package com.pm1.examen3p.Adaptador;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm1.examen3p.Clases.Medicamentos;
import com.pm1.examen3p.R;

import java.util.ArrayList;

public class Adaptador extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context context;
    ArrayList<Medicamentos> listaDatos;
    TextView txtID, txtDescripcion, txtCantidad, txtTiempo, txtPeriocidad;
    ImageView txtProducto;

    public Adaptador(Context context, ArrayList<Medicamentos> listaDatos) {
        this.context = context;
        this.listaDatos = listaDatos;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listaDatos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        @SuppressLint({"ViewHolder", "InflateParams"}) final View vista = inflater.inflate(R.layout.item, null);
        txtID = vista.findViewById(R.id.txtLID);
        txtDescripcion = vista.findViewById(R.id.txtLDescripcion);
        txtCantidad = vista.findViewById(R.id.txtLCantidad);
        txtTiempo = vista.findViewById(R.id.txtLTiempo);
        txtPeriocidad = vista.findViewById(R.id.txtLPeriocidad);
        txtProducto = vista.findViewById(R.id.txtLImagen);

        try {
            txtID.setText("ID: " + listaDatos.get(i).getId_medicamento());
            txtDescripcion.setText("Descripción: " + listaDatos.get(i).getDescripcion());
            txtCantidad.setText("Cantidad: " + listaDatos.get(i).getCantidad());
            txtTiempo.setText("Tiempo: " + listaDatos.get(i).getTiempo());
            txtPeriocidad.setText("Periocidad: " + listaDatos.get(i).getPeriocidad());
            byte[] decodedString = Base64.decode(listaDatos.get(i).getImagen(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            txtProducto.setImageBitmap(decodedByte);
        } catch (Exception ex) {
            Toast.makeText(vista.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return vista;
    }
}
