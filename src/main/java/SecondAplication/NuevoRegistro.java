package SecondAplication;

public class NuevoRegistro {
    private int id;
    private String maquinariaId;
    private String fechaInicio;
    private String fechaTermino;
    private String operador;
    private String horasTrabajo;
    private String dni;
    private double total;
    private String usuarios;
    private String cliente;
    private String estadoId;

    public NuevoRegistro(int id, String maquinariaId, String fechaInicio, String fechaTermino,
                         String operador, String horasTrabajo, String dni, double total,
                         String usuarios, String cliente, String estadoId) {
        this.id = id;
        this.maquinariaId = maquinariaId;
        this.fechaInicio = fechaInicio;
        this.fechaTermino = fechaTermino;
        this.operador = operador;
        this.horasTrabajo = horasTrabajo;
        this.dni = dni;
        this.total = total;
        this.usuarios = usuarios;
        this.cliente = cliente;
        this.estadoId = estadoId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getMaquinariaId() {
        return maquinariaId;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaTermino() {
        return fechaTermino;
    }

    public String getOperador() {
        return operador;
    }

    public String getHorasTrabajo() {
        return horasTrabajo;
    }

    public String getDni() {
        return dni;
    }

    public double getTotal() {
        return total;
    }

    public String getUsuarios() {
        return usuarios;
    }

    public String getCliente() {
        return cliente;
    }

    public String getEstadoId() {
        return estadoId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setMaquinariaId(String maquinariaId) {
        this.maquinariaId = maquinariaId;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaTermino(String fechaTermino) {
        this.fechaTermino = fechaTermino;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public void setHorasTrabajo(String horasTrabajo) {
        this.horasTrabajo = horasTrabajo;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setUsuarios(String usuarios) {
        this.usuarios = usuarios;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setEstadoId(String estadoId) {
        this.estadoId = estadoId;
    }
}
