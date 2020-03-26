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
import android.database.sqlite.SQLiteDatabase;
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

public class activity_modificar extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText titulo, autor, tonalidad, letra_cancion;

    private ImageView img , botonBuscar;
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
        setContentView(R.layout.activity_modificar);

        botonBuscar = findViewById(R.id.btnBuscar);
        titulo = findViewById(R.id.txtBtitulo);
        autor = findViewById(R.id.txtAutor);
        tonalidad = findViewById(R.id.txtTonalidadM);
        letra_cancion = findViewById(R.id.txtLetraCnacionM);

        fotos = findViewById(R.id.btnFoto2);

        toolbar = findViewById(R.id.toolbar1);

        setSupportActionBar(toolbar);

        //Imagen y permisos
        img = findViewById(R.id.imagenViewModificar);


    }


    //----------------------------------------------------------------------------------------------------------------------------------
    //MÉTODOS PARA CÁMARA (FOTO)


    //Conseguir el path de la imagen pasándole la URI
    private String getRealPathFromURI(Uri contentURI) throws Exception{
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

    public void verImagenCompleta(View view){

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (path != null && fotoTomada == true){
                    Intent visorImagen = new Intent(activity_modificar.this, verImagenCompleta.class);
                    visorImagen.putExtra("path", path);
                    startActivity(visorImagen);
                }else if(path == null || fotoTomada == false){
                    try {
                        Intent visorImagen = new Intent(activity_modificar.this, verImagenCompleta.class);
                        visorImagen.putExtra("path", getRealPathFromURI(photoURI));
                        startActivity(visorImagen);
                    }catch (Exception n){
                        Toast.makeText(activity_modificar.this, "DEBE ACTUALIZAR IMÁGEN", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

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
            MediaScannerConnection.scanFile(activity_modificar.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String s, Uri uri) {

                }
            });
            Toast.makeText(this, path, Toast.LENGTH_LONG).show();
            Glide.with(this)
                    .load(path)
                    .into(img);
            /*Bitmap bitmap = BitmapFactory.decodeFile(path);
            img.setImageBitmap(bitmap);*/
        }
    }

    public void hacerFoto(View view) {
        mostrarOpciones();
    }


    //MOSTRAR VENTANA CON OPCIONES
    private void mostrarOpciones() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity_modificar.this);
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
                Toast.makeText(this, " -Debe introducir un título de canción existente para que al pulsar sobre la lupa" +
                        " se muestren los datos de esta.\n -Si pulsa sobre la imagen se verá en grande solo si la ha modificado." +
                        "\n -Se puede añadir foto, borrar canción o guardar.", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    //RELLENAR LOS CAMPOS (CONSULTA)
    public void modficarDatos(View view) {

        SQLiteBaseDeDatos cancion = new SQLiteBaseDeDatos(this, "MisCanciones",null,1);

        SQLiteDatabase BaseDeDatos = cancion.getWritableDatabase();

        String _titulo = titulo.getText().toString();

        if (!_titulo.isEmpty()){

            Cursor fila = BaseDeDatos.rawQuery
                    ("select autor, tonalidad, letra_cancion, foto from canciones where titulo='"+_titulo+"'", null);

            if (fila.moveToFirst()){
                autor.setText(fila.getString(0));
                tonalidad.setText(fila.getString(1));
                letra_cancion.setText(fila.getString(2));

                path = fila.getString(3);

                Glide.with(this)
                        .load(path)
                        .into(img);
                /*photoURI = Uri.parse(fila.getString(3));

                Bitmap bitmap = BitmapFactory.decodeFile(path);
                img.setImageBitmap(bitmap);*/

                BaseDeDatos.close();
            }else{
                Toast.makeText(this, "No existe esa canción.", Toast.LENGTH_SHORT).show();
                BaseDeDatos.close();
            }

        }else{
            Toast.makeText(this, "Debes introducir el Título de la canción.", Toast.LENGTH_SHORT).show();
        }

    }

    //ACTUALIZAR BD (UPDATE)
    public void actualizarDatos(View view) {

        SQLiteBaseDeDatos cancion = new SQLiteBaseDeDatos(this,"MisCanciones", null, 1);

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

        if (!_titulo.isEmpty() && !_autor.isEmpty() && !_tonalidad.isEmpty() && !_letra_cancion.isEmpty()){

            ContentValues registros = new ContentValues();

            registros.put("titulo", _titulo);
            registros.put("autor",_autor);
            registros.put("tonalidad",_tonalidad);
            registros.put("letra_cancion",_letra_cancion);
            registros.put("foto",url_foto);

            int cantidad = BaseDeDatos.update("canciones", registros,"titulo='"+_titulo+"'", null);

            BaseDeDatos.close();
            
            if (cantidad == 1){
                Toast.makeText(this, "Canción modificada correctamente", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "La canción no existe", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Debes rellenar todos los campos y añadir foto!", Toast.LENGTH_SHORT).show();
        }

    }

    //Método para eliminar una canción de la Base De Datos
    public void eliminarCancion(View view) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Eliminar");
        builder.setMessage("Estás seguro de que quieres eliminar esta Canción?").setCancelable(false).
                setPositiveButton("SÍ", new DialogInterface.OnClickListener() {

                    //DIÁLOGO PARA CONFIRMAR
            public void onClick(DialogInterface dialog, int which) {

                //DELETE

                SQLiteBaseDeDatos cancion = new SQLiteBaseDeDatos(activity_modificar.this, "MisCanciones", null,1);

                SQLiteDatabase BaseDeDatos = cancion.getWritableDatabase();

                String _titulo = titulo.getText().toString();

                if (!_titulo.isEmpty()){

                    int cantidad = BaseDeDatos.delete("canciones", "titulo='"+_titulo+"'", null);

                    BaseDeDatos.close();

                    titulo.setText("");
                    autor.setText("");
                    tonalidad.setText("");
                    letra_cancion.setText("");
                    img.setImageResource(0);

                    if (cantidad == 1){
                        Toast.makeText(activity_modificar.this, "Canción Eliminada", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(activity_modificar.this, "La canción no existe", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(activity_modificar.this, "Debes introducir el título de la canción a eliminar", Toast.LENGTH_SHORT).show();
                }



            }
        }).
                setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }
}
