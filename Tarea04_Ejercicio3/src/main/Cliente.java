package main;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.IOException;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;

public class Cliente {
	
	private static InputStream inputStream;
	private static OutputStream outputStream;
	
	private static String token = null;
    private static Map<Integer, String> estados = new HashMap<>();
    private static String estadoActual = "enviando";

	
	private static int puertoServidor = 1500;
	
    public static void main(String[] args) {
    	
		estados.put(0,"pausado");
		estados.put(1,"recibiendo");
		estados.put(2,"enviando");
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		
		try (Socket socket = new Socket("localhost", puertoServidor)) {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			
			enviarDatos("");
			while(true) {
				if(estadoActual.equals("enviando")) {
					String datos = sc.nextLine();
					enviarDatos(datos);	
				}
				if(estadoActual.equals("recibiendo")) {
					recibirDatos();
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {e.printStackTrace();}

			}
		
		} catch (IOException e) {
			System.out.println("No se ha podido establecer conexión con el servidor.");
		}
		

    }
    
	private static String recibirDatos() {
		String datosServidor = null;
		
		if(estadoActual.equals("enviando")) {
			System.out.println("ERROR: Interrupcion recibiendo datos.");
		} else {
			estadoActual = estados.get(1);

			try {
				// System.out.println(">>Esperando datos del Servidor...");
				
                while (true) {
                    
                	if (inputStream.available() > 0) {
 
                        byte[] buffer = new byte[1024];
                        int bytesRead = inputStream.read(buffer);
                        String respuesta = new String(buffer, 0, bytesRead);
        				datosServidor = respuesta;
        				
                        break;
                    } else {
                        
                    	// Dormir durante un breve período antes de volver a verificar
                        try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
                    }
                }
		         
			} catch (IOException e) {
				System.out.println("ERROR: No se ha podido conectar con el cliente.");
			}
			String modoCliente = datosServidor.split(";")[0];
			String mensaje = datosServidor.split(";")[1];
			
			
			if(modoCliente.equals("token")) {
				token = mensaje;
				modoCliente = "recibiendo";
				
			}
			
			System.out.println(">>Servidor: "+mensaje);
			estadoActual = modoCliente;
		}
		return datosServidor;
	}
	
	private static void enviarDatos(String mensaje) {
		if(estadoActual.equals("recibiendo")) {
			System.out.println("ERROR: No pueden recibirse datos en este momento.");
		} else {
				
			try {
				
				estadoActual = estados.get(2);
				String datos = token+";"+mensaje;
				outputStream.write(datos.getBytes());
				outputStream.flush();
				estadoActual = estados.get(1);
				
			} catch (IOException e) {
				System.out.println("ERROR: No se ha podido conectar con el servidor.");
				e.printStackTrace();
			}
		}
		
	}
    

	
}