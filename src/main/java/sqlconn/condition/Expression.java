package sqlconn.condition;

import java.util.ArrayList;
import java.util.HashMap;

abstract public class Expression {
    public final boolean negation;
    public final boolean isAnd;
    final HashMap<String, ArrayList<String>> columns = new HashMap<>();

    public Expression(boolean isAnd, boolean negation) {
        this.negation = negation;
        this.isAnd = isAnd;
    }

    abstract String toSql();
}
