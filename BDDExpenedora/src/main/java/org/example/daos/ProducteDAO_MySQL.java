package org.example.daos;

import org.example.Model.Producte;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class ProducteDAO_MySQL implements ProducteDAO {
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_ROUTE = "jdbc:mysql://localhost:3306/expenedora";
    private static final String DB_USER = "root";
    private static final String DB_PWD = "1234";

    private Connection conn;
    public ProducteDAO_MySQL(){
        try {
            Class.forName(DB_DRIVER);
            conn = DriverManager.getConnection(DB_ROUTE, DB_USER, DB_PWD);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /**createProducte obte un producte que se li passa per parametre i inserta a la taula producte
     * el producte que se li passa per parametre
     * @param p Producte que es passa per parametre
     * @return false
     * @throws SQLException
     */
    @Override
    public boolean createProducte(Producte p) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO producte VALUES(?,?,?,?,?)");
        ps.setString(1, p.getCodiProducte());
        ps.setString(2, p.getNom());
        ps.setString(3, p.getDescripcio());
        ps.setFloat(4, p.getPreuCompra());
        ps.setFloat(5, p.getPreuVenta());

        ps.executeUpdate();

        return false;
    }

    @Override
    public boolean deleteProducte(Producte p) {
        return false;
    }

    @Override
    public boolean deleteProducte(String codiProducte) {
        return false;
    }

    @Override
    public boolean updateProducte(Producte p) {
        return false;
    }

    @Override
    public Producte readProducte() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("select * from slot");
        ResultSet rs =ps.executeQuery();
        System.out.println("Posicio      Producte                Quantitat disponible");
        System.out.println("===========================================================");
        while (rs.next()) {
            String v_nom = rs.getString(3);
            PreparedStatement ps2 = conn.prepareStatement("select nom from producte where codi_producte=?");
            ps2.setString(1,v_nom);
            ResultSet rs2 = ps2.executeQuery();
            rs2.next();
            System.out.println(rs.getString(1)+"           "+rs2.getString(1)+"                "+rs.getString(2));
        }
        return null;
    }

    /**
     *  readProductes es conecta a la taula producte de la base de dades i obte totes les
     *  caracteristiques de tots els productes i els guarda a un array list tipus producte
     * @return retorna un ArrayList tipus Producte amb tots els productes
     * @throws SQLException
     */
    @Override
    public ArrayList<Producte> readProductes() throws SQLException {
        ArrayList<Producte> llistaProductes= new ArrayList<Producte>();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM producte");

        ResultSet rs =ps.executeQuery();

        while (rs.next()){
            Producte p = new Producte();

            p.setCodiProducte(rs.getString(1));
            p.setNom(rs.getString(2));
            p.setDescripcio(rs.getString(3));
            p.setPreuCompra(4);
            p.setPreuVenta(5);


            llistaProductes.add(p);
        }

        return llistaProductes;
    }

    @Override
    public Map<String, Producte> readProductesM() {
        return null;
    }

    /**
     * El metode restar producte obte la quantitat d'un producte segons l'eslot que s'hagi passat per parametre
     * i se li restara 1 a la quantitat i s'executara la funcio sumarBenefici passant per paramatre l'eslot,
     * en cas que la quantitat sigui 0 no es vendra el producte
     * @param slot integer que se li passa al programa representant un slot de la maquina
     * @return retorna un benefici
     * @throws SQLException
     */
    public float restarProducte(int slot) throws SQLException {
        PreparedStatement ps2 = conn.prepareStatement("select quantitat from slot where posicio=?");
        ps2.setInt(1,slot);
        ResultSet rs2 = ps2.executeQuery();
        rs2.next();
        String quantitat=rs2.getString(1);
        if (!quantitat.equals("0")){
        PreparedStatement ps = conn.prepareStatement("update slot set quantitat=quantitat-1 where posicio=?");
        ps.setInt(1,slot);
        ps.executeUpdate();
        //-------------------------------------------------------------------------------------------------------------------------------------
            return sumarBenefici(slot);
        }else{
            System.out.println("No queden mes unitats d'aquest producte");
            return 0;
        }
    }

    /**
     * Aqueset metode obte el codi producte segons la posicio de la taula slot i utilitzant aquest codi s'obte de la taula producte
     * el preu de compra i el preu de venta i amb aquest dos es calcula el benefici i es retorna
     * @param slot integer que se li passa al programa representant un slot de la maquina
     * @return retorna un float benefici
     * @throws SQLException
     */
    public float sumarBenefici(int slot) throws SQLException {
        //Obtenim el codi del producte
        PreparedStatement psCodi = conn.prepareStatement("select codi_producte from slot where posicio=?");
        psCodi.setInt(1,slot);
        ResultSet rsCodi = psCodi.executeQuery();
        rsCodi.next();
        String codi = rsCodi.getString(1);
        //Utilitzam el codi del producte per obtenir el seu preu de venta i compra
        PreparedStatement psBen = conn.prepareStatement("select preu_copmra, preu_venta from producte where codi_producte=?");

        psBen.setString(1,codi);
        ResultSet rsBen = psBen.executeQuery();
        rsBen.next();
        float compra = rsBen.getFloat(1);
        float venta = rsBen.getFloat(2);
        return venta-compra;
    }

    /**
     * restablir producte obte la quantitat de productes i assigna la seva quantitat a 10 unitats
     * segons la seva posicio
     * @throws SQLException
     */
    public void restablirProductes() throws SQLException {
        PreparedStatement ps2 = conn.prepareStatement("select count(posicio) from slot");
        ResultSet rs2 = ps2.executeQuery();
        rs2.next();
        for (int i = 1; (i<rs2.getInt(1)+1);i++){
            PreparedStatement ps = conn.prepareStatement("update slot set quantitat=10 where posicio=?");
            ps.setInt(1,i);
            int rs = ps.executeUpdate();
            System.out.println(rs);
        }
    }

    /**
     * modificarPosicions assigna una posicio diferent a un producte segons la posicio que te actualment
     * @throws SQLException
     */
    public void modificarPosicions() throws SQLException {
        Scanner llegir = new Scanner(System.in);
        PreparedStatement ps = conn.prepareStatement("update slot set posicio=? where posicio=?");
        System.out.println("A quin slot esta el producte que vols moure");
        int posicioVella = Integer.parseInt(llegir.nextLine());
        ps.setInt(2,posicioVella);
        System.out.println("A quin slot el vols moure? 1-30");
        int posicioNova = Integer.parseInt(llegir.nextLine());
        ps.setInt(1,posicioNova);
        ps.executeUpdate();
    }

    /**
     * modificarStock modificar l'estock d'un producte segons la seva posicio
     * @throws SQLException
     */
    public void modificarStock() throws SQLException {
        Scanner llegir = new Scanner(System.in);
        PreparedStatement ps = conn.prepareStatement("update slot set quantitat=? where posicio=?");
        System.out.println("Posicio del producte per modificar la quantitat");
        int posicio = Integer.parseInt(llegir.nextLine());
        ps.setInt(2,posicio);
        System.out.println("Nova quantitat de stock del producte");
        int quantitat = Integer.parseInt(llegir.nextLine());
        ps.setInt(1,quantitat);
        int rs = ps.executeUpdate();
        System.out.println(rs);
    }

    /**
     * afegirSlots afegeir 1 slot a la taula slot a la posicio que es desitgi
     * @throws SQLException
     */
    public void afegirSlots() throws SQLException {
        Scanner llegir = new Scanner(System.in);
        PreparedStatement ps = conn.prepareStatement("insert into slot values (?,?,?)");
        System.out.println("Posicio del producte");
        int posicio = Integer.parseInt(llegir.nextLine());
        ps.setInt(1,posicio);
        System.out.println("Codi del producte");
        String codi = llegir.nextLine();
        ps.setString(3,codi);
        System.out.println("Quantitat del producte");
        int quantitat = Integer.parseInt(llegir.nextLine());
        ps.setInt(2,quantitat);

        int rs = ps.executeUpdate();
    }
}
