package com.pm1.examen3p;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    CardView ingrear, listar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        ingrear.setOnClickListener(this::onClickIngresar);
        listar.setOnClickListener(this::onClickListar);
    }

    private void onClickListar(View view) {
        Intent listar = new Intent(getApplicationContext(), ActivityListar.class);
        startActivity(listar);
        finish();
    }

    private void onClickIngresar(View view) {
        Intent ingresar = new Intent(getApplicationContext(), ActivityIngresar.class);
        startActivity(ingresar);
        finish();
    }

    private void init(){
        ingrear = findViewById(R.id.btnIngresar);
        listar = findViewById(R.id.btnListar);
    }
}