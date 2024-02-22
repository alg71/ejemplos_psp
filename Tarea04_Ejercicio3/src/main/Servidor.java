package main;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Servidor {
	
    static Map<String, String> usuarios = new HashMap<>();
    static Map<String, String> token = new HashMap<>();

	//Creamos un servidor con hilos para manejar varios clientes
	public static void main(String[] args) {
		int puertoEscucha = 1500;
		
	    usuarios.put("arturo", "secreta");
	    token.put("arturo", null);
	    
	    usuarios.put("armando", "bronca");
	    token.put("armando", null);
	    
	    usuarios.put("aitor", "menta");
	    token.put("aitor", null);
		
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
	
	public static int checkAuth(String user, String pw, HCliente hc) {
		
		int checkAuth = 0;
		
		String usuario = user.split(";")[1];
		String password = pw.split(";")[1];
		
		String credenciales = usuarios.get(usuario);
		
		if(credenciales != null) {
			
			if(credenciales.equals(password)) {
				checkAuth =  1;
				if((token.get(usuario) == null)) {
					openSession(usuario, hc);
					checkAuth = 2;
				}	
			}
		}
	
		return checkAuth;
	}
	
	
	private static void openSession(String user, HCliente hc) {
		int hash = Math.abs((int) (user.hashCode()*Math.random()));
		hc.setToken(Integer.toString(hash));
		hc.setUser(user);

		token.put(user, String.valueOf(hash));
	};
	public static void closeSession(String user, HCliente hc) {

		hc.setToken(null);
		token.put(user, null);
	};

}
