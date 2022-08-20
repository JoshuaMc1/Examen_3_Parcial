package com.pm1.examen3p;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pm1.examen3p.Adaptador.Adaptador;
import com.pm1.examen3p.Clases.Medicamentos;

import java.util.ArrayList;

public class ActivityListar extends AppCompatActivity {
    ArrayList<Medicamentos> datosMedicamentos = new ArrayList<>();
    ListView lista;
    ImageView atras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);
        init();
        atras.setOnClickListener(this::onClickAtras);
        lista.setOnItemClickListener((parent, view, i, id) -> {
            Intent acciones = new Intent(getApplicationContext(), ActivityAcciones.class);
            acciones.putExtra("idProducto", datosMedicamentos.get(i).getId_medicamento());
            acciones.putExtra("key", datosMedicamentos.get(i).getKey());
            acciones.putExtra("descripcion", datosMedicamentos.get(i).getDescripcion());
            acciones.putExtra("cantidad", datosMedicamentos.get(i).getCantidad());
            acciones.putExtra("tiempo", datosMedicamentos.get(i).getTiempo());
            acciones.putExtra("periocidad", datosMedicamentos.get(i).getPeriocidad());
            acciones.putExtra("imagen", datosMedicamentos.get(i).getImagen());
            startActivity(acciones);
            finish();
        });
    }

    private void onClickAtras(View view) {
        Intent atras = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(atras);
        finish();
    }

    private void init(){
        lista = findViewById(R.id.txtLLista);
        atras = findViewById(R.id.btnLAtras);
        cargarProductos();
    }

    private void cargarProductos(){
        try {
            CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Medicamentos");
            collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot item : queryDocumentSnapshots) {
                        Medicamentos medicamentos = item.toObject(Medicamentos.class);
                        datosMedicamentos.add(medicamentos);
                    }
                     lista.setAdapter(new Adaptador(this, datosMedicamentos));
                }
            }).addOnFailureListener(e -> Log.e("Error", e.getMessage()));
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
    }
}