package sqlconn.database;

import java.sql.SQLException;

public class MySqlDb extends Db{
    public MySqlDb(String url, String user, String password) throws SQLException {
        super(url, user, password, "MySQL");
    }

    @Override
    public void slice(Integer id, int num) {
        this.queries.get(id).limit(num);
    }
}
