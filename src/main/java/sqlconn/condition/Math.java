package sqlconn.condition;

import sqlconn.condition.mathformula.Formula;

public class Math extends Expression{
    public Formula root;
    public Formula tail;

    public Math(boolean isAnd, boolean negation, Formula formula) {
        super(isAnd, negation);
        this.root = formula;
        this.tail = formula;
    }

    public Math(Formula formula) {
        this(true, true, formula);
    }

    public Math(boolean isAnd, Formula formula) {
        this(isAnd, true, formula);
    }

    public void append(Formula formula) {
        this.tail.next = formula;
        this.tail = formula;
    }

    @Override
    public String toSql() {
        Formula current = this.root;
        StringBuilder sql = new StringBuilder();
        while (current != null) {
            sql.append(current.toSql());
            current = current.next;
        };
        return sql.toString();
    }
}
