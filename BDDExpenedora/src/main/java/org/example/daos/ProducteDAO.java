package org.example.daos;

import org.example.Model.Producte;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public interface ProducteDAO {

    public boolean createProducte(Producte p) throws SQLException;
    public boolean deleteProducte(Producte p);
    public boolean deleteProducte(String codiProducte);
    public boolean updateProducte(Producte p);
    public Producte readProducte() throws SQLException;
    public ArrayList<Producte> readProductes() throws SQLException;
    public Map<String,Producte> readProductesM();


}
