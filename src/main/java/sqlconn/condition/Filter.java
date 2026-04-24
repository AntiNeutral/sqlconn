package sqlconn.condition;

public class Filter extends Expression{
    public final String value;
    public final String table;
    public final String column;

    public Filter(boolean isAnd, boolean negation, String table, String column, String value) {
        super(isAnd, negation);
        this.value = value;
        this.table = table;
        this.column = column;
        this.addColumn(table, column);
    }

    public Filter(String table, String column, String value) {
        this(true, true, table, column, value);
    }

    public Filter(boolean isAnd, String table, String column, String value) {
        this(isAnd, true, table, column, value);
    }

    @Override
    public String toSql() {
        String sql = this.table + "." + this.column + " = " + this.value;
        return this.notWrapper(sql);
    }
}
