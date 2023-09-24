package resources;

import ambito.MemberDetails;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlQuerys {

    final String url = "jdbc:mysql://localhost:3306/a20130044";
    final String username = "root";
    final String password  = "root";
    Connection connection;
    public void tryConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url,username,password);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.printf(String.valueOf(e));
        }
    }
    public void truncateTable(){
        try {
            Statement statement =  connection.createStatement();
            statement.execute("TRUNCATE TABLE ambito");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public void addMember(MemberDetails mb){
        try {
            Statement statement =  connection.createStatement();
            statement.execute("INSERT INTO a20130044.ambito (nameId,typeId,classId,ambito,cantParametro," +
                    "typeParametro,arrayDimension,arrayLength) VALUES ('"+mb.getId()+"','"+mb.getType()+"'," +
                    "'"+mb.getClassId()+"','"+mb.getAmbito()+"','"+mb.getCantParametro()+"','"+mb.getTypeParametro()+"" +
                    "','"+mb.getArrayDimension()+"','"+mb.getArrayLength()+"');");
            System.out.println(mb.getType());
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public boolean isDeclarated(final int ambito,final String id){
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM ambito WHERE nameId = '" + id + "' AND ambito = " + ambito;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            if (rs.next()) {
                return true;
            }

            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }
    public boolean isDeclaratedGetSet(final int ambito,final String id,final String classId){
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM ambito WHERE nameId = '" + id + "' AND classId = '" + classId + "' AND ambito = " + ambito;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            if (rs.next()) {
                return true;
            }

            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }
    public int calculateType(final String type,final int ambito) {
        int countType = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) AS count_type FROM ambito WHERE typeId = '" + type + "' AND ambito = " + ambito;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            while (rs.next()) {
                countType = rs.getInt("count_type");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return countType;
    }
    public int classType(final int ambito) {
        int countType = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) AS count_type FROM ambito WHERE typeId LIKE '#%' AND ambito = " + ambito;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            while (rs.next()) {
                countType = rs.getInt("count_type");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return countType;
    }
    public int ambitos(){
        int ambitos=0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(DISTINCT ambito) AS ambitos FROM ambito";
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            while (rs.next()) {
                ambitos = rs.getInt("ambitos");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return ambitos;
    }
    public boolean declarationError(){

        return false;
    }
    public boolean executeError(){

        return false;
    }
}
