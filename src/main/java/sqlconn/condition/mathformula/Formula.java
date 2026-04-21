package sqlconn.condition.mathformula;

public abstract class Formula {
    public Formula next = null;
    public String table = null;
    public String column = null;
    public Double value = null;
    public int lp;
    public int rp;
    String operator;

    public Formula(String table, String column, int lp, int rp) {
        this.table = table;
        this.column = column;
        this.lp = lp;
        this.rp = rp;
    }

    public Formula(Double value, int lp, int rp) {
        this.value = value;
        this.lp = lp;
        this.rp = rp;
    }

    public String toSql() {
        StringBuilder sb = new StringBuilder();
        if (value != null) {
            sb.append(" ").append(this.operator).append(" ");
            sb.repeat("(", Math.max(0, lp)).append(value);
        } else {
            sb.append(" ").append(this.operator).append(" ");
            sb.repeat("(", Math.max(0, lp)).append(this.table).append(".").append(this.column);
        }
        sb.repeat(")", Math.max(0, rp));
        return sb.toString();
    };
}
