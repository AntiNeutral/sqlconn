package sqlconn.condition.mathformula;

public class Sub extends Formula{
    public Sub(String table, String column) {
        super(table, column);
        this.operator = "-";
    }

    public Sub(Double value) {
        super(value);
        this.operator = "-";
    }
}
