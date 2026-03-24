package mysqlconn;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class db {
    ArrayList<String> table_list = new ArrayList<>();
    Connection conn;
    Map<String, Map<String, String>> loaded_tables = new HashMap<>();
    DatabaseMetaData meta;

    public db(String url, String user, String password) throws SQLException {
        this.conn = DriverManager.getConnection(url, user, password);
        this.meta = conn.getMetaData();
        try (ResultSet tables = this.meta.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
            this.table_list.add(tables.getString("TABLE_NAME"));
            }
        }
    }

    private void parseTableName(String table_name) {
        if (!this.table_list.contains(table_name)) {
            throw new IllegalArgumentException("Table not found");
        }
    }

    private void praseColumnName(String column_name) {

    }

    private void loadTable(String table_name) throws SQLException {
        parseTableName(table_name);
        if (!this.loaded_tables.containsKey(table_name)) {
            Map<String, String> columns = new LinkedHashMap<>();
            try (ResultSet cols = this.meta.getColumns(null, null, table_name, "%")) {
                while (cols.next()) {
                String name = cols.getString("COLUMN_NAME");
                String type = cols.getString("TYPE_NAME");
                columns.put(name, type);
                }
            }
            this.loaded_tables.put(table_name, columns);
        }
    }
}
