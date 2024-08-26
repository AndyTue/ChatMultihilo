/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.net.Socket;

public class ClienteConectado {
    private String nombre;
    private String direccion;
    private int port;
    
    //Hemos de guardar tambien el socket, para hacer mensajes privados
        
    private Socket socketCliente;
    
    public ClienteConectado(String nombre, String direccion, int port, Socket socketCliente){
        
        this.nombre = nombre;
        this.direccion = direccion;
        this.port = port;
        this.socketCliente = socketCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public int getPort() {
        return port;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public Socket getSocketCliente() {
        return socketCliente;
    }
    
    
}
