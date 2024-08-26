
package Cliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class CierreConexionServidor implements ActionListener {

    private String nombre;
    private Socket socketServicio;
    private PrintWriter outPrinter;



    CierreConexionServidor(Socket socketServicio, String nombre){
        this.socketServicio = socketServicio;
        this.nombre = nombre;

        try{
            this.outPrinter = new PrintWriter(socketServicio.getOutputStream(),true);

        }catch(IOException e){
            System.out.print("Error al abrir salida para mensaje");
        }


    }



    @Override
    public void actionPerformed(ActionEvent e){

        /*erramos sesión con el servidor*/
        this.outPrinter.println("2000#"+this.nombre +"#");

    }



}

