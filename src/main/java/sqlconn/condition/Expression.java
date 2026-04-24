package sqlconn.condition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

abstract public class Expression {
    boolean negation;
    boolean isAnd;
    final HashMap<String, HashSet<String>> columns = new HashMap<>();

    public Expression(boolean isAnd, boolean negation) {
        this.negation = negation;
        this.isAnd = isAnd;
    }

    String notWrapper(String sql) {
        return this.negation ? sql: "NOT (" + sql + ")";
    }

    abstract String toSql();

    void addColumn(String table, String column) {
        if (this.columns.containsKey(table)) {
            this.columns.get(table).add(column);
        } else {
            this.columns.put(table, new HashSet<>(List.of(column)));
        }
    }
}
