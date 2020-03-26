package com.example.cancionesbd;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class activity_aniadir extends AppCompatActivity {

    private EditText titulo, autor, tonalidad, letra_cancion;

    private Toolbar toolbar;

    private ImageView img;
    private Uri photoURI;
    private ImageButton fotos;
    int SELEC_IMAGEN = 200;
    int TOMAR_FOTO = 100;
    String CARPETA_RAIZ = "MisFotosApp";
    String CARPETAS_IMAGENES = "imagenes";
    String RUTA_IMAGEN = CARPETA_RAIZ + CARPETAS_IMAGENES;
    static String path;
    String nombreImagen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aniadir);

        titulo = findViewById(R.id.txtTitulo);
        autor = findViewById(R.id.txtAutor);
        tonalidad = findViewById(R.id.txtTonalidadM);
        letra_cancion = findViewById(R.id.txtLetra);

        fotos = findViewById(R.id.btnFoto);

        //Añadir la toolbar
        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        //Imagen y permisos
        img = findViewById(R.id.imageViewAniadir);


    }

    public void verImagenCompleta(View view){

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (path != null && fotoTomada == true){
                    Intent visorImagen = new Intent(activity_aniadir.this, verImagenCompleta.class);
                    visorImagen.putExtra("path", path);
                    startActivity(visorImagen);
                }else if(path == null || fotoTomada == false){
                    try {
                        Intent visorImagen = new Intent(activity_aniadir.this, verImagenCompleta.class);
                        visorImagen.putExtra("path", getRealPathFromURI(photoURI));
                        startActivity(visorImagen);
                    }catch (Exception n){
                        Toast.makeText(activity_aniadir.this, "DEBE AÑADIR IMÁGEN", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    //----------------------------------------------------------------------------------------------------------------------------------
    //MÉTODOS PARA CÁMARA (FOTO)

    //Conseguir el path de la imagen pasándole la URI
    private String getRealPathFromURI(Uri contentURI) throws Exception {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    boolean fotoTomada = false;
    public void tomarFoto() {
        fotoTomada = true;
        File fileImagen = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        boolean isCreada = fileImagen.exists();

        if(isCreada == false) {
            isCreada = fileImagen.mkdirs();
        }

        if(isCreada == true) {
            nombreImagen = (System.currentTimeMillis() / 1000) + ".jpg";
        }

        path = Environment.getExternalStorageDirectory()+File.separator+RUTA_IMAGEN+File.separator+nombreImagen;
        File imagen = new File(path);

        Intent intent = null;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authorities = this.getPackageName()+".provider";
            Uri imageUri = FileProvider.getUriForFile(this, authorities, imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }

        startActivityForResult(intent, TOMAR_FOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == SELEC_IMAGEN) {
            photoURI = data.getData();
            img.setImageURI(photoURI);
        } else if(resultCode == RESULT_OK && requestCode == TOMAR_FOTO) {
            MediaScannerConnection.scanFile(activity_aniadir.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String s, Uri uri) {

                }
            });
            Toast.makeText(this, path, Toast.LENGTH_LONG).show();

            Glide.with(this)
                    .load(path)
                    .into(img);

            /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            Bitmap bitmap = BitmapFactory.decodeFile(path,options);
            img.setImageBitmap(bitmap);*/
        }
    }

    public void hacerFoto(View view) {
        mostrarOpciones();
    }

    //Mostrar Opciones
    private void mostrarOpciones() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity_aniadir.this);
        builder.setTitle("Eleige una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option[which] == "Tomar foto"){
                    tomarFoto();
                }else if(option[which] == "Elegir de galeria"){
                    seleccionarImagen();
                }else {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    //SELECCIONAR LA IMÁGEN DE LA GALERÍA
    public void seleccionarImagen() {
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria, SELEC_IMAGEN);
        fotoTomada = false;
    }

//----------------------------------------------------------------------------------------------------------------------------------

    //MÉTODOS TOOLBAR Y MENU
    public boolean onCreateOptionsMenu(Menu mimenu){
        getMenuInflater().inflate(R.menu.menu_principal, mimenu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.home:
                    Intent volverHome = new Intent(this, MainActivity.class);
                    startActivity(volverHome);
                break;
            case R.id.mapa:
                Intent intent = new Intent(this, MapsActivity1.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                Toast.makeText(this, " -Se deben introducir los datos de la canción (título y letra son obligatorios)" +
                        "\n -Si se pulsa sobre la imágen se podrá ver en grande.", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    //MÉTODOS BASE DE DATOS

    public void insertarDatos(View view) {

        SQLiteBaseDeDatos cancion = new SQLiteBaseDeDatos(this,"MisCanciones",null,1);

        SQLiteDatabase BaseDeDatos = cancion.getWritableDatabase();

        String _titulo = titulo.getText().toString();
        String _autor = autor.getText().toString();
        String _tonalidad = tonalidad.getText().toString();
        String _letra_cancion = letra_cancion.getText().toString();
        String url_foto = "";

        if (path != null && fotoTomada == true){
            url_foto = path;
        }else if(path == null || fotoTomada == false){
            try {
                url_foto = getRealPathFromURI(photoURI);
            }catch(Exception e){
                //nada
            }
                
        }

        if (!_titulo.isEmpty() && !_letra_cancion.isEmpty()){
            ContentValues registros = new ContentValues();
            registros.put("titulo", _titulo);
            registros.put("autor",_autor);
            registros.put("tonalidad",_tonalidad);
            registros.put("letra_cancion",_letra_cancion);
            registros.put("foto",url_foto);

            try {
                BaseDeDatos.insertOrThrow("canciones", null, registros);

                BaseDeDatos.close();

                Toast.makeText(this, "Canción Guardada", Toast.LENGTH_SHORT).show();

            }catch(Exception s){
                Toast.makeText(this, "Esa canción ya existe!", Toast.LENGTH_SHORT).show();
                BaseDeDatos.close();
            }


            titulo.setText("");
            autor.setText("");
            tonalidad.setText("");
            letra_cancion.setText("");
            img.setImageResource(0);

        }else{
            Toast.makeText(this, "Debes introducir un Título y la Letra de la canción", Toast.LENGTH_SHORT).show();
        }


    }

}
