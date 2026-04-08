package mysqlconn;

import java.util.HashMap;
import java.util.HashSet;

public class QString {
    public String body;
    public HashSet<String> tables;
    private final HashMap<String, Integer> avail_clauses = new HashMap<>();

    QString() {
        this.body = "";
        this.avail_clauses.put("from", 1);
        this.avail_clauses.put("join", 1);
        this.avail_clauses.put("where", 1);
    }

    void from(String table) {
        if (this.tables.contains(table)) {
            throw new IllegalArgumentException("Table already exists");
        }
        if (this.avail_clauses.get("from") == 0) {
            throw new IllegalArgumentException("Clause already exists");
        }
        this.body = this.body + " " + table;
        this.tables.add(table);
        this.avail_clauses.put("from", 0);
    }

    void join(String table, String on_main_table, String on_main_column, String on_right_column, String type) {
        if (this.avail_clauses.get("where") == 0) {
            throw new IllegalArgumentException("Clause already exists");
        }
        if (this.tables.contains(table)) {
            throw new IllegalArgumentException("Table already exists");
        }
        if (!type.equals("left") && !type.equals("right") && !type.equals("inner") && !type.equals("outer")) {
            throw new IllegalArgumentException("Invalid join type");
        }
        this.body = this.body + " " + type + "join " + table + " on " + on_main_table + "." + on_main_column + " = " + table + "." + on_right_column + " ";
        this.tables.add(table);
        this.avail_clauses.put("join", 0);
    }
}
