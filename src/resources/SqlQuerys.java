package resources;

import ambito.MemberDetails;
import semantica.Operand;

import java.sql.*;
import java.util.ArrayList;

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
            statement.execute("TRUNCATE TABLE temporals");
            statement.execute("TRUNCATE TABLE asignations");
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
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public void addTemporal(Operand operand){
        try {
            Statement statement =  connection.createStatement();
            statement.execute("INSERT INTO a20130044.temporals (lexeme,typeString,typeNumber,line) VALUES ('"+operand.getLexema()+"','"+operand.getDataType()+"'," +
                    "'"+operand.getType()+"','"+operand.getLine()+"');");

        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public void addAsignations(final String name,final String symbol,final int type,final int line,final String typeData){
        try {
            Statement statement =  connection.createStatement();
            statement.execute("INSERT INTO a20130044.asignations (nameId,symbol,type,line,typeData) VALUES ('"+name+"','"+symbol+"'," +
                    "'"+type+"','"+line+"','"+typeData+"');");
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
    public String getOneIDType(final int ambito,final String id){
        String type="";
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT typeId FROM ambito WHERE nameId = '" + id + "' AND ambito = " + ambito;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente
            if (rs.next()) {
                type = rs.getString("typeId");
                // Cerrar el ResultSet y el Statement después de su uso
                rs.close();
                statement.close();
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return type;
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
    public int getType(final String type, final int ambito) {
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
    public int getClassType(final int ambito) {
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
    public int getAmbitos(){
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

    public void updateAsignations(final String temp,final int finalDataType){
        try {
            Statement statement =  connection.createStatement();
            statement.execute("UPDATE asignations SET finalData = '"+temp+"', finalDataType ="+finalDataType+" WHERE asignationsID = "+ getLastIdAsignations()+";");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    private int getLastIdAsignations(){
        int lastId = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT MAX(asignationsID) AS last_id FROM asignations";
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            while (rs.next()) {
                lastId = rs.getInt("last_id");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return lastId;
    }
    public int getTempTypeLine(final int line, final int type){
        int typePerLine = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(temporals.typeNumber) AS count_temporals_type FROM asignations INNER JOIN temporals ON asignations.line = temporals.line " +
                    "WHERE temporals.typeNumber = '"+type+"' AND asignations.line = '"+line+"' GROUP BY asignations.type,temporals.typeNumber;";
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            while (rs.next()) {
                typePerLine = rs.getInt("count_temporals_type");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return typePerLine;
    }
    public ArrayList<Integer> getLinesSemantica(){
        ArrayList<Integer> lines=new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT line FROM asignations";
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            while (rs.next()) {
                lines.add(rs.getInt("line"));
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return lines;
    }
    public String getFinalTempString(final int line){
        String finalTemp = "";
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT finalData,symbol,nameId FROM asignations WHERE line = "+line;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            while (rs.next()) {
                finalTemp = rs.getString("nameId");
                finalTemp += " " + rs.getString("symbol");
                finalTemp += " " + rs.getString("finalData");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return finalTemp;
    }
    public boolean getErrorSemantica(final int line){
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM asignations WHERE type = finalDataType AND line = "+line;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            if (rs.next()) {
                return false;
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return true;
    }
    public String getNameIdAsignation(final int line){
        String nameId="";
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT nameId FROM asignations WHERE line = "+line;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            while (rs.next()) {
                nameId = rs.getString("nameId");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return nameId;
    }
    public String getAsignationOperator(final int line){
        String symbol="";
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT symbol FROM asignations WHERE line = "+line;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            while (rs.next()) {
                symbol = rs.getString("symbol");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return symbol;
    }
    public String[] getTypeDataAsignation(final int line){
        String typeData[] = new String[2];
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT typeData,finalData FROM asignations WHERE line = "+line;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente
            while (rs.next()) {
                typeData[0] = rs.getString("typeData");
                typeData[1] = rs.getString("finalData");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return typeData;
    }
}
