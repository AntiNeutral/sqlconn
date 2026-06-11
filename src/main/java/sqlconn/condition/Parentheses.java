package sqlconn.condition;

import java.util.*;

public abstract class Parentheses <T extends Parentheses<T>>{
    protected T left;
    protected T right;
    protected T parent;
    public String operator;
    protected final HashMap<String, HashSet<String>> columns = new HashMap<>();

    protected abstract T self();

    public abstract String toSql();

    abstract public T wrap(T right) throws IllegalArgumentException;

    /**
     * Populates the columns map with table-column relationships from this parentheses structure.
     * This method traverses the internal structure (either expressions or child parentheses)
     * and merges all column references into the columns HashMap, where keys are table names
     * and values are sets of column names used in those tables.
     */
    protected abstract void getColumns();

    public static void mergeColumns(HashMap<String, HashSet<String>> column1, HashMap<String, HashSet<String>> column2) {
        for (String table: column2.keySet()) {
            if (column1.containsKey(table)) {
                column1.get(table).addAll(column2.get(table));
            } else {
                column1.put(table, new HashSet<>(column2.get(table)));
            }
        }
    };

    protected void mergeColumns(HashMap<String, HashSet<String>> columns) {
        mergeColumns(this.columns, columns);
    }

    protected void addSingleColumn(String table, String column) {
        if (this.columns.containsKey(table)) {
            this.columns.get(table).add(column);
        } else {
            this.columns.put(table, new HashSet<>(List.of(column)));
        }
    }

    public String parse() {
        StringBuilder sql = new StringBuilder();
        LinkedList<T> stack = new LinkedList<>();
        T boundary = (this.parent != null) ? this.parent.self() : null;
        T current = this.self();
        if (current.left != null) {
            stack.add(current);
            sql.append("(");
            current = current.left;
        } else {
            current.getColumns();
            this.mergeColumns(current.columns);
            return this.toSql();
        }
        while (current != boundary) {
            T last = stack.getLast();
            if (current.parent == last) {
                sql.append("(");
                if (current.left != null) {
                    stack.add(current);
                    current = current.left;
                } else {
                    sql.append(current.toSql());
                    stack.add(current);
                    current.getColumns();
                    this.mergeColumns(current.columns);
                    current = current.parent;
                }
            } else if (last == current.left) {
                sql.append(")").append(" ").append(current.right.operator).append(" ");
                stack.removeLast();
                current = current.right;
            } else {
                sql.append(")");
                stack.removeLast();
                current = current.parent;
            }
        }
        sql.append(")");
        return sql.toString();
    }
}
