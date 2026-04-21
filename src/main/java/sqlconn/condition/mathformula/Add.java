package sqlconn.condition.mathformula;

public class Add extends Formula{
    public Add(String table, String column, int lp, int rp) {
        super(table, column, lp, rp);
        this.operator = "+";
    }

    public Add(Double value, int lp, int rp) {
        super(value, lp, rp);
        this.operator = "+";
    }

    public Add(String table, String column) {
        this(table, column, 0, 0);
    }

    public Add(Double value) {
        this(value, 0, 0);
    }
}
