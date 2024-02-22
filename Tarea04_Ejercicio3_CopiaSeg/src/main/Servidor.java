package main;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Servidor {
	
    static Map<String, String> usuarios = new HashMap<>();
    static Map<String, Boolean> logged = new HashMap<>();

	//Creamos un servidor con hilos para manejar varios clientes
	public static void main(String[] args) {
		int puertoEscucha = 1500;
		
	    usuarios.put("arturo", "secreta");
	    logged.put("arturo", false);
	    usuarios.put("armando", "bronca");
	    logged.put("armando", false);
	    usuarios.put("aitor", "menta");
	    logged.put("aitor", false);
		
		try(ServerSocket serverSocket = new ServerSocket(puertoEscucha)){
			
			System.out.println("Servidor esperando conexiones en el puerto " + puertoEscucha);
			
			while (true) {
					Socket clienteSocket = serverSocket.accept();
					System.out.println("Nuevo cliente conectado.");
	                
					HCliente hiloCliente = new HCliente(clienteSocket);
	                new Thread(hiloCliente).start();

			}
            	
		} catch (Exception e) {e.printStackTrace();}

	}
	
	public static int checkAuth(String user, String pw) {
		int checkAuth = 0;
		String credenciales = usuarios.get(user);
		if(credenciales == null ) checkAuth = 0;
		if(credenciales != null) {
			if(credenciales.equals(pw)) {
				if(checkLogged(user)) {checkAuth = 2;}
				else {checkAuth = 1;}
			}
		}
		return checkAuth;
	}
	private static boolean checkLogged(String user) {
		boolean checklogged = false;
		Boolean log = logged.get(user);
		if( log!=null ) {
			checklogged = !log;
			openSession(user);
		}
		return checklogged;
	}
	
	private static void openSession(String user) {
		logged.put(user, true);
	};
	private static void closeSession(String user) {
		logged.put(user, false);
	};

}
