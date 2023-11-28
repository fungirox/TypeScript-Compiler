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
            Statement statement =  connection.createStatement();
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
            for (int i=0;i<native_functions.length;i++){
                statement.execute(
                        "INSERT INTO a20130044.ambito " +
                                "(nameId,typeId,classId,ambito,cantParametro,typeParametro)" +
                                "VALUES  "+native_functions[i]);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public void deleteNativeFunctions(){
        try (Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM a20130044.ambito WHERE declarationID <= "+native_functions.length);
        } catch (SQLException e) {
            System.out.println(e);
        }

    }
    public void addMember(MemberDetails mb){
        try {
            Statement statement =  connection.createStatement();
            String arrow = mb.getClassId().equals("arrow fuction") ? "number" : mb.getType();
            statement.execute("INSERT INTO a20130044.ambito (nameId,typeId,classId,ambito,cantParametro," +
                    "typeParametro,arrayDimension,arrayLength) VALUES ('"+mb.getId()+"','"+arrow+"'," +
                    "'"+mb.getClassId()+"','"+mb.getAmbito()+"','"+mb.getCantParametro()+"','"+mb.getTypeParametro()+"" +
                    "','"+mb.getArrayDimension()+"','"+mb.getArrayToJson()+"');");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public void addTemporal(Operand operand){
//        System.out.println("jojo "+operand);
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
    public String getOneIDClass(final int ambito,final String id){
        String classId="";
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT classId FROM ambito WHERE nameId = '" + id + "' AND ambito = " + ambito;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente
            while (rs.next()) {
                classId = rs.getString("classId");
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return classId;
    }
    public String getOneIDType(final int ambito,final String id){
        String type = "";
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT typeId FROM ambito WHERE nameId = '" + id + "' AND ambito = " + ambito;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente
            while (rs.next()) {
                type = rs.getString("typeId");
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return type;
    }
    public int getArrayFuction(final int ambito,final String id){
        int type = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT ambito,type, FROM ambito WHERE nameId = '" + id + "' AND ambito = " + ambito;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente
            while (rs.next()) {
                type = rs.getInt("cantParametro");
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return type;
    }
    public boolean isArray (final int ambito, final String id){
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM ambito WHERE nameId = '" + id + "' AND classId = 'Array' AND ambito = " + ambito;
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
    public void updateTypeMember(final String type){
        try {
            Statement statement =  connection.createStatement();
            statement.execute("UPDATE ambito SET typeId = '"+type+"' WHERE declarationID = "+ getLastIdAmbito()+";");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public void updateTypeMemberClass(final String type){
        try {
            Statement statement =  connection.createStatement();
            statement.execute("UPDATE ambito SET classId = '"+type+"' WHERE declarationID = "+ getLastIdAmbito()+";");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public void deleteLastTemporal(){
        try {
            Statement statement =  connection.createStatement();
            statement.execute("DELETE FROM temporals WHERE temporalsID = "+ getLastIdTemporals()+";");
        } catch (SQLException e) {
            System.out.println(e);
        }
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
    private int getLastIdTemporals(){
        int lastId = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT MAX(temporalsID) AS last_id FROM temporals";
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
    private int getLastIdAmbito(){
        int lastId = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT MAX(declarationID) AS last_id FROM ambito";
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
    public int getDeclarationID(final int ambito,final String id){
        int declarationID = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT declarationID FROM ambito WHERE ambito = "+ambito+" AND nameId = '"+id+"'";
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            while (rs.next()) {
                declarationID = rs.getInt("declarationID");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return declarationID;
    }
    public int getTempTypeLineAsig(final int line, final int type){
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
    public int getTempTypeLine(final int type){
        int typePerLine = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(temporals.typeNumber) AS count_temporals_type FROM temporals WHERE temporals.typeNumber = '"+type+"'  GROUP BY temporals.typeNumber;";
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
    public boolean getRealNumber(final int line){
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM asignations WHERE type = 1 AND finalDataType = 0 AND line = "+line;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente

            if (rs.next()) {
                return true;
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
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

    public int getArrayLengthPosition(final int ambito,final String id,final int position){
        int arrayLength = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT JSON_EXTRACT(arrayLength, '$["+position+"]') as arrayL FROM ambito WHERE ambito = "+ambito+" AND nameId = '"+id+"'";
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente
            while (rs.next()) {
                arrayLength = rs.getInt("arrayL");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return arrayLength;
    }
    public int getAmbito(final int declarationID){
        int type = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT ambito FROM ambito WHERE declarationID = "+declarationID;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente
            while (rs.next()) {
                type = rs.getInt("ambito");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return type;
    }
    public int getArrayDimension(final int declarationID){
        int arrayDimension = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT arrayDimension FROM ambito WHERE declarationID = "+declarationID;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente
            while (rs.next()) {
                arrayDimension = rs.getInt("arrayDimension");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return arrayDimension;
    }
    public String getClass(final int declarationID){
        String classId="";
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT classId FROM ambito WHERE declarationID = "+declarationID;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente
            while (rs.next()) {
                classId = rs.getString("classId");
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return classId;
    }
    public String getType(final int declarationID){
        String type = "";
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT typeId FROM ambito WHERE declarationID = "+declarationID;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente
            while (rs.next()) {
                type = rs.getString("typeId");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return type;
    }
    public int getCantParametro(final int declarationID){
        int cantParametro = 0;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT cantParametro FROM ambito WHERE declarationID = "+declarationID;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente
            while (rs.next()) {
                cantParametro = rs.getInt("cantParametro");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return cantParametro;
    }
    public String getTypeParametro(final int declarationID){
        String typeParametro = "";
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT typeParametro FROM ambito WHERE declarationID = "+declarationID;
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente
            while (rs.next()) {
                typeParametro = rs.getString("typeParametro");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return typeParametro;
    }
    public String getTypeOfAParameter(final int cantParametro,final int ambito, final String typeParametro){
        String typeId = "";
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT typeId FROM ambito WHERE cantParametro = "+cantParametro+" AND ambito = "+ambito+" AND typeParametro = '"+typeParametro+"'";
            ResultSet rs = statement.executeQuery(query);  // Cambiado a executeQuery directamente
            while (rs.next()) {
                typeId = rs.getString("typeId");
            }

            // Cerrar el ResultSet y el Statement después de su uso
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return typeId;
    }
    final String [] native_functions = {
            // toLowerCase
            "('toLowerCase','string','fuction',0,1,'-1')",
            "('par1','string','variable',-1,1,'toLowerCase')",
            // toUpperCase
            "('toUpperCase','string','fuction',0,1,'-2')",
            "('par1','string','variable',-2,1,'toUpperCase')",
            // legth
            "('legth','number','fuction',0,1,'-3')",
            "('par1','string','variable',-3,1,'legth')",
            // trim
            "('trim','string','fuction',0,1,'-4')",
            "('par1','string','variable',-4,1,'trim')",
//            "('par2','string','variable',-4,2,'trim')",
            // charAt
            "('charAt','string','fuction',0,2,'-5')",
            "('par1','string','variable',-5,1,'charAt')",
            "('par2','number','variable',-5,2,'charAt')",
            // startsWith
            "('startsWith','boolean','fuction',0,2,'-6')",
            "('par1','string','variable',-6,1,'startsWith')",
            "('par2','string','variable',-6,2,'startsWith')",
            // endsWith
            "('endsWith','boolean','fuction',0,2,'-7')",
            "('par1','string','variable',-7,1,'endsWith')",
            "('par2','string','variable',-7,2,'endsWith')",
            // indexOf
            "('indexOf','number','fuction',0,2,'-8')",
            "('par1','string','variable',-8,1,'indexOf')",
            "('par2','string','variable',-8,2,'indexOf')",
            // includes
            "('includes','boolean','fuction',0,2,'-9')",
            "('par1','string','variable',-9,1,'includes')",
            "('par2','string','variable',-9,2,'includes')",
            // slice
            "('slice','string','fuction',0,3,'-10')",
            "('par1','string','variable',-10,1,'slice')",
            "('par2','number','variable',-10,2,'slice')",
            "('par3','number','variable',-10,3,'slice')",
            // replace
            "('replace','string','fuction',0,3,'-11')",
            "('par1','string','variable',-11,1,'replace')",
            "('par2','string','variable',-11,2,'replace')",
            "('par3','string','variable',-11,3,'replace')",
            // split
            "('split','string','fuction',0,2,'-12')",
            "('par1','string','variable',-12,1,'split')",
            "('par2','string','variable',-12,2,'split')",

            // expo
            "('expo','number','fuction',0,2,'-13')",
            "('par1','real','variable',-13,1,'expo')",
            "('par2','number','variable',-13,2,'expo')",
            // sqrtv
            "('sqrtv','real','fuction',0,2,'-14')",
            "('par1','real','variable',-14,1,'sqrtv')",
            "('par2','number','variable',-14,2,'sqrtv')",
            // ConvBase
            "('ConvBase','void','fuction',0,3,'-15')",
            "('par1','string','variable',-15,1,'ConvBase')",
            "('par2','number','variable',-15,2,'ConvBase')",
            "('par3','number','variable',-15,3,'ConvBase')",
            // asc
            "('asc','string','fuction',0,1,'-16')",
            "('par1','number','variable',-16,1,'asc')",
            // sen
            "('sen','real','fuction',0,1,'-17')",
            "('par1','real','variable',-17,1,'sen')",
            // cos
            "('cos','real','fuction',0,1,'-18')",
            "('par1','real','variable',-18,1,'cos')",
            // tan
            "('tan','real','fuction',0,1,'-19')",
            "('par1','real','variable',-19,1,'tan')",
            // val
            "('val','number','fuction',0,1,'-20')",
            "('par1','string','variable',-20,1,'val')",

    };
}