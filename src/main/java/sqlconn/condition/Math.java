package sqlconn.condition;

import sqlconn.condition.mathformula.Formula;
import sqlconn.condition.mathformula.MathParentheses;
import java.util.HashMap;
import java.util.HashSet;
import static sqlconn.condition.Parentheses.mergeColumns;

public class Math extends Expression {
    public MathParentheses root;
    public Formula formula;

    public Math(boolean isAnd, boolean negation, Formula formula) {
        super(isAnd, negation);
        this.formula = formula;
        HashMap<String, HashSet<String>> columns = this.formula.getColumns();
        for (String table : columns.keySet()) {
            this.columns.put(table, new HashSet<>(columns.get(table)));
        }
    }

    public Math(Formula formula) {
        this(true, true, formula);
    }

    public Math(boolean isAnd, Formula formula) {
        this(isAnd, true, formula);
    }

    public Math(boolean isAnd, boolean negation, MathParentheses root) {
        super(isAnd, negation);
        this.root = root;
    }

    public Math(MathParentheses root) {
        this(true, true, root);
    }

    public Math(boolean isAnd, MathParentheses root) {
        this(isAnd, true, root);
    }

    @Override
    public String toSql() {
        if (formula != null) {
            return this.logicWrapper(formula.toSql());
        };
        String sql = this.logicWrapper(root.parse());
        mergeColumns(this.columns, root.columns);
        return sql;
    }
}
