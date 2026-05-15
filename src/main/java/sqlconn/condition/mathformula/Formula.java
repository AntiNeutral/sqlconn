package sqlconn.condition.mathformula;

public abstract class Formula {
    public Formula next = null;
    public String table = null;
    public String column = null;
    public Double value = null;
    String operator;

    public Formula(String table, String column) {
        this.table = table;
        this.column = column;
    }

    public Formula(Double value) {
        this.value = value;
    }

    public String toSql() {
        StringBuilder sb = new StringBuilder();
        if (value != null) {
            sb.append(this.operator).append(" ").append(this.value);
        } else {
            sb.append(this.operator).append(" ").append(this.table).append(".").append(this.column);
        }
        return sb.toString();
    };
}
