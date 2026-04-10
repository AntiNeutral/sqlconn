package sqlconn;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import rXlsx.dataframe;
import rXlsx.series;

abstract class Db {
    public ArrayList<String> table_list = new ArrayList<>();
    private final HashMap<String, LinkedHashMap<String, String>> loaded_tables = new HashMap<>();
    public DatabaseMetaData meta;
    public final HashMap<Integer, QString>  queries = new HashMap<>();
    private final Connection conn;

    public Db(String url, String user, String password) throws SQLException {
        this.conn = DriverManager.getConnection(url, user, password);
        this.meta = conn.getMetaData();
        try (ResultSet tables = this.meta.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
            this.table_list.add(tables.getString("TABLE_NAME"));
            }
        }
    }

    public HashMap<String, LinkedHashMap<String, String>> showCachedTables() {
        HashMap<String, LinkedHashMap<String, String>> view = new HashMap<>();
        for (String table: this.loaded_tables.keySet()) {
            view.put(table, new LinkedHashMap<>());
            for (String column: this.loaded_tables.get(table).keySet()) {
                view.get(table).put(column, this.loaded_tables.get(table).get(column));
            }
        }
        return view;
    }

    public void parseTableName(String table_name) {
        if (!this.table_list.contains(table_name)) {
            throw new IllegalArgumentException("Table not found");
        }
    }

    public void praseColumnName(String column_name, String table_name) {
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

    public void bulkInsert(String table_name, dataframe df) throws SQLException {
        int width = df.dim[1];
        ArrayList<String> column_types = df.column_type;
        ArrayList<String> columns = df.columns;
        String command = this.prepareInsertString(table_name);
        try (PreparedStatement stmt = this.conn.prepareStatement(command)) {
            this.conn.setAutoCommit(false);
            int batch_size = 1000;
            int i = 0;
            for (series row: df.data) {
                for (int j = 0; j < width; j++) {
                    switch (column_types.get(j)) {
                        case "str":
                            stmt.setString(j + 1, (String) row.data.get(columns.get(j)));
                            break;
                        case "d":
                            stmt.setDouble(j + 1, (Double) row.data.get(columns.get(j)));
                            break;
                        case "int":
                            stmt.setInt(j + 1, (Integer) row.data.get(columns.get(j)));
                            break;
                        case "date":
                            stmt.setDate(j + 1, (Date) row.data.get(columns.get(j)));
                            break;
                    }
                }
                stmt.addBatch();
                if ((i + 1) % batch_size == 0) {
                    stmt.executeBatch();
                }
                i++;
            }
            stmt.executeBatch();
            this.conn.commit();
        } catch (SQLException e) {
            this.conn.rollback();
            throw e;
        } finally {this.conn.setAutoCommit(true);}
    }

    public ResultSet executeQuery(String query) throws SQLException {
        return this.conn.createStatement().executeQuery(query);
    }

    public void addQuery(Integer id) {
        this.queries.put(id, new QString());
    }

    public void from(Integer id, String table) throws SQLException {
        this.loadTable(table);
        this.queries.get(id).from(table);
    }

    public void join(Integer id, String table, String on_main_table, String on_main_column, String on_right_column, String type) throws SQLException {
        this.loadTable(table);
        this.praseColumnName(on_main_column, on_main_table);
        this.praseColumnName(on_right_column, table);
        this.queries.get(id).join(table, on_main_table, on_main_column, on_right_column, type);
    }

    public void unsafeWhere(Integer id, String condition) {
        this.queries.get(id).unsafeWhere(condition);
    }

    public void mathWhere(Integer id, String template, ArrayList<String> argsTable, ArrayList<String> argsColumn) throws SQLException {
        for (int i = 0; i < argsTable.size(); i++) {
            this.loadTable(argsTable.get(i));
            this.praseColumnName(argsColumn.get(i), argsTable.get(i));
        }
        this.queries.get(id).mathWhere(template, argsTable, argsColumn);
    }

    /**
     * Adds a logical operator to the where clause
     * @param id The id of the query
     * @param logic The logical operator to add: AND, OR, XOR
     */
    public void addWhereCond(Integer id, String logic) {
        this.queries.get(id).addWhereCond(logic);
    }

    public void groupBy(Integer id, String table, String column) throws SQLException {
        this.loadTable(table);
        this.praseColumnName(column, table);
        this.queries.get(id).groupBy(table, column);
    }

    public void groupBy(Integer id, ArrayList<String> tables, ArrayList<String> columns) throws SQLException {
        for (int i = 0; i < tables.size(); i++) {
            this.loadTable(tables.get(i));
            this.praseColumnName(columns.get(i), tables.get(i));
        }
        this.queries.get(id).groupBy(tables, columns);
    }

    abstract public void slice();
}
