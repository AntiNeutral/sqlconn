package sqlconn.condition.mathformula;

public class Multi extends Formula{
    public Multi(String table, String column, int lp, int rp) {
        super(table, column, lp, rp);
        this.operator = "*";
    }

    public Multi(Double value, int lp, int rp) {
        super(value, lp, rp);
        this.operator = "*";
    }

    public Multi(String table, String column) {
        this(table, column, 0, 0);
    }

    public Multi(Double value) {
        this(value, 0, 0);
    }
}
