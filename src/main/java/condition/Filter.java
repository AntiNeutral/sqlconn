package condition;

import java.util.ArrayList;
import java.util.List;

public class Filter extends Expression{
    public final String value;
    public final String table;
    public final String column;

    public Filter(boolean isAnd, boolean negation, String table, String column, String value) {
        super(isAnd, negation);
        this.value = value;
        this.columns.put(table, new ArrayList<>(List.of(column)));
        this.table = table;
        this.column = column;
    }

    @Override
    public String toString() {
        return this.table + "." + this.column + " = " + this.value;
    }
}
