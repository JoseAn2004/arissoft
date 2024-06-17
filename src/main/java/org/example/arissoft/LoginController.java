package org.example.arissoft;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import conexion.ConexcionDB;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class LoginController {

    @FXML
    private Button botoniniciar;

    @FXML
    private PasswordField txtcontrasena;

    @FXML
    private TextField txtusuario;

    @FXML
    private Label lblmensaje;

    @FXML
    void LoginApplication(ActionEvent event) {
        String usuario = txtusuario.getText();
        String contrasena = txtcontrasena.getText();

        try {
            Connection conexion = ConexcionDB.conectar();
            if (conexion != null) {
                String consulta = "{ call LeerUsuarios(?, ?) }";
                CallableStatement statement = conexion.prepareCall(consulta);
                statement.setString(1, usuario);
                statement.setString(2, contrasena);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String nombre = resultSet.getString("NOMBRES");
                    String apellido = resultSet.getString("APELLIDO");
                    String rol = resultSet.getString("TIPO_USUARIO");
                    String correo = resultSet.getString("CORREO");
                    lblmensaje.setText("Inicio de sesión exitoso.");
                    String codigoVerificacion = generarCodigoVerificacion();
                    boolean enviado = enviarCodigoVerificacion(correo, codigoVerificacion);

                    if (enviado) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/arissoft/verification.fxml"));
                        Parent root = loader.load();
                        VerificationController controller = loader.getController();
                        controller.setUsuario(nombre, apellido, codigoVerificacion, rol);
                        Scene scene = new Scene(root);
                        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        primaryStage.setScene(scene);
                        primaryStage.centerOnScreen();
                        primaryStage.show();
                    }

                } else {
                    lblmensaje.setText("Credenciales incorrectas.");
                }
                resultSet.close();
                statement.close();
                conexion.close();
            } else {
                lblmensaje.setText("Error al conectar a la base de datos.");
            }
        } catch (SQLException e) {
            lblmensaje.setText("Error al realizar la consulta a la base de datos: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            lblmensaje.setText("Error al cargar la ventana de verificación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generarCodigoVerificacion() {
        int codigo = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(codigo);
    }

    private boolean enviarCodigoVerificacion(String correo, String codigo) {
        String remitente = "jca7492@gmail.com";
        String clave = "kebu okrq oryr sjts"; // Usa una contraseña de aplicación si tienes la verificación en dos pasos habilitada

        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.host", "smtp.gmail.com");
        propiedades.put("mail.smtp.port", "587");
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");

        Session sesion = Session.getInstance(propiedades, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
            mensaje.setSubject("Código de verificación");
            mensaje.setText("Tu código de verificación es: " + codigo);

            // Establecer el temporizador para la expiración del código
            TimerTask task = new TimerTask() {
                public void run() {
                    // Lógica para eliminar el código después de 3 minutos
                    System.out.println("El código ha expirado después de 3 minutos.");
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 180000); // 180000 milisegundos = 3 minutos

            Transport.send(mensaje);

            lblmensaje.setText("Código de verificación enviado.");
            return true;
        } catch (MessagingException e) {
            lblmensaje.setText("Error al enviar el código de verificación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
