package Cliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JTextField;

public class ConexionServidor implements ActionListener{
    private Socket socketConexion;
    private JTextField tfmensaje;
    private String nombre;

    //Flujos de entrada y salida
    private PrintWriter outPrinter;
    private BufferedReader inReader;


    public ConexionServidor(Socket socket, JTextField msg, String nombre){
        this.socketConexion=socket;
        this.tfmensaje = msg;
        this.nombre=nombre;

        //Intamos abrir flujos de entrada y salida (En este caso solo de salida, ya que solo vamos a mandar)
        //Recordar que el cliente se queda en bucle infinito esperando mensajes, y se sale del bucle para
        //poder mandar al pulsar Enviar con un ActionListener

        try{
            this.outPrinter = new PrintWriter(socket.getOutputStream(),true);

        }catch(IOException e){
            System.out.print("Error al abrir salida para mensaje");
        }
    }
    //Al pulsar en enviar, el evento hace que salga nuestro nombre en el chat, y nuestro mensaje, además, limpiamos el campo del mensaje para seguir añadiendo
    //Osea, al llamar al evento se va a ejectuar la función que le pasemos, ó la instancia que le pasemos y por supuesto
    //su método actionPerformed, el cual "hereda" de la interfaz ActionListener y que siempre ejecuta.
    @Override
    public void actionPerformed(ActionEvent e){

        this.outPrinter.println("1001#"+this.nombre +": "+ tfmensaje.getText()+"#");
        tfmensaje.setText("");
    }

}