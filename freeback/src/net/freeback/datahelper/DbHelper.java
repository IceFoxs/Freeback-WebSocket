package net.freeback.datahelper;

import java.sql.Connection;

public class DbHelper {
        protected final int ERROR_RESULT_VALUE = -999;
        protected Connection connection = null;

        public DbHelper(Connection connection) {
                this.connection = connection;
        }
}
