package org.example;

import org.example.Model.Producte;
import org.example.daos.DAOFactory;
import org.example.daos.ProducteDAO;
import org.example.daos.ProducteDAO_MySQL;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static float benefici=0;
    public static void main(String[] args) throws SQLException {

        Scanner lector = new Scanner(System.in);            //TODO: passar Scanner a una classe InputHelper
        int opcio = 0;

        do {
            mostrarMenu();
            opcio = lector.nextInt();

            switch (opcio) {
                case 1:
                    mostrarMaquina();
                    break;
                case 2:
                    comprarProducte();
                    break;

                case 10:
                    mostrarInventari();
                    break;
                case 11:
                    afegirProductes();
                    break;
                case 12:
                    modificarMaquina();
                    break;
                case 13:
                    mostrarBenefici();
                    break;
                case 14:
                    restablirProducte();

                case -1:
                    System.out.println("Bye...");
                    break;
                default:
                    System.out.println("Opció no vàlida");
            }

        } while (opcio != -1);

    }

    /**
     * modificar maquina mostra un menu i dona les opcions de modificar les posicions dels productes,
     * l’estock d’un producte o afegir mes slots amb productes
     * @throws SQLException
     */
    private static void modificarMaquina() throws SQLException {
        Scanner llegir = new Scanner(System.in);
        /**
         * Ha de permetre:
         *      - modificar les posicions on hi ha els productes de la màquina (quin article va a cada lloc)
         *      - modificar stock d'un producte que hi ha a la màquina
         *      - afegir més ranures a la màquina
         */
        System.out.println("""
                Selecciona la operació a realitzar
                [1] Modificar les posicions dels productes
                [2] Modificar stock d'un producte
                [3] Afegir mes slots a la màquina
                
                [-1] Sortir
                """);
        int opcio = Integer.parseInt(llegir.nextLine());
        ProducteDAO_MySQL producte = new ProducteDAO_MySQL();
        switch (opcio) {
            case 1 -> producte.modificarPosicions();
            case 2 -> producte.modificarStock();
            case 3 -> producte.afegirSlots();
            case -1 -> System.out.println("Bye...");
            default -> System.out.println("Ups has introduit algo que no tocava");
        }
    }

    /**afegirProductes demana un producte i crea un objecte tipus producte amb les dades obtingudes
     * i crida al metode createProducte i li passa per parametre l'objecte tipus producte
     * i finalment llista la noav llista de productes amb el nou producte*/
    private static void afegirProductes() {
        Scanner llegir = new Scanner(System.in);
        System.out.println("Codi de producte: ");
        String codiProducte = llegir.nextLine();
        System.out.println("Nom producte: ");
        String nom = llegir.nextLine();
        System.out.println("Descripcio producte: ");
        String descripcio = llegir.nextLine();
        System.out.println("Preu compra producte: ");
        float preuCompra = Float.parseFloat(llegir.nextLine());
        System.out.println("Preu venta producte: ");
        float preuVenta = Float.parseFloat(llegir.nextLine());
        Producte p = new Producte(codiProducte, nom, descripcio, preuCompra, preuVenta);
        try {
            ProducteDAO_MySQL producteDAO = new ProducteDAO_MySQL();
            //Demanem de guardar el producte p a la BD
            producteDAO.createProducte(p);
            //Agafem tots els productes de la BD i els mostrem (per compvoar que s'ha afegit)
            ArrayList<Producte> productes = producteDAO.readProductes();
            for (Producte prod: productes) System.out.println(prod);
        } catch (SQLException e) {          //TODO: tractar les excepcions
            e.printStackTrace();
            System.out.println("Error en l'execucio: "+e.getErrorCode()+" "+e.getMessage());
        }

    }

    /**
     * mostrarInventari mostra tots els productes, utilitzant un metode getAll() de la casse Producte,
     * d'un array list tipus productes que obte els productes del metode readProductes()
     */
    private static void mostrarInventari() {

        try {
            System.out.println("""
                    Codi | Nom | Descripcio | Preu Compra | Preu Venta
                    ===========================================================
                    """);
            //Agafem tots els productes de la BD i els mostrem
            ProducteDAO_MySQL producte = new ProducteDAO_MySQL();
            ArrayList<Producte> productes = producte.readProductes();
            for (Producte prod: productes)
            {
                prod.getAll();
            }

        } catch (SQLException e) {          //TODO: tractar les excepcions
            e.printStackTrace();
            System.out.println("Error en l'execucio: "+e.getErrorCode()+" "+e.getMessage());
        }
    }

    private static void comprarProducte() throws SQLException {
        Scanner llegir = new Scanner(System.in);
        /**
         * Mínim: es realitza la compra indicant la posició on es troba el producte que es vol comprar
         * Ampliació (0.5 punts): es permet entrar el NOM del producte per seleccionar-lo (abans cal mostrar els
         * productes disponibles a la màquina)
         *
         * Tingueu en compte que quan s'ha venut un producte HA DE QUEDAR REFLECTIT a la BD que n'hi ha un menys.
         * (stock de la màquina es manté guardat entre reinicis del programa)
         */
        mostrarMaquina();
            int slot = Integer.parseInt(llegir.nextLine());
            ProducteDAO_MySQL producte = new ProducteDAO_MySQL();
            benefici += producte.restarProducte(slot);
            mostrarMaquina();
    }

    private static void mostrarMaquina() throws SQLException {

        /** IMPORTANT **
         * S'està demanat NOM DEL PRODUCTE no el codiProducte (la taula Slot conté posició, codiProducte i stock)
         * també s'acceptarà mostrant només el codi producte, però comptarà menys.
         *
         * Posicio      Producte                Quantitat disponible
         * ===========================================================
         * 1            Patates 3D              8
         * 2            Doritos Tex Mex         6
         * 3            Coca-Cola Zero          10
         * 4            Aigua 0.5L              7
         */

        ProducteDAO_MySQL producte = new ProducteDAO_MySQL();
        producte.readProducte();

    }

    private static void mostrarMenu() {
        System.out.println("\nMenú de la màquina expenedora");
        System.out.println("=============================");
        System.out.println("Selecciona la operació a realitzar introduïnt el número corresponent: \n");


        //Opcions per client / usuari
        System.out.println("[1] Mostrar Posició / Nom producte / Stock de la màquina");
        System.out.println("[2] Comprar un producte");

        //Opcions per administrador / manteniment
        System.out.println();
        System.out.println("[10] Mostrar llistat productes disponibles (BD)");
        System.out.println("[11] Afegir productes disponibles");
        System.out.println("[12] Assignar productes / stock a la màquina");
        System.out.println("[13] Mostrar benefici");
        System.out.println("[14] Restablir Productes");

        System.out.println();
        System.out.println("[-1] Sortir de l'aplicació");
    }

    /**
     * mostra la variable benefici que es va incrementant quan es fa una compra
     */
    private static void mostrarBenefici() { //TODO mostrar beneficis

        /** Ha de mostrar el benefici de la sessió actual de la màquina, cada producte té un cost de compra
         * i un preu de venda. La suma d'aquesta diferència de tots productes que s'han venut ens donaran el benefici.
         *
         * Simplement s'ha de donar el benefici actual des de l'últim cop que s'ha engegat la màquina. (es pot fer
         * amb un comptador de benefici que s'incrementa per cada venda que es fa)
         */

        /** AMPLIACIÓ **
         * En entrar en aquest menú ha de permetre escollir entre dues opcions: veure el benefici de la sessió actual o
         * tot el registre de la màquina.
         *
         * S'ha de crear una nova taula a la BD on es vagi realitzant un registre de les vendes o els beneficis al
         * llarg de la vida de la màquina.
         */
        System.out.println(benefici);
    }

    /**
     * actualitza la quantitat d’estock de la taula stock de tots els productes a 10 unitats
     * i mostra els productes actuals de la maquina
     * @throws SQLException
     */
    private static void restablirProducte() throws SQLException {
        ProducteDAO_MySQL producte = new ProducteDAO_MySQL();
        producte.restablirProductes();
        mostrarMaquina();
    }

}