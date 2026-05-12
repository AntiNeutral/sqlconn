package sqlconn.condition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Condition extends Parentheses<Condition>{
    private final LinkedList<Expression> expressions;
    final HashMap<String, HashSet<String>> columns = new HashMap<>();

    public Condition(Expression expression) {
        this.expressions = new LinkedList<>();
        this.expressions.addFirst(expression);
        this.operator = (expression.isAnd) ? "AND" : "OR";
    }

    private Condition(Condition left, Condition right) {
        this.expressions = null;
        this.left = left;
        this.right = right;
        this.operator = left.operator;
    };

    @Override
    protected Condition self() {
        return this;
    }

    @Override
    public Condition wrap(Condition right) throws IllegalArgumentException {
        if (right == null) {
            throw new IllegalArgumentException("Right condition cannot be null");
        }
        Condition parent = new Condition(this, right);
        this.parent = parent;
        right.parent = parent;
        return parent;
    }

    @Override
    public String toSql() {
        StringBuilder sql = new StringBuilder();
        Iterator<Expression> it = this.expressions.iterator();
        Expression head = it.next();
        sql.append(head.toSql());
        if (head.isAnd) {
            sql.delete(0, 4);
        } else {
            sql.delete(0, 3);
        }
        while (it.hasNext()) {
            sql.append(it.next().toSql());
        }
        return sql.toString();
    }

    @Override
    public void negate() {
        Iterator<Expression> it = this.expressions.iterator();
        it.next();
        while (it.hasNext()) {
            Expression current = it.next();
            current.isAnd = !current.isAnd;
            current.negation = !current.negation;
        }
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
}
