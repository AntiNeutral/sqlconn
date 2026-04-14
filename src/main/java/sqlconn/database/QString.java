package sqlconn.database;

import java.util.ArrayList;
import java.util.HashSet;

class QString {
    private String body;
    public HashSet<String> tables;
    private int avail_clauses = 0;

    QString() {
        this.body = "";
    }

    String getQuery() {
        return this.body;
    }

    void from(String table) {
        if (avail_clauses > 1) {
            throw new IllegalArgumentException("FROM clause already exists");
        }
        this.body = this.body + " " + table;
        this.tables.add(table);
        this.avail_clauses = 1;
    }

    void join(String table, String on_main_table, String on_main_column, String on_right_column, String type) {
        if (this.avail_clauses > 2) {
            throw new IllegalArgumentException("JOIN clause already exists");
        }
        if (this.tables.contains(table)) {
            throw new IllegalArgumentException("Table already joined");
        }
        if (!type.equals("left") && !type.equals("right") && !type.equals("inner") && !type.equals("outer")) {
            throw new IllegalArgumentException("Invalid join type");
        }
        this.body = this.body + " " + type + "join " + table + " on " + on_main_table + "." + on_main_column + " = " + table + "." + on_right_column;
        this.tables.add(table);
        this.avail_clauses = 2;
    }

    void unsafeWhere(String condition) {
        if (this.avail_clauses > 3) {
            throw new IllegalArgumentException("WHERE clause already exists");
        } else if (this.avail_clauses < 3 && this.avail_clauses > 0) {
            this.body = this.body + " where " + condition;
            this.avail_clauses = 3;
        } else if (this.avail_clauses == -1) {
            this.body = this.body + " " + condition;
            this.avail_clauses = 3;
        } else {
            throw new IllegalArgumentException("Add a logical operator before adding another WHERE clause");
        }
    }

    void mathWhere(String template, ArrayList<String> argsTable, ArrayList<String> argsColumn) {
        if (this.avail_clauses > 3) {
            throw new IllegalArgumentException("WHERE clause already exists");
        }
        for (int i = 0; i < argsTable.size(); i++) {
            template = template.replaceFirst(" ? ", " " + argsTable.get(i) + "." + argsColumn.get(i) + " ");
        }
        template = template.strip();
        if (this.avail_clauses < 3 && this.avail_clauses > 0) {
            this.body = this.body + " where " + template;
            this.avail_clauses = 3;
        } else if (this.avail_clauses == -1) {
            this.body = this.body + " " + template;
        } else {
            throw new IllegalArgumentException("Add a logical operator before adding another WHERE clause");
        }
    }

    /**
     * Adds a logical operator to the where clause
     * @param logic The logical operator to add: AND, OR, XOR
     */
    void addWhereCond(String logic) {
        if (this.avail_clauses != 3) {
            throw new IllegalArgumentException("Insert a where clause first or where clause already exists");
        }
        this.body = this.body + " " + logic;
        this.avail_clauses = -1;
    }

    void groupBy(String table, String column) {
        if (this.avail_clauses > 4) {
            throw new IllegalArgumentException("GROUPBY clause already exists");
        } else if (this.avail_clauses == -1) {
            throw new IllegalArgumentException("WHERE clause not finished");
        } else if (this.avail_clauses < 4) {
            this.body = this.body + " group by " + table + "." + column;
            this.avail_clauses = 4;
        } else {
            this.body = this.body + ", " + table + "." + column;
        }
    }

    void groupBy(ArrayList<String> tables, ArrayList<String> columns) {
        if (this.avail_clauses > 4) {
            throw new IllegalArgumentException("GROUPBY clause already exists");
        } else if (this.avail_clauses == -1) {
            throw new IllegalArgumentException("WHERE clause not finished");
        } else if (this.avail_clauses < 4) {
            StringBuilder group_by_clause = new StringBuilder(" group by");
            for (int i = 0; i < tables.size(); i++) {
                group_by_clause.append(", ").append(tables.get(i)).append(".").append(columns.get(i));
            }
            this.body += group_by_clause;
            this.avail_clauses = 4;
        } else {
            StringBuilder group_by_clause = new StringBuilder();
            for (int i = 0; i < tables.size(); i++) {
                group_by_clause.append(", ").append(tables.get(i)).append(".").append(columns.get(i));
            }
            this.body += group_by_clause;
        }
    }

    void limit(int num) {
        if (this.avail_clauses <= 6) {
            throw new IllegalArgumentException("Finsh the query first");
        }
        this.body = this.body + " limit " + num;
    }
}
