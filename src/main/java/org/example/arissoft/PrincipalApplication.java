package org.example.arissoft;

import conexion.ConexcionDB;
import javafx.application.Application;
import javafx.css.converter.StringConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalDateStringConverter;

import java.io.File;


public class PrincipalApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PrincipalApplication.class.getResource("principal.fxml"));
        fxmlLoader.setController(this);
        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
        stage.setTitle("ARIS_SOFT_1.0!");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private Tab taboperarios;
    @FXML
    private TabPane tabPane;

    @FXML
    private void handleOperariosButtonClick(ActionEvent event) {
        tabPane.getSelectionModel().select(taboperarios);
    }

    @FXML
    private Tab tabinicio;

    @FXML
    private void handleinicioButtonClick(ActionEvent event) {
        tabPane.getSelectionModel().select(tabinicio);
    }

    @FXML
    private Tab tabmaquinarias;

    @FXML
    private void handleMaquinariasButtonClick(ActionEvent event) {
        tabPane.getSelectionModel().select(tabmaquinarias);
    }

    @FXML
    private Tab tabclientes;

    @FXML
    private void handleClientesButtonClick(ActionEvent event) {
        tabPane.getSelectionModel().select(tabclientes);
    }

    @FXML
    private Tab tabgastos;

    @FXML
    private void handleGastosButtonClick(ActionEvent event) {
        tabPane.getSelectionModel().select(tabgastos);
    }

    @FXML
    private Tab tabreportes;

    @FXML
    private void handleReportesButtonClick(ActionEvent event) {
        tabPane.getSelectionModel().select(tabreportes);
    }

    @FXML
    private Button btnsalir;

    @FXML
    private void handleSalirButtonClick(ActionEvent event) {
        Stage stage = (Stage) btnsalir.getScene().getWindow();
        stage.close();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
            Stage loginStage = new Stage();
            loginStage.setTitle("INICIO SESION");
            loginStage.setScene(scene);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //------------------------------------------------------------------------------------------------------------------

    @FXML
    private ComboBox<String> txtelegirmaquinaria;
    @FXML
    private ComboBox<String> boxestadoini;
    @FXML
    private DatePicker txtfechainicio;
    @FXML
    private DatePicker txtfechatermino;
    @FXML
    private ComboBox<String> txtoperador;

    @FXML
    private TextField txthoras;
    @FXML
    private TextField txthoras_realizar;
    @FXML
    private TextField txtdni;
    @FXML
    private TextField txtnombres;


    @FXML
    private TextField txttotal;

    private void cargarMaquinarias() {
        String sql = "SELECT NOMBRE FROM MAQUINARIAS";

        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            txtelegirmaquinaria.getItems().clear();
            while (resultSet.next()) {
                String nombreMaquinaria = resultSet.getString("NOMBRE");
                txtelegirmaquinaria.getItems().add(nombreMaquinaria);
            }
            txtelegirmaquinaria.setOnAction(e -> {
                String maquinariaSeleccionada = txtelegirmaquinaria.getValue();
                System.out.println("Maquinaria seleccionada: " + maquinariaSeleccionada);
                // Si necesitas el ID de la maquinaria seleccionada, puedes usar un Map para asociar nombres con IDs
                // Integer idSeleccionado = getKeyByValue(mapMaquinarias, maquinariaSeleccionada);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void Cargaroperariosini() {
        Map<String, String> operariosMap = new HashMap<>();
        String sql = "SELECT ID_OPERARIOS, NOMBRES, APELLIDOS FROM OPERARIOS";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String id = resultSet.getString("ID_OPERARIOS");
                String nombre = resultSet.getString("NOMBRES").trim(); // Eliminar espacios en blanco adicionales
                String apellido = resultSet.getString("APELLIDOS").trim(); // Eliminar espacios en blanco adicionales
                String nombreCompleto = nombre + " " + apellido;
                operariosMap.put(nombreCompleto, id);
            }
            txtoperador.getItems().clear();
            txtoperador.getItems().addAll(operariosMap.keySet());
            txtoperador.setOnAction(e -> {
                String nombreCompletoSeleccionado = txtoperador.getValue();
                String idOperarioAsociado = operariosMap.get(nombreCompletoSeleccionado);
                System.out.println("ID de operario seleccionado: " + idOperarioAsociado);
            });

        } catch (SQLException e) {
            System.err.println("Error al cargar los nombres de los operarios: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void cargarCostoPorHora() {
        String maquinariaSeleccionada = (String) txtelegirmaquinaria.getSelectionModel().getSelectedItem();
        if (maquinariaSeleccionada == null) {
            return;
        }

        String sql = "SELECT COSTO_POR_HORA FROM MAQUINARIAS WHERE nombre = ?";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, maquinariaSeleccionada);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    double costoPorHora = resultSet.getDouble("COSTO_POR_HORA");
                    txthoras.setText(String.valueOf(costoPorHora));
                } else {
                    txthoras.setText("");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarClientePorDNI() {
        String dni = txtdni.getText();
        if (dni == null || dni.trim().isEmpty()) {
            txtnombres.setText("");
            return;
        }

        String sql = "SELECT NOMBRE, APELLIDO FROM CLIENTES WHERE DNI = ?";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, dni);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String nombre = resultSet.getString("NOMBRE");
                    String apellido = resultSet.getString("APELLIDO");
                    txtnombres.setText(nombre +" "+ apellido);
                } else {
                    txtnombres.setText("Cliente no encontrado");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while loading client details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void calcularTotal() {
        String costoPorHoraStr = txthoras.getText();
        String horasRealizarStr = txthoras_realizar.getText();

        if (costoPorHoraStr == null || costoPorHoraStr.trim().isEmpty() || horasRealizarStr == null || horasRealizarStr.trim().isEmpty()) {
            txttotal.setText("");
            return;
        }

        try {
            double costoPorHora = Double.parseDouble(costoPorHoraStr);
            double horasRealizar = Double.parseDouble(horasRealizarStr);
            double total = costoPorHora * horasRealizar;
            txttotal.setText(String.valueOf(total));
        } catch (NumberFormatException e) {
            txttotal.setText("Error en los valores ingresados");
        }
    }

    private void cargarDescripcionesEstados() {
        Map<String, String> estadosMap = new HashMap<>();
        String sql = "SELECT ID_ESTADO, DESCRIPCION FROM ESTADO_TRABAJO_MAQUINARIA";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String id = resultSet.getString("ID_ESTADO");
                String descripcion = resultSet.getString("DESCRIPCION");
                estadosMap.put(descripcion, id);
            }
            boxestadoini.getItems().clear();
            boxestadoini.getItems().addAll(estadosMap.keySet());
            boxestadoini.setOnAction(e -> {
                String descripcionSeleccionada = boxestadoini.getValue();
                String idEstadoAsociado = estadosMap.get(descripcionSeleccionada);
                System.out.println("ID de estado seleccionado: " + idEstadoAsociado);
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //-----------------------------------------------------------------------------------------------------------------------
    //PAGE MAJOR
    @FXML
    private TextField txtusuario_registro;
    @FXML
    private TableView<NuevoRegistro> tablainicio;

    @FXML
    private TableColumn<NuevoRegistro, String> id_tabini;
    @FXML
    private TableColumn<NuevoRegistro, String> usuario_tabini;
    @FXML
    private TableColumn<NuevoRegistro, String> maquina_tabini;
    @FXML
    private TableColumn<NuevoRegistro, String> inicio_tabini;
    @FXML
    private TableColumn<NuevoRegistro, String> fin_tabini;
    @FXML
    private TableColumn<NuevoRegistro, String> operador_tabini;
    @FXML
    private TableColumn<NuevoRegistro, String> horas_tabini;
    @FXML
    private TableColumn<NuevoRegistro, String> dni_tabini;
    @FXML
    private TableColumn<NuevoRegistro, String> cliente_tabini;
    @FXML
    private TableColumn<NuevoRegistro, String> estado_tabini;
    @FXML
    private TableColumn<NuevoRegistro, Double> total_tabini;

    @FXML
    private void ingreso_nuevo_registro() {
        StringBuilder errores = new StringBuilder();

        // Validación de campos
        String maquinaria = String.valueOf(txtelegirmaquinaria.getValue());
        if (maquinaria == null || maquinaria.isEmpty()) {
            errores.append("El campo maquinaria es obligatorio.\n");
        }

        LocalDate fechaInicio = txtfechainicio.getValue();
        if (fechaInicio == null) {
            errores.append("El campo fecha de inicio es obligatorio.\n");
        }
        String fechaInicioFormateada = fechaInicio != null ? fechaInicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";

        LocalDate fechaTermino = txtfechatermino.getValue();
        if (fechaTermino == null) {
            errores.append("El campo fecha de término es obligatorio.\n");
        }
        String fechaTerminoFormateada = fechaTermino != null ? fechaTermino.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";

        String operadorNombreCompleto = String.valueOf(txtoperador.getValue());
        if (operadorNombreCompleto == null || operadorNombreCompleto.isEmpty()) {
            errores.append("El campo operador es obligatorio.\n");
        } else if (!operadorNombreCompleto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            errores.append("El campo operador debe contener solo letras.\n");
        }

        String horasTrabajo = txthoras_realizar.getText();
        if (horasTrabajo == null || horasTrabajo.isEmpty()) {
            errores.append("El campo horas de trabajo es obligatorio.\n");
        } else if (!horasTrabajo.matches("[0-9]+")) {
            errores.append("El campo horas de trabajo debe contener solo números.\n");
        }

        String dni = txtdni.getText();
        if (dni == null || dni.isEmpty()) {
            errores.append("El campo DNI es obligatorio.\n");
        }

        String usuarioNombreCompleto = txtusuario_registro.getText();
        if (usuarioNombreCompleto == null || usuarioNombreCompleto.isEmpty()) {
            errores.append("El campo usuario es obligatorio.\n");
        } else if (!usuarioNombreCompleto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            errores.append("El campo usuario debe contener solo letras.\n");
        }

        String clienteNombreCompleto = txtnombres.getText();
        if (clienteNombreCompleto == null || clienteNombreCompleto.isEmpty()) {
            errores.append("El campo cliente es obligatorio.\n");
        } else if (!clienteNombreCompleto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            errores.append("El campo cliente debe contener solo letras.\n");
        }

        String estado = boxestadoini.getValue();
        if (estado == null || estado.isEmpty()) {
            errores.append("El campo estado es obligatorio.\n");
        }

        String totalText = txttotal.getText();
        if (totalText == null || totalText.isEmpty()) {
            errores.append("El campo total es obligatorio.\n");
        }
        double total = 0;
        if (totalText != null && !totalText.isEmpty()) {
            try {
                total = Double.parseDouble(totalText);
            } catch (NumberFormatException e) {
                errores.append("El campo total debe ser un número válido.\n");
            }
        }

        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(null, errores.toString(), "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String sql = "{CALL InsertarRegistroAlquilerMaquinaria(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection connection = ConexcionDB.conectar();
             CallableStatement statement = connection.prepareCall(sql)) {
            statement.setString(1, maquinaria);
            statement.setString(2, fechaInicioFormateada);
            statement.setString(3, fechaTerminoFormateada);
            statement.setString(4, operadorNombreCompleto);
            statement.setString(5, horasTrabajo);
            statement.setString(6, dni);
            statement.setDouble(7, total);
            statement.setString(8, usuarioNombreCompleto);
            statement.setString(9, clienteNombreCompleto);
            statement.setString(10, estado);
            statement.executeUpdate();
            System.out.println("Datos insertados correctamente.");
            limpiaCampos();
            cargarregistro();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void limpiaCampos() {
        txtelegirmaquinaria.setValue(null);
        txtfechainicio.setValue(null);
        txtfechatermino.setValue(null);
        txtoperador.setValue(null);
        txthoras_realizar.setText("");
        txtdni.setText("");
        txtnombres.setText("");
        boxestadoini.setValue(null);
        txttotal.setText("");
        txthoras.setText("");
    }


    private void cargarregistro() {
        List<NuevoRegistro> registro = new ArrayList<>();
        String sql = "EXEC LeerRegistroAlquilerMaquinaria";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("ID_RegistroAlq");
                String maquinariaId = resultSet.getString("Maquinaria");
                String fechaInicio = resultSet.getString("FECHA_INICIO");
                String fechaTermino = resultSet.getString("FECHA_TERMINO");
                String operador = resultSet.getString("Operador");
                String horasTrabajo = resultSet.getString("HORAS_TRABAJO");
                String dni = resultSet.getString("DNI");
                double total = resultSet.getDouble("TOTAL");
                String usuarios = resultSet.getString("Usuario");
                String cliente = resultSet.getString("Cliente");
                String estadoId = resultSet.getString("Estado");
                registro.add(new NuevoRegistro(id, maquinariaId, fechaInicio, fechaTermino, operador, horasTrabajo, dni, total, usuarios, cliente, estadoId));
            }
            tablainicio.getItems().setAll(registro);
            cargarReportes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private NuevoRegistro registroSeleccionadoAnterior = null;

    @FXML
    private void seleccionarregistro() {
        NuevoRegistro registroseleccionar = tablainicio.getSelectionModel().getSelectedItem();
        if (registroseleccionar!= null &&!registroseleccionar.equals(registroSeleccionadoAnterior)) {
            registroSeleccionadoAnterior = registroseleccionar;

            if (registroseleccionar.getMaquinariaId() == null || registroseleccionar.getOperador() == null || registroseleccionar.getEstadoId() == null) {
                JOptionPane.showMessageDialog(null, "Faltan datos necesarios para modificar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Salir del método si faltan datos necesarios
            }

            JOptionPane.showMessageDialog(null, "Registro seleccionado.", "Información", JOptionPane.INFORMATION_MESSAGE);
            // Asignar valores a los componentes de la interfaz
            txtelegirmaquinaria.setValue(registroseleccionar.getMaquinariaId());
            txtoperador.setValue(registroseleccionar.getOperador());
            boxestadoini.setValue(registroseleccionar.getEstadoId());
            txtfechainicio.setValue(LocalDate.parse(registroseleccionar.getFechaInicio()));
            txtfechatermino.setValue(LocalDate.parse(registroseleccionar.getFechaTermino()));
            txtusuario_registro.setText(registroseleccionar.getUsuarios());
            txthoras_realizar.setText(registroseleccionar.getHorasTrabajo());
            txtdni.setText(registroseleccionar.getDni());
            txttotal.setText(String.valueOf(registroseleccionar.getTotal()));
            habilitarEdicion(true);
        }
    }

    private void habilitarEdicion(boolean habilitar) {
        txtelegirmaquinaria.setDisable(!habilitar);
        txtoperador.setDisable(!habilitar);
        boxestadoini.setDisable(!habilitar);
        txtfechainicio.setDisable(!habilitar);
        txtfechatermino.setDisable(!habilitar);
        txtusuario_registro.setDisable(!habilitar);
        txthoras_realizar.setDisable(!habilitar);
        txtdni.setDisable(!habilitar);
        txttotal.setDisable(!habilitar);
    }


    private void modificarRegistro(NuevoRegistro nuevoregistro) {
        String sql = "{CALL ActualizarRegistroAlqMaquinaria(?,?,?,?,?,?,?,?,?,?,?)}";
        try (Connection connection = ConexcionDB.conectar();
             CallableStatement statement = connection.prepareCall(sql)) {

            // Establecer los parámetros para el procedimiento almacenado
            statement.setInt(1, nuevoregistro.getId());
            statement.setDate(2, java.sql.Date.valueOf(nuevoregistro.getFechaInicio()));
            statement.setDate(3, java.sql.Date.valueOf(nuevoregistro.getFechaTermino()));
            statement.setString(4, nuevoregistro.getMaquinariaId());
            statement.setString(5, nuevoregistro.getOperador());
            statement.setBigDecimal(6, new BigDecimal(nuevoregistro.getHorasTrabajo()));
            statement.setString(7, nuevoregistro.getDni());
            statement.setBigDecimal(8, BigDecimal.valueOf(nuevoregistro.getTotal()));
            statement.setString(9, nuevoregistro.getUsuarios());
            statement.setString(10, nuevoregistro.getCliente());
            statement.setString(11, nuevoregistro.getEstadoId());

            // Ejecutar el procedimiento almacenado y verificar el resultado
            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("INFO: Registro actualizado correctamente.");
                JOptionPane.showMessageDialog(null, "Registro actualizado correctamente.", "Confirmación", JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.err.println("ERROR: No se pudo actualizar el registro.");
                JOptionPane.showMessageDialog(null, "No se pudo actualizar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("ERROR SQL al actualizar el registro: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al actualizar el registro: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    private void modificarRegistro() {
        NuevoRegistro registroseleccionar = tablainicio.getSelectionModel().getSelectedItem();
        if (registroseleccionar != null) {
            String maquinariaId = txtelegirmaquinaria.getValue();
            String operador = txtoperador.getValue();
            String estadoId = boxestadoini.getValue();

            // Verifica que los valores no sean null antes de asignarlos al registro seleccionado
            if (maquinariaId != null && !maquinariaId.isEmpty()) {
                registroseleccionar.setMaquinariaId(maquinariaId);
            } else {
                System.err.println("ERROR: No se ha seleccionado maquinaria.");
                JOptionPane.showMessageDialog(null, "Debe seleccionar una maquinaria.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (operador != null && !operador.isEmpty()) {
                registroseleccionar.setOperador(operador);
            } else {
                System.err.println("ERROR: No se ha seleccionado operador.");
                JOptionPane.showMessageDialog(null, "Debe seleccionar un operador.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (estadoId != null && !estadoId.isEmpty()) {
                registroseleccionar.setEstadoId(estadoId);
            } else {
                System.err.println("ERROR: No se ha seleccionado estado.");
                JOptionPane.showMessageDialog(null, "Debe seleccionar un estado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            registroseleccionar.setFechaInicio(txtfechainicio.getValue() != null ? txtfechainicio.getValue().toString() : null);
            registroseleccionar.setFechaTermino(txtfechatermino.getValue() != null ? txtfechatermino.getValue().toString() : null);
            registroseleccionar.setHorasTrabajo(txthoras_realizar.getText());
            registroseleccionar.setDni(txtdni.getText());
            registroseleccionar.setUsuarios(txtusuario_registro.getText());
            registroseleccionar.setCliente(txtnombres.getText());

            try {
                registroseleccionar.setTotal(Double.parseDouble(txttotal.getText()));
            } catch (NumberFormatException e) {
                System.err.println("ERROR: El total no es un número válido.");
                JOptionPane.showMessageDialog(null, "El campo total debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("INFO: Datos actualizados correctamente. Llamando a modificarRegistro(NuevoRegistro)...");
            modificarRegistro(registroseleccionar); // Llama al método para modificar el registro en la base de datos
            actualizarComboBoxDespuesModificacion(registroseleccionar); // Actualiza los ComboBox después de modificar
            cargarregistro(); // Actualiza la tabla u otra vista de datos
            limpiarCampos(); // Limpia los campos después de la modificación
        } else {
            System.err.println("ERROR: No se ha seleccionado ningún registro para modificar.");
            JOptionPane.showMessageDialog(null, "No se ha seleccionado ningún registro para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarComboBoxDespuesModificacion(NuevoRegistro registro) {
        txtelegirmaquinaria.setValue(registro.getMaquinariaId());
        txtoperador.setValue(registro.getOperador());
        boxestadoini.setValue(registro.getEstadoId());
    }

    private void limpiarCampos() {
        txtelegirmaquinaria.setValue(null);
        txtoperador.setValue(null);
        boxestadoini.setValue(null);
        txtfechainicio.setValue(null);
        txtfechatermino.setValue(null);
        txthoras_realizar.clear();
        txthoras.clear();
        txtdni.clear();
        txtnombres.clear();
        txttotal.clear();
    }




//----------------------------------------------------------------------------------------------------------------------

    @FXML
    private TextField nomopera;

    @FXML
    private TextField apelliopera;

    @FXML
    private TextField dniopera;
    @FXML
    private TextField direccopera;

    @FXML
    private TextField teleopera;

    @FXML
    private TextField licenopera;

    @FXML
    private TextField cargoopera;

    @FXML
    private TextField sueldoopera;

    @FXML
    private void guardarOpera() {

        String nombre = nomopera.getText();
        String apellido = apelliopera.getText();
        String dni = dniopera.getText();
        String direccion = direccopera.getText();
        String telefono = teleopera.getText();
        String licencia = licenopera.getText();
        String cargo = cargoopera.getText();
        Double sueldo = Double.parseDouble(sueldoopera.getText());

        String sql = "INSERT INTO OPERARIOS (NOMBRES, APELLIDOS,DNI , DIRECCION,TELEFONO, NUMERO_DE_LICENCIA ,CARGO,SUELDO) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, apellido);
            preparedStatement.setString(3, dni);
            preparedStatement.setString(4, direccion);
            preparedStatement.setString(5, telefono);
            preparedStatement.setString(6, licencia);
            preparedStatement.setString(7, cargo);
            preparedStatement.setDouble(8, sueldo);
            preparedStatement.executeUpdate();
            System.out.println("Datos insertados correctamente.");
            nomopera.clear();
            apelliopera.clear();
            dniopera.clear();
            direccopera.clear();
            teleopera.clear();
            licenopera.clear();
            cargoopera.clear();
            sueldoopera.clear();
            cargarOpe();
            Cargaroperariosini();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TableView<Operarios> tablaoperarios;
    @FXML
    private TableColumn<Operarios, String> idtab;
    @FXML
    private TableColumn<Operarios, String> nomtab;
    @FXML
    private TableColumn<Operarios, String> apetab;
    @FXML
    private TableColumn<Operarios, String> dnitab;
    @FXML
    private TableColumn<Operarios, String> direcctab;
    @FXML
    private TableColumn<Operarios, String> tetab;
    @FXML
    private TableColumn<Operarios, String> licetab;
    @FXML
    private TableColumn<Operarios, String> cargtab;
    @FXML
    private TableColumn<Operarios, Double> sueltab;


    private void cargarOpe() {
        List<Operarios> operarios = new ArrayList<>();
        String sql = "SELECT * FROM OPERARIOS";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("ID_OPERARIOS");
                String nombre = resultSet.getString("NOMBRES");
                String apellido = resultSet.getString("APELLIDOS");
                String direccion = resultSet.getString("DIRECCION");
                String cargo = resultSet.getString("CARGO");
                String telefono = resultSet.getString("TELEFONO");
                String dni = resultSet.getString("DNI");
                String licencia = resultSet.getString("NUMERO_DE_LICENCIA");
                double sueldo = resultSet.getDouble("SUELDO");
                operarios.add(new Operarios(id, nombre, apellido, dni, direccion, telefono, licencia, cargo, sueldo));
            }
            tablaoperarios.getItems().setAll(operarios);
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void opeselec() {
        Operarios OperarioSeleccionada = tablaoperarios.getSelectionModel().getSelectedItem();
        if (OperarioSeleccionada != null) {
            nomopera.setText(OperarioSeleccionada.getNombre());
            apelliopera.setText(OperarioSeleccionada.getApellidos());
            dniopera.setText(OperarioSeleccionada.getDni());
            direccopera.setText(OperarioSeleccionada.getDireccion());
            cargoopera.setText(OperarioSeleccionada.getDireccion());
            teleopera.setText(OperarioSeleccionada.getTelefono());
            licenopera.setText(OperarioSeleccionada.getLicencia());
            sueldoopera.setText(Double.toString(OperarioSeleccionada.getSueldo()));
        }
    }


    private void modificarOperarios(Operarios Operario) {
        String sql = "UPDATE OPERARIOS SET NOMBRES=?, APELLIDOS=?, DIRECCION=?, CARGO=?, TELEFONO=?, SUELDO=?, DNI=?, NUMERO_DE_LICENCIA=? WHERE ID_OPERARIOS=?";

        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, Operario.getNombre());
            preparedStatement.setString(2, Operario.getApellidos());
            preparedStatement.setString(3, Operario.getDireccion());
            preparedStatement.setString(4, Operario.getCargo());
            preparedStatement.setString(5, Operario.getTelefono());
            preparedStatement.setDouble(6, Operario.getSueldo());
            preparedStatement.setString(7, Operario.getDni());
            preparedStatement.setString(8, Operario.getLicencia());
            preparedStatement.setInt(9, Operario.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void modificarOperarios() {
        Operarios OperarioSeleccionada = tablaoperarios.getSelectionModel().getSelectedItem();
        if (OperarioSeleccionada != null) {
            OperarioSeleccionada.setNombre(nomopera.getText());
            OperarioSeleccionada.setApellidos(apelliopera.getText());
            OperarioSeleccionada.setDni(dniopera.getText());
            OperarioSeleccionada.setDireccion(direccopera.getText());
            OperarioSeleccionada.setTelefono(teleopera.getText());
            OperarioSeleccionada.setLicencia(licenopera.getText());
            OperarioSeleccionada.setCargo(cargoopera.getText());
            OperarioSeleccionada.setSueldo(Double.parseDouble(sueldoopera.getText()));
            modificarOperarios(OperarioSeleccionada);

            nomopera.clear();
            apelliopera.clear();
            dniopera.clear();
            direccopera.clear();
            teleopera.clear();
            licenopera.clear();
            cargoopera.clear();
            sueldoopera.clear();
            cargarOpe();
        }
    }

    /*SANTOS CONTINUA*/
    //
    //
    //


    //------------------------------------------------------------------------------------------------------------------
    @FXML
    private TextField txtnombremaquinaria;
    @FXML
    private TextField txtdescripcionmaquinaria;
    @FXML
    private TextField txtcostoporhora;

    @FXML
    private void guardarDatos() {
        String nombre = txtnombremaquinaria.getText();
        String descripcion = txtdescripcionmaquinaria.getText();
        double costoPorHora = Double.parseDouble(txtcostoporhora.getText());
        String sql = "INSERT INTO MAQUINARIAS (NOMBRE, DESCRIPCION, COSTO_POR_HORA) VALUES (?, ?, ?)";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, descripcion);
            preparedStatement.setDouble(3, costoPorHora);
            preparedStatement.executeUpdate();
            System.out.println("Datos insertados correctamente.");
            txtnombremaquinaria.clear();
            txtdescripcionmaquinaria.clear();
            txtcostoporhora.clear();
            cargarDatos();
            cargarMaquinarias();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TableView<Maquinaria> tablamaquinarias;
    @FXML
    private TableColumn<Maquinaria, String> cl_maquina;
    @FXML
    private TableColumn<Maquinaria, String> nombre_maquina;
    @FXML
    private TableColumn<Maquinaria, String> descripcion_maquina;
    @FXML
    private TableColumn<Maquinaria, Double> costo_maquina;


    public void setUsuario(String nombre, String apellido) {
        txtusuario_registro.setText(nombre +" "+ apellido);

    }



    public void initialize() {
        txtdni.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!= null) {
                if (!newValue.matches("\\d*")) {
                    txtdni.setText(oldValue);
                } else if (newValue.length() > 8) {
                    txtdni.setText(oldValue);
                } else if (newValue.length() == 8) {
                    txtdni.setEditable(false);
                }
            }
        });




        cl_maquina.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombre_maquina.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcion_maquina.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        costo_maquina.setCellValueFactory(new PropertyValueFactory<>("costoPorHora"));
        cargarDatos();

        cl_clientes.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombre_cliente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellido_cliente.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        dni_cliente.setCellValueFactory(new PropertyValueFactory<>("dni"));
        direccion_cliente.setCellValueFactory(new PropertyValueFactory<>("direccionc"));
        telefono_cliente.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        cargardatosclientes();

        cl_gasto.setCellValueFactory(new PropertyValueFactory<>("id"));
        cl_nombre.setCellValueFactory(new PropertyValueFactory<>("nombr"));
        cl_apellido.setCellValueFactory(new PropertyValueFactory<>("apellid"));
        cl_descripcion.setCellValueFactory(new PropertyValueFactory<>("descrip"));
        cl_total.setCellValueFactory(new PropertyValueFactory<>("tot"));
        cargartb_gastos();

        idtab.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomtab.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apetab.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        dnitab.setCellValueFactory(new PropertyValueFactory<>("dni"));
        direcctab.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        tetab.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        licetab.setCellValueFactory(new PropertyValueFactory<>("licencia"));
        cargtab.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        sueltab.setCellValueFactory(new PropertyValueFactory<>("sueldo"));
        cargarOpe();

        //DATOS BOX
        cargarMaquinarias();
        Cargaroperariosini();
        txtelegirmaquinaria.setOnAction(event -> cargarCostoPorHora());

        txtdni.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                cargarClientePorDNI();
            }
        });
        txtdni.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                cargarClientePorDNI();
            }
        });

        txthoras.textProperty().addListener((observable, oldValue, newValue) -> calcularTotal());
        txthoras_realizar.textProperty().addListener((observable, oldValue, newValue) -> calcularTotal());

        cargarDescripcionesEstados();


        id_tabini.setCellValueFactory(new PropertyValueFactory<>("id"));
        maquina_tabini.setCellValueFactory(new PropertyValueFactory<>("maquinariaId"));
        inicio_tabini.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        fin_tabini.setCellValueFactory(new PropertyValueFactory<>("fechaTermino"));
        operador_tabini.setCellValueFactory(new PropertyValueFactory<>("operador"));
        horas_tabini.setCellValueFactory(new PropertyValueFactory<>("horasTrabajo"));
        dni_tabini.setCellValueFactory(new PropertyValueFactory<>("dni"));
        total_tabini.setCellValueFactory(new PropertyValueFactory<>("total"));
        usuario_tabini.setCellValueFactory(new PropertyValueFactory<>("usuarios"));
        cliente_tabini.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        estado_tabini.setCellValueFactory(new PropertyValueFactory<>("estadoId"));
        cargarregistro();

        id_tabre.setCellValueFactory(new PropertyValueFactory<>("idReporte"));
        fecha_tabre.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        concepto_tabre.setCellValueFactory(new PropertyValueFactory<>("concepto"));
        tipo_tabre.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        monto_tabre.setCellValueFactory(new PropertyValueFactory<>("monto"));
        cargarReportes();


    }

    private void cargarDatos() {
        List<Maquinaria> maquinarias = new ArrayList<>();
        String sql = "SELECT * FROM MAQUINARIAS";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("ID_MAQUINARIAS");
                String nombre = resultSet.getString("NOMBRE");
                String descripcion = resultSet.getString("DESCRIPCION");
                double costoPorHora = resultSet.getDouble("COSTO_POR_HORA");
                maquinarias.add(new Maquinaria(id, nombre, descripcion, costoPorHora));
            }
            tablamaquinarias.getItems().setAll(maquinarias);
            cargarMaquinarias();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void seleccionarMaquinaria() {
        Maquinaria maquinariaSeleccionada = tablamaquinarias.getSelectionModel().getSelectedItem();
        if (maquinariaSeleccionada != null) {
            txtnombremaquinaria.setText(maquinariaSeleccionada.getNombre());
            txtdescripcionmaquinaria.setText(maquinariaSeleccionada.getDescripcion());
            txtcostoporhora.setText(Double.toString(maquinariaSeleccionada.getCostoPorHora()));
        }
    }

    private void modificarMaquinaria(Maquinaria maquinaria) {
        String sql = "UPDATE MAQUINARIAS SET NOMBRE=?, DESCRIPCION=?, COSTO_POR_HORA=? WHERE ID_MAQUINARIAS=?";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, maquinaria.getNombre());
            preparedStatement.setString(2, maquinaria.getDescripcion());
            preparedStatement.setDouble(3, maquinaria.getCostoPorHora());
            preparedStatement.setInt(4, maquinaria.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void modificarMaquinaria() {
        Maquinaria maquinariaSeleccionada = tablamaquinarias.getSelectionModel().getSelectedItem();
        if (maquinariaSeleccionada != null) {
            maquinariaSeleccionada.setNombre(txtnombremaquinaria.getText());
            maquinariaSeleccionada.setDescripcion(txtdescripcionmaquinaria.getText());
            maquinariaSeleccionada.setCostoPorHora(Double.parseDouble(txtcostoporhora.getText()));
            modificarMaquinaria(maquinariaSeleccionada);
            txtnombremaquinaria.clear();
            txtdescripcionmaquinaria.clear();
            txtcostoporhora.clear();
            cargarDatos();

        }
    }

    @FXML
    private TextField txtnombrecliente;
    @FXML
    private TextField txtapellidocliente;
    @FXML
    private TextField txtdnicliente;
    @FXML
    private TextField txtdireccioncliente;
    @FXML
    private TextField txttelefonoliente;

    @FXML
    private void addclients() {
        String nombre = txtnombrecliente.getText();
        String apellido = txtapellidocliente.getText();
        int dni = Integer.parseInt(txtdnicliente.getText());
        String direccionc = txtdireccioncliente.getText();
        int telefono = Integer.parseInt(txttelefonoliente.getText());

        String sql = "INSERT INTO CLIENTES (NOMBRE, APELLIDO, DNI, DIRECCION, TELEFONO) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, apellido);
            preparedStatement.setDouble(3, dni);
            preparedStatement.setString(4, direccionc);
            preparedStatement.setInt(5, telefono);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Cliente agregado corectamente");
            txtnombrecliente.clear();
            txtapellidocliente.clear();
            txtdnicliente.clear();
            txtdireccioncliente.clear();
            txttelefonoliente.clear();
            cargardatosclientes();
            cargarClientePorDNI();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TableView<Clientes> tabla_clientes;
    @FXML
    private TableColumn<Clientes, String> cl_clientes;
    @FXML
    private TableColumn<Clientes, String> nombre_cliente;
    @FXML
    private TableColumn<Clientes, String> apellido_cliente;
    @FXML
    private TableColumn<Clientes, String> dni_cliente;
    @FXML
    private TableColumn<Clientes, String> direccion_cliente;
    @FXML
    private TableColumn<Clientes, String> telefono_cliente;


    private void cargardatosclientes() {
        List<Clientes> cliente = new ArrayList<>();
        String sql = "SELECT * FROM CLIENTES";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("ID_CLIENTES");
                String nombre = resultSet.getString("NOMBRE");
                String apellido = resultSet.getString("APELLIDO");
                int dni = resultSet.getInt("DNI");
                String direccionc = resultSet.getString("DIRECCION");
                int telefono = resultSet.getInt("TELEFONO");
                cliente.add(new Clientes(id, nombre, apellido, dni, direccionc, telefono));
            }
            tabla_clientes.getItems().setAll(cliente);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void seleccionarCliente() {
        Clientes clienteSeleccionado = tabla_clientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado != null) {
            txtnombrecliente.setText(clienteSeleccionado.getNombre());
            txtapellidocliente.setText(clienteSeleccionado.getApellido());
            txtdnicliente.setText(Integer.toString(clienteSeleccionado.getDni()));
            txtdireccioncliente.setText(clienteSeleccionado.getDireccionc());
            txttelefonoliente.setText(Integer.toString(clienteSeleccionado.getTelefono()));
        }
    }

    private void modificar_clientes(Clientes cliente) {
        String sql = "UPDATE CLIENTES SET NOMBRE=?, APELLIDO=?, DNI=?, DIRECCION=?, TELEFONO=? WHERE ID_CLIENTES=?";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cliente.getNombre());
            preparedStatement.setString(2, cliente.getApellido());
            preparedStatement.setInt(3, cliente.getDni());
            preparedStatement.setString(4, cliente.getDireccionc());
            preparedStatement.setInt(5, cliente.getTelefono());
            preparedStatement.setInt(6, cliente.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void modificarclientes() {
        Clientes clienteseleccionado = tabla_clientes.getSelectionModel().getSelectedItem();
        if (clienteseleccionado != null) {
            clienteseleccionado.setNombre(txtnombrecliente.getText());
            clienteseleccionado.setApellido(txtapellidocliente.getText());
            clienteseleccionado.setDni(Integer.parseInt(txtdnicliente.getText()));
            clienteseleccionado.setDireccionc(txtdireccioncliente.getText());
            clienteseleccionado.setTelefono(Integer.parseInt(txttelefonoliente.getText()));
            modificar_clientes(clienteseleccionado);
            txtnombrecliente.clear();
            txtapellidocliente.clear();
            txtdnicliente.clear();
            txtdireccioncliente.clear();
            txttelefonoliente.clear();
            cargardatosclientes();

        }
    }

    @FXML
    private TextField nombr;
    @FXML
    private TextField apellid;
    @FXML
    private TextArea descrip;
    @FXML
    private TextField tot;

    @FXML
    private void agregargasto() {
        try (Connection connection = ConexcionDB.conectar()) {
            String nombre = nombr.getText();
            String apellido = apellid.getText();
            String total = tot.getText();
            String descripcion = descrip.getText();

            String sql = "{CALL InsertarGasto(?,?,?,?)}";
            CallableStatement statement = connection.prepareCall(sql);
            statement.setString(1, nombre);
            statement.setString(2, apellido);
            statement.setBigDecimal(3, new BigDecimal(total));
            statement.setString(4, descripcion);
            statement.executeUpdate();

            nombr.clear();
            apellid.clear();
            descrip.clear();
            tot.clear();
            cargartb_gastos();
            cargarReportes();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al insertar el registro en la base de datos.");
        }
    }


    @FXML
    private TableView<Gastos> tabla_gasto;
    @FXML
    private TableColumn<Gastos, String> cl_gasto;
    @FXML
    private TableColumn<Gastos, String> cl_nombre;
    @FXML
    private TableColumn<Gastos, String> cl_apellido;
    @FXML
    private TableColumn<Gastos, String> cl_descripcion;
    @FXML
    private TableColumn<Gastos, String> cl_total;

    private void cargartb_gastos() {
        List<Gastos> gastoss = new ArrayList<>();
        String sql = "SELECT * FROM GASTOS";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("ID_GASTOS");
                String nombr = resultSet.getString("NOMBRE");
                String apellid = resultSet.getString("APELLIDO");
                String descrip = resultSet.getString("DESCRIPCION");
                double total = resultSet.getDouble("TOTAL");

                gastoss.add(new Gastos(id, nombr, apellid, descrip, total));
            }
            tabla_gasto.getItems().setAll(gastoss);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void seleccionarRegistro() {
        Gastos gastoseleccionado = tabla_gasto.getSelectionModel().getSelectedItem();
        if (gastoseleccionado != null) {
            nombr.setText(gastoseleccionado.getNombr());
            apellid.setText(gastoseleccionado.getApellid());
            descrip.setText(gastoseleccionado.getDescrip());
            tot.setText(Double.toString(gastoseleccionado.getTot()));

        }
    }

//DE ACA PARA ABJO MODIFICA ESTO NOMAS PARA CORRER AVER SI FUNCIONA POR QUE CON ERROR NO CORRE

    private void modificar_gastos(Gastos gastos) {
        String sql = "UPDATE GASTOS SET NOMBRE=?, APELLIDO=?, DESCRIPCION=?, TOTAL=? WHERE ID_GASTOS=?";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, gastos.getNombr());
            preparedStatement.setString(2, gastos.getApellid());
            preparedStatement.setString(3, gastos.getDescrip());
            preparedStatement.setDouble(4, gastos.getTot());
            preparedStatement.setInt(5, gastos.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarRegistro() {
        Gastos gastoSeleccionado = tabla_gasto.getSelectionModel().getSelectedItem();

        if (gastoSeleccionado != null) {
            try {
                gastoSeleccionado.setNombr(nombr.getText());
                gastoSeleccionado.setApellid(apellid.getText());
                gastoSeleccionado.setDescrip(descrip.getText());
                gastoSeleccionado.setTot(Double.parseDouble(tot.getText()));
                modificar_gastos(gastoSeleccionado);
                nombr.setText("");
                apellid.setText("");
                descrip.setText("");
                tot.setText("");
                cargartb_gastos();
            } catch (NumberFormatException e) {
                System.out.println("Error: El campo total no contiene un valor numérico válido.");
            }
        }
    }



    @FXML
    private TableView<Reportes> tabla_reporte;
    @FXML
    private TableColumn<Reportes, String> id_tabre;
    @FXML
    private TableColumn<Reportes, String> fecha_tabre;
    @FXML
    private TableColumn<Reportes, String> concepto_tabre;
    @FXML
    private TableColumn<Reportes, String> tipo_tabre;
    @FXML
    private TableColumn<Reportes, String> monto_tabre;

    @FXML
    private TextField txtingre;
    @FXML
    private TextField txtegre;
    @FXML
    private TextField txtdifere;

    private void cargarReportes() {
        List<Reportes> reportes = new ArrayList<>();
        double totalIngresos = 0.0;
        double totalEgresos = 0.0;
        String sql = "exec leerreportes";
        try (Connection connection = ConexcionDB.conectar();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id_tabre = resultSet.getInt("ID_REPORTES");
                String fechaTabre = resultSet.getString("FECHA");
                String concepto_tabre = resultSet.getString("CONCEPTO");
                String tipo_tabre = resultSet.getString("TIPO");
                double monto_tabre = Double.parseDouble(resultSet.getString("MONTO"));

                if (tipo_tabre.equals("Ingreso")) {
                    totalIngresos += monto_tabre;
                } else if (tipo_tabre.equals("Egreso")) {
                    totalEgresos += monto_tabre;
                }
                reportes.add(new Reportes(id_tabre, fechaTabre, concepto_tabre, tipo_tabre, monto_tabre));
            }
            tabla_reporte.getItems().setAll(reportes);
            // Mostrar
            txtingre.setText(String.valueOf(totalIngresos));
            txtegre.setText(String.valueOf(totalEgresos));
            // diferencia entre ingresos y egresos
            double diferencia = totalIngresos - totalEgresos;
            txtdifere.setText(String.valueOf(diferencia));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button btn_excel;


    @FXML
    private void ExportButtonAction() {

        FileChooser fileChooser = new FileChooser();

        // Establecer un título para la ventana del FileChooser
        fileChooser.setTitle("Guardar reporte");

        // Establecer un filtro de extensión para archivos .xlsx
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos Excel (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        // Mostrar la ventana de guardado y obtener la ruta seleccionada por el usuario
        Stage stage = (Stage) btn_excel.getScene().getWindow(); // Asegúrate de tener acceso a tu Stage
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            String filePath = file.getAbsolutePath();
            ExcelExporter.exportToExcel(tabla_reporte, filePath);
        }
    }





}



