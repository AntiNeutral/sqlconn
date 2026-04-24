package sqlconn.condition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Condition {
    final LinkedList<Expression> expressions = new LinkedList<>();
    final HashMap<String, HashSet<String>> columns = new HashMap<>();

    public Condition(Expression expression) {
        this.expressions.addFirst(expression);
    }

    public Condition() {
    }

    public String toSql() {
        StringBuilder sql = new StringBuilder();
        StringBuilder parentheses = new StringBuilder();
        Iterator<Expression> it = this.expressions.iterator();
        sql.append(it.next().toSql());
        while (it.hasNext()) {
            Expression expression = it.next();
            if (expression.isAnd) {
                sql.append(" AND ").append(expression.toSql());
            } else {
                sql.append(" OR ").append(expression.toSql()).append(")");
                parentheses.append("(");
            }
        }
        parentheses.append(sql);
        return parentheses.toString();
    }

    void mergeColumns(HashMap<String, HashSet<String>> columns) {
        for (String table: columns.keySet()) {
            if (this.columns.containsKey(table)) {
                this.columns.get(table).addAll(columns.get(table));
            } else {
                this.columns.put(table, new HashSet<>(columns.get(table)));
            }
        }
    }

    public void append(Expression expression) {
        this.expressions.addLast(expression);
        this.mergeColumns(expression.columns);
    }

    public void merge(Condition condition) {
        mergeColumns(condition.columns);
    }
}
