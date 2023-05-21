package org.example.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data @AllArgsConstructor @NoArgsConstructor
public class Producte {
    private String codiProducte;
    private String nom;
    private String descripcio;
    private float preuCompra;
    private float preuVenta;

    @NonNull
    public void update(Producte p){


    }

    /**
     * metoe que mostra les propietats de la classe producte utilitzant Gets
     */
    public void getAll(){
        System.out.println(getCodiProducte() + " | "+getNom()+" | "+getDescripcio()+" | "+getPreuVenta()+" | "+getPreuCompra());
    }
}
