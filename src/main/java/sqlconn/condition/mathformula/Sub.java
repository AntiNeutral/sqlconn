package sqlconn.condition.mathformula;

public class Sub extends Formula{
    public Sub(String table, String column, int lp, int rp) {
        super(table, column, lp, rp);
        this.operator = "-";
    }

    public Sub(Double value, int lp, int rp) {
        super(value, lp, rp);
        this.operator = "-";
    }

    public Sub(String table, String column) {
        this(table, column, 0, 0);
    }

    public Sub(Double value) {
        this(value, 0, 0);
    }
}
