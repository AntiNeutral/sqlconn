package sqlconn.condition.mathformula;

public class Multi extends Formula{
    public Multi(String table, String column) {
        super(table, column);
        this.operator = "*";
    }

    public Multi(Double value) {
        super(value);
        this.operator = "*";
    }
}
