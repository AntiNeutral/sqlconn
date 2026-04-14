package sqlconn.condition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Condition {
    public final LinkedList<Expression> expressions = new LinkedList<>();
    public final HashMap<String, ArrayList<String>> columns = new HashMap<>();

    public Condition(Expression expression) {
        this.expressions.addFirst( expression);
    }

    public Condition() {
    }
}
