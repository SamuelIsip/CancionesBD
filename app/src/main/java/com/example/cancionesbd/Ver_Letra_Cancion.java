package com.example.cancionesbd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Ver_Letra_Cancion extends AppCompatActivity {

    TextView letra_cancion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver__letra__cancion);

        letra_cancion = findViewById(R.id.txtLetraCnacion);

        Intent intent = getIntent();

        String letraCancion = intent.getStringExtra("letra_cancion");

        letra_cancion.setText(letraCancion);

    }
}
