package org.example.arissoft;

import SecondAplication.SecondAplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.io.IOException;

public class VerificationController {
    @FXML
    private Label lblUsuario;
    @FXML
    private TextField txtCodigo;
    @FXML
    private Button btnVerificar;
    @FXML
    private Label lblMensaje;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String codigoVerificacion;
    private String rolUsuario;

    public void setUsuario(String nombre, String apellido, String codigo, String rol) {
        this.nombreUsuario = nombre;
        this.apellidoUsuario = apellido;
        this.codigoVerificacion = codigo;
        this.rolUsuario = rol;
        lblUsuario.setText("Usuario: " + nombre + " " + apellido);
    }

    @FXML
    void verificarCodigo(ActionEvent event) {
        String codigoIngresado = txtCodigo.getText();
        if (codigoIngresado.equals(codigoVerificacion)) {
            lblMensaje.setText("Código correcto. Bienvenido!");
            abrirVentanaPorRol();
        } else {
            lblMensaje.setText("Código incorrecto. Por favor, inténtelo de nuevo.");
        }
    }

    private void abrirVentanaPorRol() {
        Stage stage = (Stage) btnVerificar.getScene().getWindow();
        String fxmlFile = "";

        if (rolUsuario.equals("Administrador")) {
            fxmlFile = "/org/example/arissoft/principal.fxml";
        } else if (rolUsuario.equals("Asistente")) {
            fxmlFile = "/org/example/arissoft/asistente.fxml";
        } else {
            lblMensaje.setText("Rol desconocido. Contacte al administrador.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Obtén el controlador de la nueva ventana
            if (rolUsuario.equals("Administrador")) {
                PrincipalApplication controller = loader.getController();
                controller.setUsuario(nombreUsuario, apellidoUsuario);
            } else if (rolUsuario.equals("Asistente")) {
                SecondAplication controller = loader.getController();
                controller.setUsuario(nombreUsuario, apellidoUsuario);
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        } catch (IOException e) {
            lblMensaje.setText("Error al cargar la ventana. Contacte al administrador.");
        }
    }


}
