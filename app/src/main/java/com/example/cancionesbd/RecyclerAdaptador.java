package com.example.cancionesbd;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerAdaptador extends  RecyclerView.Adapter{

    private Context context;
    private ArrayList<Item> listaItems;
    private onCancionListener myCancionListener;
    private onCancionLongClickListener myCancionLongListener;
    private onImagenClickListener myImagenListener;

    public RecyclerAdaptador(Context context, ArrayList<Item> listaItems, onCancionListener onCancionListener,
                             onCancionLongClickListener onCancionLongClickListener, onImagenClickListener myImagenListener) {
        this.context = context;
        this.listaItems = listaItems;

        this.myCancionListener = onCancionListener;

        this.myCancionLongListener = onCancionLongClickListener;

        this.myImagenListener = myImagenListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View contentView = LayoutInflater.from(context).inflate(R.layout.lista_canciones_row,null);
        return new Holder(contentView, myCancionListener,myCancionLongListener, myImagenListener);
    }

    //Interfaz para el click de boton del item
    public interface onCancionListener{
        void onCancionClick(int position);
    }

    //Interfaz para longClick de item
    public interface onCancionLongClickListener {
        boolean onCancionLongClicked(int position);
    }

    //Interfaz para clik en imagen de item
    public interface onImagenClickListener {
        void onImagenClickListener(int position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Item item = listaItems.get(position);

        Holder Holder = (Holder)holder;

        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;*/
        String url = item.getUrl_photo().toString();

        Glide.with(context)
                .load(url)
                .into(Holder.RViewImagen);


        //Holder.RViewImagen.setImageBitmap(BitmapFactory.decodeFile(item.getUrl_photo().toString(),options)); //Donde se crea el Bitmap con la URI

        Holder.txtTitulo.setText(item.getTitulo());
        Holder.txtAutor.setText(item.getAutor());
        Holder.txtTonalidad.setText(item.getTonalidad());

    }


    @Override
    public int getItemCount() {
        return listaItems.size();
    }


    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ImageView RViewImagen,btnVerLetraCancion;
        TextView txtTitulo, txtAutor, txtTonalidad;
        //ImageButton btnVerLetraCancion;

        onCancionListener onCancionListener;

        onCancionLongClickListener onCancionLongClickListener;

        onImagenClickListener onImagenListener;

        public Holder(@NonNull View itemView, onCancionListener onCancionListener, onCancionLongClickListener onCancionLongClickListener, onImagenClickListener onImagenListener) {
            super(itemView);

            RViewImagen = itemView.findViewById(R.id.RViewImagen);
            btnVerLetraCancion = itemView.findViewById(R.id.btnVerLetraCancion);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtAutor = itemView.findViewById(R.id.txtAutor);
            txtTonalidad = itemView.findViewById(R.id.txtTonalidad);

            this.onCancionListener = onCancionListener;

            btnVerLetraCancion.setOnClickListener(this);

            this.onCancionLongClickListener = onCancionLongClickListener;

            itemView.setOnLongClickListener(this);

            this.onImagenListener = onImagenListener;

            RViewImagen.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.RViewImagen:
                    onImagenListener.onImagenClickListener(getAdapterPosition());
                    break;
                case R.id.btnVerLetraCancion:
                    onCancionListener.onCancionClick(getAdapterPosition());
                    break;
            }

        }

        @Override
        public boolean onLongClick(View v) {
            onCancionLongClickListener.onCancionLongClicked(getAdapterPosition());
            return false;
        }

    }


}
