/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;


public class ProcesadorConexion extends Thread implements Observer{
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private Socket socketServicio;
	// stream de lectura y escritura
	private PrintWriter outPrinter;
        private BufferedReader inReader;
        //Mensajes recibidos para su procesamiento
        private MensajesRecibidos mensajes;
        
        private ListaUsuariosOnline usuariosOnline;
       

	// Constructor que tiene como parámetro una referencia al socket abierto en la conexión con el servidor

	public ProcesadorConexion(Socket socketServicio, MensajesRecibidos mensajes, ListaUsuariosOnline usuariosOnline) {
		this.socketServicio=socketServicio;
                this.mensajes = mensajes;
                
                this.usuariosOnline = usuariosOnline;
                
                // Abrimos flujos de lectura y escritura
                try{
                    
                this.outPrinter = new PrintWriter(this.socketServicio.getOutputStream(),true);
                this.inReader = new BufferedReader(new InputStreamReader(this.socketServicio.getInputStream()));
                
                }catch(IOException e){
                    System.out.print("No se pudieron abrir los flujos de entrada/salida");
                }
	}
	
	
	// Aquí es donde se realiza el procesamiento realmente:
        @Override
        public void run(){ //Esto lo hacemos porque la clase hebra tiene un método run() que es el que inicia la hebra

		String mensajeRecibido;
                boolean online = true;
                
                //Esto es importante. Apuntamos a la lista de observadores de mensajes al objeto ProcesadorConexión
                mensajes.addObserver(this);
                
                
                //Añadimos a la lista de observadores de usuarios a ésta conexión
                usuariosOnline.addObserver(this);
                
                do{
                    
                
		try {
			
                        
                            //Quedamos a la espera de leer el mensaje recibido
                           mensajeRecibido = inReader.readLine();
                           String[] cadena = mensajeRecibido.split("#");
                           String codigo = cadena[0];

                           
                           //Se podría haber hecho un switch. Cambiar si eso, ya que en el cliente sí que he hecho un switch
                           if(codigo.equals("1000")){
                               //CODIGO DE INFO CONEXIÓN PARA CREAR EL CLIENTE
                               ClienteConectado cliente = new ClienteConectado(cadena[1],cadena[2],Integer.parseInt(cadena[3]), this.socketServicio);
                               //Agregamos un cliente. Además, agregaCliente llama a NotifyObservers para modificar en los clientes los usuarios conectados
                               usuariosOnline.agregaCliente(cliente);
                               
                           }else if (codigo.equals("1001")){//CODIGO DE ENVIO DE MENSAJE A TODOS LOS CLIENTES
                                                              
                               mensajes.setMensaje(mensajeRecibido);
                               
                           }else if(codigo.equals("2000")){ //CODIGO DE LOGOUT
                               
                               usuariosOnline.eliminaCliente(cadena[1]);
                               
                               //Mandamos terminación al cliente para que cierre también él
                               this.outPrinter.println("2001");

                               online = false;
                           }
                                                      
                        
                                      
		}catch (IOException e) {
			System.err.println("Error al obtener los flujos de entrada/salida.");
                        online = false;//Si no se ha podido establecer conexión, cerramos streams y ponemos online a false
                        
                        try{ 
                            outPrinter.close();
                            inReader.close();
                            
                        }catch(IOException ex){
                            System.out.print("Error al cerrar flujos de entrada/salida");
                        }
		}

                }while(online);
                
                try{ //Una vez salimos, cerramos streams
                            outPrinter.close();
                            inReader.close();
                            this.socketServicio.close();
                        }catch(IOException ex){
                            System.out.print("Error al cerrar flujos de entrada/salida");
                        }
	}

        
        //Sobreescrito de la interfaz Observer
        @Override
        public void update(Observable o, Object arg){
            
            // Envia el mensaje al cliente
           outPrinter.println(arg.toString());
            
        }

}