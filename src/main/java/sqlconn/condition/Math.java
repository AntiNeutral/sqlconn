package sqlconn.condition;

import sqlconn.condition.mathformula.Formula;

import java.util.HashSet;

public class Math extends Expression{
    public Formula root;
    public Formula tail;

    public Math(boolean isAnd, boolean negation, Formula formula) {
        super(isAnd, negation);
        this.root = formula;
        this.tail = formula;
        this.columns.put(formula.column, new HashSet<>());
        this.columns.get(formula.column).add(formula.table);
    }

    public Math(Formula formula) {
        this(true, true, formula);
    }

    public Math(boolean isAnd, Formula formula) {
        this(isAnd, true, formula);
    }

    public void append(Formula formula) {
        this.tail.next = formula;
        do {
            this.tail = this.tail.next;
            this.addColumn(formula.table, formula.column);
        }
        while (this.tail.next != null);
    }

    @Override
    public String toSql() {
        Formula current = this.root;
        StringBuilder sql = new StringBuilder();
        while (current != null) {
            sql.append(current.toSql());
            current = current.next;
        };
        return this.logicWrapper(sql.toString());
    }

    /* TODO: rewrite how fomulas are organized*/
}
