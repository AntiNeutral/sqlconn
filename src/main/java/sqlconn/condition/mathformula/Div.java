package sqlconn.condition.mathformula;

public class Div extends Formula{
    public Div(String table, String column, int lp, int rp) {
        super(table, column, lp, rp);
        this.operator = "/";
    }

    public Div(Double value, int lp, int rp) {
        super(value, lp, rp);
        this.operator = "/";
    }

    public Div(String table, String column) {
        this(table, column, 0, 0);
    }

    public Div(Double value) {
        this(value, 0, 0);
    }
}
