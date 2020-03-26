package com.example.cancionesbd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar1);

        setSupportActionBar(toolbar);


        //permiso de escritura en memoria
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

    }

    public boolean onCreateOptionsMenu(Menu mimenu){
        getMenuInflater().inflate(R.menu.menu_principal, mimenu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.home:
                Toast.makeText(this, "HOME", Toast.LENGTH_SHORT).show();
                break;

            case R.id.mapa:
                Intent intent = new Intent(this, MapsActivity1.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                Toast.makeText(this, "Esta es una App desarrollada en el módulo de Android. (Fecha): 11/03/2020 (Desarrollador): Samuel Isip (Curso): 2ºDAM", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    public void verCanciones(View view) {

        Intent intent = new Intent(this, activity_ver_canciones.class);

        startActivity(intent);

    }

    public void aniadirCanciones(View view) {

        Intent intent = new Intent(this, activity_aniadir.class);

        startActivity(intent);
    }

    public void modificarCanciones(View view) {
        Intent intent = new Intent(this, activity_modificar.class);

        startActivity(intent);
    }

    public void accederGPS(View view) {
        Intent intent = new Intent(this, MapsActivity1.class);

        startActivity(intent);
    }


}
