package mysqlconn;
import java.sql.*;
import java.util.*;

public class Db {
    ArrayList<String> table_list = new ArrayList<>();
    Connection conn;
    Map<String, LinkedHashMap<String, String>> loaded_tables = new HashMap<>();
    DatabaseMetaData meta;

    public Db(String url, String user, String password) throws SQLException {
        this.conn = DriverManager.getConnection(url, user, password);
        this.meta = conn.getMetaData();
        try (ResultSet tables = this.meta.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
            this.table_list.add(tables.getString("TABLE_NAME"));
            }
        }
    }

    public void parseTableName(String table_name) {
        if (!this.table_list.contains(table_name)) {
            throw new IllegalArgumentException("Table not found");
        }
    }

    public void praseColumnName(String column_name, String table_name) throws SQLException {
        if (!this.loaded_tables.containsKey(table_name)) {
            this.loadTable(table_name);
        }
        if (!this.loaded_tables.get(table_name).containsKey(column_name)) {
            throw new IllegalArgumentException("Column not found");
        }
    }

    public void loadTable(String table_name) throws SQLException {
        parseTableName(table_name);
        if (!this.loaded_tables.containsKey(table_name)) {
            LinkedHashMap<String, String> columns = new LinkedHashMap<>();
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

    public String prepareInsertString(String table_name) throws SQLException {
        if (!this.loaded_tables.containsKey(table_name)) {
            this.loadTable(table_name);
        }
        int size = this.loaded_tables.get(table_name).size();
        StringBuilder placeholders = new StringBuilder("?");
        placeholders.repeat(", ?", Math.max(0, size - 1));
        return "INSERT INTO " + table_name + " VALUES (" + placeholders + ")";
    }

    public void bulkInsert(String table_name, List<List<Object>> data) throws SQLException {
        int width = data.getFirst().size();
        String command = this.prepareInsertString(table_name);
        try (PreparedStatement stmt = this.conn.prepareStatement(command)) {
            this.conn.setAutoCommit(false);
            int batch_size = 1000;
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < width; j++) {
                    stmt.setObject(j + 1, data.get(i).get(j));
                }
                stmt.addBatch();
                if ((i + 1) % batch_size == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
            this.conn.commit();
        } catch (SQLException e) {
            this.conn.rollback();
            throw e;
        } finally {this.conn.setAutoCommit(true);}
    }
}
