package sqlconn.condition.mathformula;

public class Add extends Formula{
    public Add(String table, String column) {
        super(table, column);
        this.operator = "+";
    }

    public Add(Double value) {
        super(value);
        this.operator = "+";
    }
}
