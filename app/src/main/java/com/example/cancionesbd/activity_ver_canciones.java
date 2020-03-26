package com.example.cancionesbd;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class activity_ver_canciones extends AppCompatActivity implements RecyclerAdaptador.onCancionListener, RecyclerAdaptador.onCancionLongClickListener, RecyclerAdaptador.onImagenClickListener{

    private Toolbar toolbar;
    private RecyclerView listaConcionesRW;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private ArrayList<Item> ListaItems = new ArrayList<Item>();
    boolean isLongClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_canciones);

        toolbar = findViewById(R.id.toolbar1);

        setSupportActionBar(toolbar);

        //RecycleView
        listaConcionesRW = findViewById(R.id.recyclerViewCanciones);
        listaConcionesRW.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //Poner líneas divisorias a la lista
        //Poner en forma de lista
        manager = new LinearLayoutManager(this);
        listaConcionesRW.setLayoutManager(manager);

        adapter = new RecyclerAdaptador(this, GetArrayListItems(), this, this, this);
        listaConcionesRW.setAdapter(adapter);

    }

    //--------------------------------------------------------------------------------------------------

    @Override
    public void onCancionClick(int position) {
        if(isLongClick == false) {
            Intent intent = new Intent(this, Ver_Letra_Cancion.class);
            intent.putExtra("letra_cancion", ListaItems.get(position).getLetra_canción());
            startActivity(intent);
        }
        isLongClick = false;
    }

    @Override
    public boolean onCancionLongClicked(int position) {
        isLongClick = true;

        String titulo_cancion = ListaItems.get(position).getTitulo();
        eliminarCancion(titulo_cancion, position);

        return true;
    }

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

    @Override
    public void onImagenClickListener(int position) {
        if(isLongClick == false) {

            Uri photoURI = ListaItems.get(position).getUrl_photo();

                try {
                    Intent visorImagen = new Intent(activity_ver_canciones.this, verImagenCompleta.class);
                    visorImagen.putExtra("path", getRealPathFromURI(photoURI));
                    startActivity(visorImagen);
                }catch (Exception n){
                    Toast.makeText(activity_ver_canciones.this, "DEBE ACTUALIZAR IMÁGEN", Toast.LENGTH_SHORT).show();
                }
            }

        isLongClick = false;
    }

    //Método para eliminar la canción de la lista y de la base de datos
    public void eliminarCancion(final String titulo_cancion, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Eliminar");
        builder.setMessage("Estás seguro de que quieres eliminar esta Canción?").setCancelable(false).
                setPositiveButton("SÍ", new DialogInterface.OnClickListener() {

                    //DIÁLOGO PARA CONFIRMAR
                    public void onClick(DialogInterface dialog, int which) {

                        //DELETE
                        SQLiteBaseDeDatos cancion = new SQLiteBaseDeDatos(activity_ver_canciones.this, "MisCanciones", null,1);

                        SQLiteDatabase BaseDeDatos = cancion.getWritableDatabase();

                        int cantidad = BaseDeDatos.delete("canciones", "titulo='"+titulo_cancion+"'", null);

                        BaseDeDatos.close();

                        if (cantidad == 1){
                            ListaItems.remove(position);
                            adapter.notifyItemRangeChanged(position, ListaItems.size());
                            adapter.notifyDataSetChanged();
                            Toast.makeText(activity_ver_canciones.this, "Canción Eliminada", Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(activity_ver_canciones.this, "La canción no existe", Toast.LENGTH_SHORT).show();
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


    //MÉTODO PARA PASAR LOS DATOS AL RECYCLEVIEW
    private ArrayList<Item> GetArrayListItems(){


        //HACER CONSULTA GUARDANDO TODOS LOS DATOS DE LA BASE DE DATOS
        SQLiteBaseDeDatos cancion = new SQLiteBaseDeDatos(this, "MisCanciones",null,1);

        SQLiteDatabase BaseDeDatos = cancion.getWritableDatabase();

        Cursor fila = BaseDeDatos.rawQuery
                ("select foto, titulo, autor, tonalidad, letra_cancion  from canciones", null);

        if (fila.moveToFirst()){

            while(!fila.isAfterLast()){
                ListaItems.add(new Item(Uri.parse(fila.getString(0)),fila.getString(1),
                        fila.getString(2), fila.getString(3)
                            , fila.getString(4)));
                fila.moveToNext();
            }
            BaseDeDatos.close();
        }else{
            Toast.makeText(this, "DEBE AÑADIR UNA CANCIÓN.", Toast.LENGTH_LONG).show();
            BaseDeDatos.close();
        }

        return ListaItems;
    }
    //----------------------------------------------------------------------------------

    //TOOLBAR Y MENÚ
    //------------------------------------------------------------------------------------------------
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
                Toast.makeText(this, "-Al pulsar sobre la foto, esta se amplía.\n -Al mantener pulsado" +
                        " se eliminará la canción." +
                        "\n -Al pulsar el botón se mostrará la letra de la canción.", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    //------------------------------------------------------------------------------------------------


}
