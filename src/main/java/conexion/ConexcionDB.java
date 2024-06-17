package conexion;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConexcionDB {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlserver://aris2.database.windows.net:1433;database=ARIS_SOFT");
        config.setUsername("jchavez@aris2");
        config.setPassword("chavezs1234#");
        config.addDataSourceProperty("encrypt", "true");
        config.addDataSourceProperty("trustServerCertificate", "false");
        config.addDataSourceProperty("hostNameInCertificate", "*.database.windows.net");
        config.setMaximumPoolSize(10); // Tamaño máximo del pool de conexiones

        dataSource = new HikariDataSource(config);
    }

    public static Connection conectar() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión del pool: " + e.getMessage());
            throw new RuntimeException("Error al obtener conexión del pool", e);
        }
    }

    public static void main(String[] args) {
        try (Connection conn = conectar()) {
            if (conn != null) {
                System.out.println("Conexión establecida.");
            } else {
                System.out.println("La conexión no se estableció correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Excepción al cerrar la conexión.");
            e.printStackTrace();
        }
    }
}
