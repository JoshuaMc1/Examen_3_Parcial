package com.pm1.examen3p;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pm1.examen3p.Clases.Medicamentos;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class ActivityAcciones extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    int idProducto;
    String key;
    ImageView producto, atras;
    TextView titulo;
    EditText txtDescripcion, txtCantidad, txtPeriocidad;
    Spinner tiempo;
    Button btnTomarFoto, btnEditar, btnEliminar;
    String opcion[] = {"Seleccione una opción", "Horas", "Diarias"};
    Bitmap imageBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acciones);
        init();
        tiempo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    txtPeriocidad.setVisibility(View.VISIBLE);
                    titulo.setVisibility(View.VISIBLE);
                } else {
                    txtPeriocidad.setVisibility(View.INVISIBLE);
                    titulo.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        atras.setOnClickListener(this::onClickAtras);
        btnEliminar.setOnClickListener(this::onClickEliminar);
        btnTomarFoto.setOnClickListener(this::onClickTomarFoto);
        btnEditar.setOnClickListener(this::onClickEditar);
    }

    private void onClickEditar(View view) {
        if (campoVacio(txtCantidad) && campoVacio(txtDescripcion) && imageBitmap != null && tiempo.getSelectedItemId() != 0){
            editar();
        } else message("Hay campos vacios...");
    }

    private void editar() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            HashMap<String, Object> map = new HashMap<>();
            map.put("descripcion", txtDescripcion.getText().toString());
            map.put("cantidad", Integer.parseInt(txtCantidad.getText().toString()));
            map.put("tiempo", tiempo.getSelectedItem().toString());
            map.put("periocidad", Integer.parseInt(txtPeriocidad.getText().toString()));
            map.put("imagen", Base64.encodeToString(byteArray, Base64.DEFAULT));
            CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Medicamentos");
            collectionReference.document(key).update(map).addOnSuccessListener(unused -> {
                message("Registro actualizado con exito");
                lista();
            }).addOnFailureListener(e -> message("Error al actualizar el medicamento\n" + e.getMessage()));
        } catch (Exception ex) {
            message("Error: " + ex.getMessage());
        }
    }

    private void onClickEliminar(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Eliminar");
        alertDialogBuilder
                .setMessage("¿Deseas eliminar el siguiente medicamento (" + txtDescripcion.getText() +")?")
                .setCancelable(false)
                .setPositiveButton("Si, deseo eliminar", (dialog, id) -> {
                    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Medicamentos");
                    collectionReference.document(key).delete().addOnSuccessListener(unused -> message("Registro eliminado con exito")
                    ).addOnFailureListener(e -> message("Error al eliminar el medicamento\n" + e.getMessage()));
                    lista();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void onClickTomarFoto(View view) {
        dispatchTakePictureIntent();
    }

    private void onClickAtras(View view) {
        Intent atras = new Intent(getApplicationContext(), ActivityListar.class);
        startActivity(atras);
        finish();
    }

    private void lista(){
        Intent lista = new Intent(getApplicationContext(), ActivityListar.class);
        startActivity(lista);
        finish();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            producto.setImageBitmap(imageBitmap);
        }
    }

    private void init(){
        atras = findViewById(R.id.btnAAtras);
        producto = findViewById(R.id.txtAImagen);
        titulo = findViewById(R.id.txtATPeriosidad);
        txtDescripcion = findViewById(R.id.txtADescripcion);
        txtCantidad = findViewById(R.id.txtACantidad);
        txtPeriocidad = findViewById(R.id.txtAPeriocidad);
        tiempo = findViewById(R.id.txtATiempo);
        btnTomarFoto = findViewById(R.id.btnATomarFoto);
        btnEditar = findViewById(R.id.btnAEditar);
        btnEliminar = findViewById(R.id.btnAEliminar);
        ArrayAdapter<String> tiempoOpciones = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, opcion);
        tiempo.setAdapter(tiempoOpciones);
        txtPeriocidad.setVisibility(View.INVISIBLE);
        titulo.setVisibility(View.INVISIBLE);
        txtPeriocidad.setText("0");
        llenarCampos();
    }

    private void llenarCampos() {
        idProducto = this.getIntent().getExtras().getInt("idProducto");
        key = this.getIntent().getExtras().getString("key");
        txtDescripcion.setText(this.getIntent().getExtras().getString("descripcion"));
        txtCantidad.setText(String.valueOf(this.getIntent().getExtras().getInt("cantidad")));
        String tiempoP = this.getIntent().getExtras().getString("tiempo");
        if (tiempoP.equals("Horas")) {
            tiempo.setSelection(1);
        } else tiempo.setSelection(2);
        txtPeriocidad.setText(String.valueOf(this.getIntent().getExtras().getInt("periocidad")));
        byte[] decodedString = Base64.decode(this.getIntent().getExtras().getString("imagen"), Base64.DEFAULT);
        imageBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        producto.setImageBitmap(imageBitmap);
    }

    public boolean campoVacio(EditText objeto) {
        return objeto.getText().length() > 0 ? true : false;
    }

    public void message(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}