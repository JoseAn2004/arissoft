package SecondAplication;

import conexion.ConexcionDB;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.arissoft.Clientes;
import org.example.arissoft.LoginApplication;
import org.example.arissoft.PrincipalApplication;

import javax.swing.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecondAplication extends Application {

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
    private TabPane tabPane;

    @FXML
    private Tab tabinicio;

    @FXML
    private void handleinicioButtonClick(ActionEvent event) {
        tabPane.getSelectionModel().select(tabinicio);
    }
    @FXML
    private Tab tabclientes;

    @FXML
    private void handleClientesButtonClick(ActionEvent event) {
        tabPane.getSelectionModel().select(tabclientes);
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
                    txtnombres.setText(nombre + " " + apellido);
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
        try {
            String maquinaria = String.valueOf(txtelegirmaquinaria.getValue());
            LocalDate fechaInicio = txtfechainicio.getValue();
            String fechaInicioFormateada = fechaInicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate fechaTermino = txtfechatermino.getValue();
            String fechaTerminoFormateada = fechaTermino.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String operadorNombreCompleto = String.valueOf(txtoperador.getValue());
            String horasTrabajo = txthoras_realizar.getText();
            String dni = txtdni.getText();
            String usuarioNombreCompleto = txtusuario_registro.getText();
            String clienteNombreCompleto = txtnombres.getText();
            String estado = boxestadoini.getValue();
            double total = Double.parseDouble(txttotal.getText());


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
                cargarregistro();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error de base de datos: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Error de formato de número: " + e.getMessage());
        }
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private NuevoRegistro registroSeleccionadoAnterior = null;

    @FXML
    private void seleccionarregistro() {
        NuevoRegistro registroseleccionar = tablainicio.getSelectionModel().getSelectedItem();
        if (registroseleccionar != null && !registroseleccionar.equals(registroSeleccionadoAnterior)) {
            registroSeleccionadoAnterior = registroseleccionar;
            if (registroseleccionar.getMaquinariaId() != null) {
                txtelegirmaquinaria.setValue(registroseleccionar.getMaquinariaId());
            }
            if (registroseleccionar.getOperador() != null) {
                txtoperador.setValue(registroseleccionar.getOperador());
            }
            if (registroseleccionar.getEstadoId() != null) {
                boxestadoini.setValue(registroseleccionar.getEstadoId());
            }
            if (registroseleccionar.getFechaInicio() != null) {
                txtfechainicio.setValue(LocalDate.parse(registroseleccionar.getFechaInicio()));
            }
            if (registroseleccionar.getFechaTermino() != null) {
                txtfechatermino.setValue(LocalDate.parse(registroseleccionar.getFechaTermino()));
            }
            if (registroseleccionar.getUsuarios() != null) {
                txtusuario_registro.setText(registroseleccionar.getUsuarios());
            }
            if (registroseleccionar.getHorasTrabajo() != null) {
                txthoras_realizar.setText(registroseleccionar.getHorasTrabajo());
            }
            if (registroseleccionar.getDni() != null) {
                txtdni.setText(registroseleccionar.getDni());
            }
            txttotal.setText(String.valueOf(registroseleccionar.getTotal()));
            habilitarEdicion(true);
        }
    }
    public void setUsuario(String nombre, String apellido) {
        txtusuario_registro.setText(nombre +" "+ apellido);

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
        String sql = "{CALL ActualizarRegistroAlqMaquinaria(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection connection = ConexcionDB.conectar();
             CallableStatement statement = connection.prepareCall(sql)) {
            statement.setInt(1, nuevoregistro.getId());
            statement.setDate(2, java.sql.Date.valueOf(nuevoregistro.getFechaInicio()));
            statement.setDate(3, java.sql.Date.valueOf(nuevoregistro.getFechaTermino()));
            statement.setString(4, nuevoregistro.getMaquinariaId());
            statement.setString(5, nuevoregistro.getOperador());
            statement.setBigDecimal(6, new BigDecimal(nuevoregistro.getHorasTrabajo()));
            statement.setString(7, nuevoregistro.getDni());
            statement.setBigDecimal(8, new BigDecimal(nuevoregistro.getTotal()));
            statement.setString(9, nuevoregistro.getUsuarios());
            statement.setString(10, nuevoregistro.getCliente());
            statement.setString(11, nuevoregistro.getEstadoId());

            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Registro actualizado correctamente.", "Confirmación", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo actualizar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al actualizar el registro: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    private void modificarRegistro() {
        NuevoRegistro registroseleccionar = tablainicio.getSelectionModel().getSelectedItem();
        if (registroseleccionar != null) {
            registroseleccionar.setMaquinariaId(String.valueOf(txtelegirmaquinaria.getSelectionModel().getSelectedItem()));
            registroseleccionar.setFechaInicio(txtfechainicio.getValue() != null ? txtfechainicio.getValue().toString() : "");
            registroseleccionar.setFechaTermino(txtfechatermino.getValue() != null ? txtfechatermino.getValue().toString() : "");
            registroseleccionar.setOperador(String.valueOf(txtoperador.getValue()));
            registroseleccionar.setHorasTrabajo(txthoras_realizar.getText());
            registroseleccionar.setDni(txtdni.getText());
            registroseleccionar.setUsuarios(txtusuario_registro.getText());
            registroseleccionar.setCliente(txtnombres.getText());
            registroseleccionar.setEstadoId(String.valueOf(boxestadoini.getValue()));
            try {
                registroseleccionar.setTotal(Double.parseDouble(txttotal.getText()));
            } catch (NumberFormatException e) {
                System.err.println("Error: el total no es un número válido.");
                JOptionPane.showMessageDialog(null, "El campo total debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            modificarRegistro(registroseleccionar);
            limpiarCampos();
            cargarregistro();
        } else {
            JOptionPane.showMessageDialog(null, "No se ha seleccionado ningún registro para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtfechainicio.setValue(null);
        txtfechatermino.setValue(null);
        txthoras_realizar.clear();
        txtdni.clear();
        txtnombres.clear();
        txttotal.clear();
        txthoras.clear();
        txtelegirmaquinaria.setValue(null);
        txtoperador.setValue(null);
        boxestadoini.setValue(null);
    }




    //CLIENTES

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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TableView<org.example.arissoft.Clientes> tabla_clientes;
    @FXML
    private TableColumn<org.example.arissoft.Clientes, String> cl_clientes;
    @FXML
    private TableColumn<org.example.arissoft.Clientes, String> nombre_cliente;
    @FXML
    private TableColumn<org.example.arissoft.Clientes, String> apellido_cliente;
    @FXML
    private TableColumn<org.example.arissoft.Clientes, String> dni_cliente;
    @FXML
    private TableColumn<org.example.arissoft.Clientes, String> direccion_cliente;
    @FXML
    private TableColumn<org.example.arissoft.Clientes, String> telefono_cliente;


    private void cargardatosclientes() {
        List<org.example.arissoft.Clientes> cliente = new ArrayList<>();
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
                cliente.add(new org.example.arissoft.Clientes(id, nombre, apellido, dni, direccionc, telefono));
            }
            tabla_clientes.getItems().setAll(cliente);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void seleccionarCliente() {
        org.example.arissoft.Clientes clienteSeleccionado = tabla_clientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado != null) {
            txtnombrecliente.setText(clienteSeleccionado.getNombre());
            txtapellidocliente.setText(clienteSeleccionado.getApellido());
            txtdnicliente.setText(Integer.toString(clienteSeleccionado.getDni()));
            txtdireccioncliente.setText(clienteSeleccionado.getDireccionc());
            txttelefonoliente.setText(Integer.toString(clienteSeleccionado.getTelefono()));
        }
    }

    private void modificar_clientes(org.example.arissoft.Clientes cliente) {
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








    public void initialize() {

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

        cl_clientes.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombre_cliente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellido_cliente.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        dni_cliente.setCellValueFactory(new PropertyValueFactory<>("dni"));
        direccion_cliente.setCellValueFactory(new PropertyValueFactory<>("direccionc"));
        telefono_cliente.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        cargardatosclientes();

    }
}
