package sqlconn.condition.mathformula;

public class Div extends Formula{
    public Div(String table, String column) {
        super(table, column);
        this.operator = "/";
    }

    public Div(Double value) {
        super(value);
        this.operator = "/";
    }
}
