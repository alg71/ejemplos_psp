package main;

import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.BufferedReader;


public class HCliente extends Thread {
	private Socket clienteSocket;
	
	//Constructor para la conexion de clientes	
	public HCliente(Socket clienteSocket) {
		this.clienteSocket = clienteSocket;
	}
	
	@Override
	public void run() {
    	try {
    		boolean autorizado = false;
    		
    		while (!autorizado) {
    			
    			BufferedReader datosRecibidos = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
    			PrintWriter datosEnviados = new PrintWriter(clienteSocket.getOutputStream(), true);
        		
    			//Solicitamos Credenciales
        		datosEnviados.println("Ingrese su usuario:");
        		String usuarioRecibido = datosRecibidos.readLine();
        		System.out.println(usuarioRecibido);
        		
        		datosEnviados.println("Ingrese su contraseña:");
        		String pwRecibida = datosRecibidos.readLine();
        		System.out.println(pwRecibida);
        		
        		
        		int  auth = Servidor.checkAuth(usuarioRecibido,pwRecibida);
        		System.out.println(auth);
        		if(auth==2) autorizado = true;

        		if(auth == 0) {
        			datosEnviados.println("Usuario o contraseña incorrectos");
        		}
        		else if(auth == 1) {
        			datosEnviados.println("Este usuario ya esta loggeado");
        		}
        		else if(auth == 2) {
        			datosEnviados.println("Bienvenido");
   
        		}
			}
    		
    		while (true) {
    			Scanner datosRecibidos = new Scanner(clienteSocket.getInputStream());
    			PrintWriter datosEnviados = new PrintWriter(clienteSocket.getOutputStream(), true);
    			
    			datosEnviados.println("Seleccione nombre de archivo o escriba salir");
    			
    			String directorio= "./src/directorioFicheros/";
    			try (Stream<Path> miStream = Files.walk(Paths.get(directorio.toString()))) {
					List<Object> listaPaths = miStream.collect(Collectors.toList());
					
					for (Object path : listaPaths) {
						datosEnviados.println(path.toString());
					}
				}
    		}

    		
    		//String archivoRuta= "./src/directorioFicheros/"+archivoSolicitado;

    		/*
    		File archivo = new File(archivoRuta);
    		
    		//Comprobamos que el archivo exista
    		if (archivo.exists()) {
    			datosEnviados.println("El archivo Existe");
    			
        		//Si el archivo existe lo mandamos con este bucle
    		    try (FileInputStream fis = new FileInputStream(archivo)) {
    		        byte[] buffer = new byte[4096];
    		        int bytesRead;
    		        while ((bytesRead = fis.read(buffer)) != -1) {
    		        	clienteSocket.getOutputStream().write(buffer, 0, bytesRead);
    		        }
    		    }
				
			} else {
				//Si el archivo No Existe mandamos una notificacion y salimos
    			datosEnviados.println("El archivo No Existe");

			}
    		*/
    		
		} catch (Exception e) {e.printStackTrace();
	    
		} finally {
	        try {
				Thread.sleep(2000);
	            clienteSocket.close();
	            System.out.println("El cliente se ha desconectado.");
	        } catch (IOException | InterruptedException e) {e.printStackTrace();}
	    }
		
	}
}
