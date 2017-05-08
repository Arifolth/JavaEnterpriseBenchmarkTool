/**
 *  Java Enterprise Benchmark Tool
 *  Copyright (C) 2017  Alexander Nilov arifolth@gmail.com 
 */


/**
 * 
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package ru.arifolth.derby;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arifolth.benchmark.*;

import java.io.*;
import java.sql.*;
import java.util.concurrent.Callable;

/**
 * Created by ANilov on 11.02.2017.
 */
public class BenchmarkImpl implements Benchmark {
    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkImpl.class);
    public static final String JDBC_DERBY_JAVA_DB = "jdbc:derby:JavaDB;create=true";
    public static final String ORG_APACHE_DERBY_JDBC_EMBEDDED_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private BenchmarkItem benchmarkItem = new BenchmarkItem("JavaDB");

    @Override
    public BenchmarkItem call() throws Exception {
        LOGGER.info("Benchmarking JavaDB...");

        Timer javaDBTimer = new Timer();
        try {
            Connection conn = createDatabaseConnection();
            Statement stmnt = conn.createStatement();

            //drop if exists
            dropTable(conn, stmnt);

            //create
            createTable(conn, stmnt);


            //insert
            insertInto(conn, stmnt, "*");

            //select
            readFrom(conn, stmnt);

            //update
            insertInto(conn, stmnt, "*");

            //deleteFrom
            deleteFrom(conn, stmnt);

            LOGGER.debug("JavaDB: took " + javaDBTimer.getElapsedTime() + " seconds");
        } catch (SQLException sqlEx) {
            LOGGER.error("Error in BenchmarkItem: ", sqlEx);
        } finally {
            LOGGER.info("Stop Benchmarking JavaDB.");
        }

        return benchmarkItem;
    }

    private void deleteFrom(Connection conn, Statement stmnt) throws SQLException {
        Timer timer = new Timer();
        int result = stmnt.executeUpdate("DELETE FROM files ");
        conn.commit();
        LOGGER.trace("DELETED #" + result + " entries");
        long elapsedMillis = timer.getElapsedMillis();
        LOGGER.debug("Table deleteFrom in : " + +elapsedMillis + " milliseconds");
        benchmarkItem.getBenchmarkResults().add(new BenchmarkResult("deleteFrom", elapsedMillis, MeasureEnum.MILLISECONDS));
    }

    private void readFrom(Connection conn, Statement stmnt) throws SQLException {
        Timer timer = new Timer();
        // --- reading the columns
        ResultSet rs = null;
        try {
            rs = stmnt.executeQuery("SELECT data FROM files");
            int i = 0;
            while (rs.next()) {
                i++;
                Clob clob = rs.getClob(i);
                String stringValue = clob.getSubString(i, (int) clob.length());
                LOGGER.trace("READ CLOB #" + i + ": " + stringValue.length());
            }
        } finally {
            if(rs != null)
                rs.close();
        }
        long elapsedMillis = timer.getElapsedMillis();
        LOGGER.debug("Table Read in : " + +elapsedMillis + " milliseconds");
        benchmarkItem.getBenchmarkResults().add(new BenchmarkResult("readFrom", elapsedMillis, MeasureEnum.MILLISECONDS));
    }

    private void insertInto(Connection conn, Statement stmnt, String str) throws SQLException, IOException {
        Timer timer = new Timer();
        File file = null;
        try {
            file = File.createTempFile(RandomStringUtils.randomAlphabetic(5), "tmp");

            // --- add a file
            OutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(StringUtils.repeat(str, 10000000).getBytes());
                fos.flush();
            } finally {
                if(fos != null)
                    fos.close();
            }

            // - first, create an input stream
            FileInputStream fin = null;
            PreparedStatement ps = null;
            try {
                fin = new FileInputStream(file);
                ps = conn.prepareStatement("INSERT INTO files (DATA) VALUES (?)");
                // - set the value of the input parameter to the input stream
                int fileLength = (int) file.length();
                ps.setAsciiStream(1, fin, fileLength);

                LOGGER.trace("INSERTING " + fileLength + " bytes");

                ps.execute();
                conn.commit();
            } finally {
                if(fin != null) {
                    fin.close();
                }

                if(ps != null) {
                    ps.close();
                }
            }
        } finally {
            if(file != null)
                file.delete();
        }
        long elapsedMillis = timer.getElapsedMillis();
        LOGGER.debug("Table Insert in : " + +elapsedMillis + " milliseconds");
        benchmarkItem.getBenchmarkResults().add(new BenchmarkResult("insertInto", elapsedMillis, MeasureEnum.MILLISECONDS));
    }

    private void dropTable(Connection conn, Statement stmnt) throws SQLException {
        Timer timer = new Timer();
        DatabaseMetaData databaseMetadata = conn.getMetaData();
        ResultSet resultSet = databaseMetadata.getTables(null, null, "FILES", null);
        if (resultSet.next()) {
            stmnt.executeUpdate("DROP TABLE files");
        }
        long elapsedMillis = timer.getElapsedMillis();
        LOGGER.debug("Table dropped in : " + +elapsedMillis + " milliseconds");
        benchmarkItem.getBenchmarkResults().add(new BenchmarkResult("dropTable", elapsedMillis, MeasureEnum.MILLISECONDS));
    }

    private void createTable(Connection conn, Statement stmnt) throws SQLException {
        Timer timer = new Timer();

        stmnt.executeUpdate(
                "CREATE TABLE files (dex INTEGER NOT NULL PRIMARY KEY "
                        + "GENERATED ALWAYS AS identity (START WITH 1, INCREMENT BY 1), "
                        + "data CLOB(10 M))");

        long elapsedMillis = timer.getElapsedMillis();
        LOGGER.debug("Table created in : " + +elapsedMillis + " milliseconds");
        benchmarkItem.getBenchmarkResults().add(new BenchmarkResult("createTable", elapsedMillis, MeasureEnum.MILLISECONDS));
    }

    private static Connection createDatabaseConnection() throws SQLException, ClassNotFoundException {
        String driver = ORG_APACHE_DERBY_JDBC_EMBEDDED_DRIVER;
        Class.forName(driver);
        String url = JDBC_DERBY_JAVA_DB;
        return DriverManager.getConnection(url);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}
