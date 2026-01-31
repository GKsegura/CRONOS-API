package application;

import api.ApiServer;
import database.TableCreator;

public class Main {
    public static void main(String[] args) {
        TableCreator.criarTabelas();
        System.out.println("=================================");
        System.out.println("   INICIANDO SERVIDOR CRONOS API");
        System.out.println("=================================");
        ApiServer.start();
    }
}