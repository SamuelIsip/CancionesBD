package com.example.cancionesbd;

import android.net.Uri;

public class Item {

    private Uri url_photo;
    private String titulo, autor, tonalidad, letra_canción;

    public Item(Uri url_photo, String titulo, String autor, String tonalidad, String letra_canción) {
        this.url_photo = url_photo;
        this.titulo = titulo;
        this.autor = autor;
        this.tonalidad = tonalidad;
        this.letra_canción = letra_canción;
    }

    public Uri getUrl_photo() {
        return url_photo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getTonalidad() {
        return tonalidad;
    }

    public String getLetra_canción() {
        return letra_canción;
    }
}
