package org.example.daos;

public class DAOFactory {
    private static DAOFactory instance;
    private DAOFactory(){

    }
    public static DAOFactory getInstance(){
        if (instance == null)
            instance = new DAOFactory();
        return instance;
    }
}

