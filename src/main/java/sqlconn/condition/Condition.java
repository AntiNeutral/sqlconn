package sqlconn.condition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Condition {
    final LinkedList<Expression> expressions = new LinkedList<>();
    final HashMap<String, ArrayList<String>> columns = new HashMap<>();

    public Condition(Expression expression) {
        this.expressions.addFirst(expression);
    }

    public Condition() {
    }

    public String toSql() {
        return this.expressions.getFirst().toSql();
    }
}
