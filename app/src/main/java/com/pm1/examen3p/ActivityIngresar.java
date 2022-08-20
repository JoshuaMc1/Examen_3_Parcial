package com.pm1.examen3p;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pm1.examen3p.Clases.Medicamentos;

import java.io.ByteArrayOutputStream;

public class ActivityIngresar extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private CollectionReference collectionReference;
    private final String CHANNEL_ID = "canal";
    ImageView producto, atras;
    TextView titulo;
    EditText txtDescripcion, txtCantidad, txtPeriocidad;
    Spinner tiempo;
    Button btnTomarFoto, btnGuardar;
    String opcion[] = {"Seleccione una opción", "Horas", "Diarias"};
    Bitmap imageBitmap = null;
    Medicamentos medicamentos;

    NotificationManagerCompat notificationManagerCompat;
    Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar);
        init();
        btnTomarFoto.setOnClickListener(this::onClickTomarFoto);
        atras.setOnClickListener(this::onClickAtras);
        btnGuardar.setOnClickListener(this::onClickGuardar);
        tiempo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    txtPeriocidad.setText("");
                    txtPeriocidad.setVisibility(View.VISIBLE);
                    titulo.setVisibility(View.VISIBLE);
                } else {
                    txtPeriocidad.setVisibility(View.INVISIBLE);
                    titulo.setVisibility(View.INVISIBLE);
                    txtPeriocidad.setText("0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void onClickGuardar(View view) {
        if (campoVacio(txtCantidad) && campoVacio(txtDescripcion) && imageBitmap != null && tiempo.getSelectedItemId() != 0){
            guardar();
        } else message("Hay campos vacios...");
    }

    private void guardar() {
        try {
            collectionReference = FirebaseFirestore.getInstance().collection("Medicamentos");
            DocumentReference documentReference = collectionReference.document();
            String id = documentReference.getId();
            medicamentos = new Medicamentos();
            medicamentos.setKey(id);
            medicamentos.setId_medicamento(getFiveDigitsNumber());
            medicamentos.setDescripcion(txtDescripcion.getText().toString());
            medicamentos.setCantidad(Integer.parseInt(txtCantidad.getText().toString()));
            medicamentos.setTiempo(tiempo.getSelectedItem().toString());
            medicamentos.setPeriocidad(Integer.parseInt(txtPeriocidad.getText().toString()));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            medicamentos.setImagen(Base64.encodeToString(byteArray, Base64.DEFAULT));

            if (tiempo.getSelectedItem().toString().equals("Diaria")) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(ActivityIngresar.this, "Medicamentos")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("MEDICAMENTO: " + txtDescripcion.getText().toString().toUpperCase())
                        .setContentText("NO OLVIDES TOMAR TU MEDICAMENTO DIARIAMENTE");

                notification = builder.build();
                notificationManagerCompat = NotificationManagerCompat.from(ActivityIngresar.this);
            }
            else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(ActivityIngresar.this, "Medicamentos")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("MEDICAMENTO: " + txtDescripcion.getText().toString().toUpperCase())
                        .setContentText("NO OLVIDES TOMAR TU MEDICAMENTO CADA " + txtPeriocidad.getText().toString() + " HORAS");

                notification = builder.build();
                notificationManagerCompat = NotificationManagerCompat.from(ActivityIngresar.this);
            }
            collectionReference.document(id).set(medicamentos).addOnSuccessListener(unused -> {
                mostrarNotificacion("Guardado", "Datos Guardados");
                limpiarCampos();
            }).addOnFailureListener(e -> message("Error al guardar: " + e.getMessage()));

            Notify();
        } catch (Exception ex) {
            message("Error: " + ex.getMessage());
        }
    }

    private void onClickAtras(View view) {
        Intent atras = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(atras);
        finish();
    }

    private void onClickTomarFoto(View view) {
        dispatchTakePictureIntent();
    }
    private void Notify() {
        notificationManagerCompat.notify(1, notification);
    }

    private void init(){
        producto = findViewById(R.id.txtRImagen);
        atras = findViewById(R.id.btnLAtras);
        titulo = findViewById(R.id.txtRTPeriosidad);
        txtDescripcion = findViewById(R.id.txtRDescripcion);
        txtCantidad = findViewById(R.id.txtRCantidad);
        txtPeriocidad = findViewById(R.id.txtRPeriocidad);
        tiempo = findViewById(R.id.txtRTiempo);
        btnTomarFoto = findViewById(R.id.btnRTomarFoto);
        btnGuardar = findViewById(R.id.btnRGuardar);
        ArrayAdapter<String> tiempoOpciones = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, opcion);
        tiempo.setAdapter(tiempoOpciones);
        txtPeriocidad.setVisibility(View.INVISIBLE);
        titulo.setVisibility(View.INVISIBLE);
        txtPeriocidad.setText("0");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Medicamentos", "Medicamentos", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
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

    public boolean campoVacio(EditText objeto) {
        return objeto.getText().length() > 0 ? true : false;
    }

    public void limpiarCampos() {
        txtPeriocidad.setText("");
        txtDescripcion.setText("");
        txtCantidad.setText("");
        tiempo.setSelection(0);
        producto.setImageBitmap(null);
    }

    public int getFiveDigitsNumber() {
        double fiveDigits = 1000000 + Math.random() * 9000000;
        return (int) fiveDigits;
    }

    public void message(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void mostrarNotificacion(String titulo, String cuerpo){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Nueva", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            notification(titulo, cuerpo);
        }
    }

    public void notification(String titulo, String cuerpo){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, ActivityIngresar.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_nofication)
                .setContentTitle(titulo)
                .setContentText(cuerpo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(uri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_close_nofication, "Cerrar", pendingIntent);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        managerCompat.notify(2, builder.build());
    }
}