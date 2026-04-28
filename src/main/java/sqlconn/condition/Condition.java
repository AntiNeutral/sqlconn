package sqlconn.condition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Condition {
    final LinkedList<Expression> expressions = new LinkedList<>();
    final HashMap<String, HashSet<String>> columns = new HashMap<>();
    public StringBuilder query = new StringBuilder();
    boolean sqlPrased = false;

    public Condition(Expression expression) {
        this.expressions.addFirst(expression);
    }

    public Condition() {
    }

    public void toSql() {
        StringBuilder sql = new StringBuilder();
        Iterator<Expression> it = this.expressions.iterator();
        Expression head = it.next();
        head.isAnd = true;
        sql.append(head.toSql().replaceFirst("AND ", ""));
        while (it.hasNext()) {
            sql.append(it.next().toSql());
        }
        this.query.append(sql);
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

    public void merge(Condition condition, String connOp) {
        mergeColumns(condition.columns);
        if (this.sqlPrased & condition.sqlPrased) {
            switch (connOp) {
                case "AND":
                    this.query.append(" AND ").append(condition.query);
                    break;
                case "OR":
                    this.query.append(" OR (").append(condition.query).append(")");
                    break;
                case "NOT":
                    this.query.append(" NOT (").append(condition.query).append(")");
                    break;
            }
        }
    }
}
