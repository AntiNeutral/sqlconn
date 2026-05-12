package sqlconn.condition;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class Parentheses <T extends Parentheses<T>>{
    protected T left;
    protected T right;
    protected T parent;
    public String operator;

    protected abstract T self();

    public abstract String toSql();

    public abstract void negate();

    abstract public T wrap(T right) throws IllegalArgumentException;

    public String parse() {
        StringBuilder sql = new StringBuilder();
        LinkedList<T> stack = new LinkedList<>();
        T boundary = this.parent.self();
        T current = this.self();
        if (current.left != null) {
            stack.add(current);
            sql.append("(");
            current = current.left;
        } else {
            return this.toSql();
        }
        while (current != boundary) {
            T last = stack.getLast();
            if (current.parent == last) {
                sql.append("(");
                if (current.left != null) {
                    stack.add(current);
                    current = current.left;
                } else {
                    sql.append(current.toSql());
                    stack.add(current);
                    current = current.parent;
                }
            } else if (last == current.left) {
                sql.append(")").append(current.right.operator);
                stack.removeLast();
                current = current.right;
            } else {
                sql.append(")");
                stack.removeLast();
                current = current.parent;
            }
        }
        sql.append(")");
        return sql.toString();
    }
}
