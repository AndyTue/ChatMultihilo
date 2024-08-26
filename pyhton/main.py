import socket
import threading
from tkinter import *


class Cliente:
    def __init__(self):
        self.root = Tk()
        self.root.title("Cliente")

        # Configuración inicial
        self.nombre = ""
        self.host = "localhost"
        self.port = 1234
        self.online = False

        # Componentes de la GUI
        self.tfMensaje = Entry(self.root)
        self.tfLog = Text(self.root, state='disabled')
        self.bEnviar = Button(self.root, text="Enviar", command=self.enviar_mensaje)
        self.bLogout = Button(self.root, text="Logout", command=self.logout)
        self.lstUsuarios = Listbox(self.root)

        # Colocación en la ventana
        self.tfLog.grid(row=0, column=0, columnspan=2)
        self.lstUsuarios.grid(row=0, column=2)
        self.tfMensaje.grid(row=1, column=0)
        self.bEnviar.grid(row=1, column=1)
        self.bLogout.grid(row=1, column=2)

        # Configuración de la conexión
        self.socketServicio = None
        self.configuracion_inicial()

        # Iniciar la escucha de mensajes
        threading.Thread(target=self.recibir_mensajes).start()

        self.root.protocol("WM_DELETE_WINDOW", self.logout)
        self.root.mainloop()

    def configuracion_inicial(self):
        self.nombre = self.obtener_usuario()
        self.root.title(f"Usuario: {self.nombre}")

        try:
            self.socketServicio = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socketServicio.connect((self.host, self.port))
            self.agregar_log("Conexión establecida con el servidor.")
            self.enviar_datos_conexion()
        except Exception as e:
            self.agregar_log(f"No se pudo conectar con el servidor: {e}")

    def obtener_usuario(self):
        ventana = Toplevel(self.root)
        ventana.title("Configuración")
        ventana.grab_set()

        Label(ventana, text="Usuario:").pack()
        usuario_entry = Entry(ventana)
        usuario_entry.pack()

        def on_accept():
            self.nombre = usuario_entry.get()
            ventana.destroy()

        Button(ventana, text="Listo!", command=on_accept).pack()
        ventana.wait_window()

        return self.nombre

    def enviar_datos_conexion(self):
        info_conexion = f"1000#{self.nombre}#{self.socketServicio.getsockname()[0]}#{self.socketServicio.getsockname()[1]}#"
        self.enviar(info_conexion)

    def enviar(self, mensaje):
        try:
            self.socketServicio.sendall(mensaje.encode())
        except Exception as e:
            self.agregar_log(f"Error al enviar mensaje: {e}")

    def recibir_mensajes(self):
        self.online = True
        while self.online:
            try:
                mensaje = self.socketServicio.recv(1024).decode()
                if mensaje:  # Verificar si el mensaje no está vacío
                    self.procesar_mensaje(mensaje)
                else:
                    self.online = False  # Si se recibe un mensaje vacío, cerrar la conexión
            except Exception as e:
                self.agregar_log(f"Error al recibir mensaje: {e}")
                self.online = False

        self.cerrar_conexion()

    def procesar_mensaje(self, mensaje):
        try:
            partes = mensaje.split("#")
            codigo = partes[0]

            if codigo == "1001":  # Mensaje compartido en chat
                if len(partes) > 1:
                    self.agregar_log(" ".join(partes[1:]))
            elif codigo == "1002":  # Actualización de usuarios conectados
                self.lstUsuarios.delete(0, END)
                for i in range(1, len(partes), 3):
                    if i + 2 < len(partes):
                        usuario = f"{partes[i]} {partes[i + 1]} {partes[i + 2]}"
                        # Ya no filtramos el cliente actual
                        self.lstUsuarios.insert(END, usuario)
            elif codigo == "2001":  # Cierre de sesión desde el servidor
                self.online = False
            else:
                self.agregar_log(f"Mensaje no reconocido: {mensaje}")
        except IndexError:
            self.agregar_log(f"Error al procesar mensaje: {mensaje}")

    def agregar_log(self, mensaje):
        self.tfLog.config(state='normal')
        self.tfLog.insert(END, f"{mensaje}\n")
        self.tfLog.config(state='disabled')

    def enviar_mensaje(self):
        mensaje = self.tfMensaje.get()
        if mensaje:
            try:
                # Enviar el mensaje al servidor con el formato correcto
                self.socketServicio.sendall(f"1001#{self.nombre}: {mensaje}#".encode())
                self.tfMensaje.delete(0, END)
            except Exception as e:
                self.agregar_log(f"Error al enviar mensaje: {e}")

    def logout(self):
        if self.online:
            self.enviar(f"2001#{self.nombre}#")
            self.online = False
        self.root.destroy()

    def cerrar_conexion(self):
        if self.socketServicio:
            try:
                self.socketServicio.close()
                self.agregar_log("Conexión cerrada con el servidor.")
            except Exception as e:
                self.agregar_log(f"Error al cerrar la conexión: {e}")


if __name__ == "__main__":
    Cliente()

