package main;

import jdk.jfr.StackTrace;
import main.model.Drink;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class PostgreSQL implements IDatabase {
    private Connection c;
    private Statement stmt;
    private final String connectionUrl = "jdbc:postgresql://localhost:5432/2dv513_a3";
    private final String connectionUser = "postgres";
    private final String connectionPwd = "test1234";

    public PostgreSQL() {
        c = null;
        stmt = null;
    }

    @Override
    public void addTable(String tableName) {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(connectionUrl, connectionUser, connectionPwd);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "INSERT INTO cafe.tables VALUES (DEFAULT, \'" + tableName + "\');";
            stmt.executeUpdate(sql);

            c.commit();

            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Row inserted successfully");
    }

    @Override
    public void addDrink(Double drinkPrice, String drinkName, String drinkCategory) {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(connectionUrl, connectionUser, connectionPwd);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "INSERT INTO cafe.drinks VALUES (DEFAULT, \'" +
                    drinkPrice + "\', \'" + drinkName + "\', \'" + drinkCategory + "\');";
            stmt.executeUpdate(sql);

            c.commit();

            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Row inserted successfully");
    }

    @Override
    public void addDrinkToTable(Drink drink, int tableId) {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(connectionUrl, connectionUser, connectionPwd);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            int drinkId = this.getDrinkId(drink.getName());
            String sql = "INSERT INTO cafe.tables_drinks VALUES (\'" +
                    tableId + "\', \'" + drinkId + "\');";
            stmt.executeUpdate(sql);

            c.commit();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage());
            for (StackTraceElement error : e.getStackTrace()) {
                System.out.println(error);
            }
            System.exit(0);
        }
        System.out.println("Row inserted successfully");
    }

    @Override
    public void addReceipt(String server, String date, int tableId) {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(connectionUrl, connectionUser, connectionPwd);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();

            // Creates receipt
            String sql = "INSERT INTO cafe.receipts VALUES (DEFAULT, " +
                    "\'" + server + "\', \'" + date + "\');";
            stmt.executeUpdate(sql);

            // Gets the newly created receipt id
            Statement stmtQuery = c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

            String sql2 = "SELECT * FROM cafe.receipts ORDER BY id DESC LIMIT 1";
            ResultSet rs = stmtQuery.executeQuery(sql2);
            rs.last();
            int receiptId = rs.getInt("id");

            // Creates row in receipts_tables with receipt id and table id
            String sql3 = "INSERT INTO cafe.receipts_tables VALUES (" + receiptId + ", " + tableId + ")";
            stmt.executeUpdate(sql3);

            c.commit();

            stmtQuery.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Row inserted successfully");
    }

    @Override
    public LinkedHashMap<Integer, String> getTables() {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(connectionUrl, connectionUser, connectionPwd);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            // Gets the newly created receipt id
            Statement stmtQuery = c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

            LinkedHashMap<Integer, String> allTables = new LinkedHashMap<>();
            String sql = "SELECT * FROM cafe.tables;";
            ResultSet rs = stmtQuery.executeQuery(sql);
            while (rs.next()) {
                allTables.put(rs.getInt("id"), rs.getString("table_name"));
            }

            c.commit();

            stmtQuery.close();
            c.close();

            return allTables;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Row inserted successfully");

        return null;
    }

    @Override
    public LinkedHashMap<String, List<Drink>> getDrinks() {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(connectionUrl, connectionUser, connectionPwd);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            // Gets the newly created receipt id
            Statement stmtQuery = c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

            LinkedHashMap<String, List<Drink>> allDrinks = new LinkedHashMap<>();
            String sql = "SELECT * FROM cafe.drinks;";
            ResultSet rs = stmtQuery.executeQuery(sql);
            List<Drink> drinkList = new ArrayList<>();
            while (rs.next()) {
                Drink d = new Drink(rs.getString("drink_name"),
                        rs.getString("drink_category"),
                        rs.getDouble("drink_price")
                );
                drinkList.add(d);
            }

            allDrinks.put("allDrinks", drinkList);

            c.commit();

            stmtQuery.close();
            c.close();

            return allDrinks;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Row inserted successfully");

        return null;
    }

    @Override
    public LinkedHashMap<String, List<Drink>> getTableDrinks(int tableId) {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(connectionUrl, connectionUser, connectionPwd);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            // Gets the newly created receipt id
            Statement stmtQuery = c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

            LinkedHashMap<String, List<Drink>> allDrinks = new LinkedHashMap<>();
            String sql = "SELECT d.drink_name, d.drink_category, d.drink_price " +
                    "FROM cafe.drinks d, cafe.tables_drinks td " +
                    "JOIN cafe.tables tb ON tb.id = td.table_id " +
                    "WHERE d.id = td.drink_id AND td.table_id = '" + tableId + "';";
            ResultSet rs = stmtQuery.executeQuery(sql);
            List<Drink> drinkList = new ArrayList<>();
            while (rs.next()) {
                Drink d = new Drink(
                        rs.getString("drink_name"),
                        rs.getString("drink_category"),
                        rs.getDouble("drink_price")
                );
                drinkList.add(d);
            }

            allDrinks.put("allDrinks", drinkList);

            c.commit();

            stmtQuery.close();
            c.close();

            return allDrinks;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Row inserted successfully");

        return null;
    }

    @Override
    public int getTableId(String tableName) {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(connectionUrl, connectionUser, connectionPwd);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            // Gets the newly created receipt id
            Statement stmtQuery = c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

            int tableId = 0;
            String sql = "SELECT tl.id FROM cafe.tables tl WHERE tl.table_name = " +
                    "'" + tableName + "'" + "LIMIT 1;";
            ResultSet rs = stmtQuery.executeQuery(sql);
            while (rs.next()) {
                tableId = rs.getInt("id");
            }

            c.commit();

            stmtQuery.close();
            c.close();

            return tableId;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Row inserted successfully");

        return 0;
    }

    @Override
    public int getDrinkId(String drinkName) {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(connectionUrl, connectionUser, connectionPwd);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            // Gets the newly created receipt id
            Statement stmtQuery = c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

            int tableId = 0;
            String sql = "SELECT dr.id FROM cafe.drinks dr WHERE dr.drink_name = " +
                    "'" + drinkName + "'" + "LIMIT 1;";
            ResultSet rs = stmtQuery.executeQuery(sql);
            while (rs.next()) {
                tableId = rs.getInt("id");
            }

            c.commit();

            stmtQuery.close();
            c.close();

            return tableId;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Row inserted successfully");

        return 0;
    }

    @Override
    public double getTotalSumOfTable(String tableName) {
        int tableId = this.getTableId(tableName);
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(connectionUrl, connectionUser, connectionPwd);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            // Gets the newly created receipt id
            Statement stmtQuery = c.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

            String sql = "SELECT SUM(d.drink_price) as total_sum " +
                    "FROM cafe.drinks d, cafe.tables_drinks td " +
                    "JOIN cafe.tables tb ON tb.id = td.table_id " +
                    "WHERE d.id = td.drink_id AND td.table_id = " + tableId + ";";
            ResultSet rs = stmtQuery.executeQuery(sql);

            double sum = 0;
            while (rs.next()) {
                sum = rs.getDouble("total_sum");
            }

            c.commit();

            stmtQuery.close();
            c.close();

            return sum;
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Row inserted successfully");

        return 0;
    }
}
