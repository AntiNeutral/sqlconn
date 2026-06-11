package sqlconn.condition.mathformula;

import java.util.HashMap;
import java.util.HashSet;

public abstract class Formula {
    public Formula next = null;
    public String table = null;
    public String column = null;
    public Double value = null;
    String operator;

    public Formula(String table, String column) {
        this.table = table;
        this.column = column;
    }

    public Formula(Double value) {
        this.value = value;
    }

    public String toSql() {
        StringBuilder sb = new StringBuilder();
        if (value != null) {
            sb.append(this.operator).append(" ").append(this.value);
        } else {
            sb.append(this.operator).append(" ").append(this.table).append(".").append(this.column);
        }
        return sb.toString();
    };

    public HashMap<String, HashSet<String>> getColumns() {
        HashMap<String, HashSet<String>> columns = new HashMap<>();
        Formula current = this;
        while (current != null) {
            if (current.table != null) {
                if (columns.containsKey(current.table)) {
                    columns.get(current.table).add(current.column);
                } else {
                    columns.put(current.table, new HashSet<>());
                    columns.get(current.table).add(current.column);
                }
            }
            current = current.next;
        }
        return columns;
    };
}
