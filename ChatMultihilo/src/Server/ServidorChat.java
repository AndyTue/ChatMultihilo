/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class ServidorChat{
	public static void main(String[] args) {
                
                //Identificación del servidor y parámetros
                int puerto = 1234;
                
                                
                ServerSocket servidor = null;
                Socket socketConexion = null;
                
                //Va a recibir mensajes de cualquier tipo. Ya sean peticiones ó los propios mensajes, luego los parseará
                MensajesRecibidos mensajes = new MensajesRecibidos();
                //Inicializamos la lista de usuarios online
                ListaUsuariosOnline usuariosOnline = new ListaUsuariosOnline();
                
                
                //Empezamos proceso de apertura del puerto de escucha y se queda a la espera de recibir conexiones
                
                try{
                    servidor = new ServerSocket(puerto);
                   
                    do{
                        
                        System.out.print("Servidor escuchando en el puerto "+ puerto+"\n");
                        System.out.print("Servidor a la espera de conexiones...\n");
                    
                        socketConexion = servidor.accept();
                        System.out.print("Conexión aceptada...");

                        //Creamos una conexión del cliente, donde se van a gestionar los flujos de entrada y salida de datos
                        ProcesadorConexion conexion = new ProcesadorConexion(socketConexion, mensajes, usuariosOnline);
                    
                        //iniciamos el proceso de recepción de mensajes desde la conexión. Además, conexión 
                        conexion.start();
                       }while(true);
                    
                }catch(IOException e){
                System.err.println("Error al escuchar en el puerto "+puerto);
                }
	}
}