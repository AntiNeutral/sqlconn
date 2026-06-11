package sqlconn.condition.mathformula;

import sqlconn.condition.Parentheses;
import java.util.HashSet;

public class MathParentheses extends Parentheses<MathParentheses> {
    public Formula root;
    public Formula tail;

    public MathParentheses(Formula formula) {
        this.root = formula;
        this.tail = formula;
        this.operator = formula.operator;
        this.columns.put(formula.table, new HashSet<>());
        this.columns.get(formula.table).add(formula.column);
    }

    private MathParentheses(MathParentheses left, MathParentheses right) {
        this.root = null;
        this.tail = null;
        this.left = left;
        this.right = right;
        this.operator = left.operator;
    }

    public void append(Formula formula) {
        this.tail.next = formula;
        do {
            this.tail = this.tail.next;
            if (this.tail.table != null) {
                this.addSingleColumn(this.tail.table, this.tail.column);
            }
        }
        while (this.tail.next != null);
    }

    @Override
    public String toSql() {
        Formula current = this.root;
        StringBuilder sql = new StringBuilder();
        sql.append(current.toSql());
        current = current.next;
        sql.delete(0, 1 + this.operator.length());
        while (current != null) {
            sql.append(" ").append(current.toSql());
            current = current.next;
        };
        return sql.toString();
    }
    @Override
    protected MathParentheses self() {
        return this;
    }

    @Override
    public MathParentheses wrap(MathParentheses right) throws IllegalArgumentException {
        if (right == null) {
            throw new IllegalArgumentException("Right condition cannot be null");
        }
        MathParentheses parent = new MathParentheses(this, right);
        this.parent = parent;
        right.parent = parent;
        return parent;
    }

    @Override
    protected void getColumns() {
        this.mergeColumns(this.root.getColumns());
    }
}
