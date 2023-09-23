package resources;


import lexico.Errores;
import lexico.Token;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class CargarRecursos {
    public static SqlQuerys connectionSQL = new SqlQuerys();
    public static BufferedImage compatibleImageOPAQUE(final String ruta){
        Image imagen;
        BufferedImage imagenBuff=null;
        try {
            imagen= ImageIO.read(new File(ruta));
            GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            imagenBuff=gc.createCompatibleImage(imagen.getWidth(null),imagen.getHeight(null),Transparency.OPAQUE);
            Graphics g=imagenBuff.getGraphics();
            g.drawImage(imagen,0,0,null);
            g.dispose();
        } catch (IOException ex) {}

        return imagenBuff;
    }
    public static BufferedImage compatibleImageTRANSLUCENT(final String ruta){
        Image imagen=null;

        try {
            imagen=ImageIO.read(new File(ruta));
        } catch (IOException ex) {}
        GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage imagenBuff=gc.createCompatibleImage(imagen.getWidth(null),imagen.getHeight(null),Transparency.TRANSLUCENT);
        Graphics g=imagenBuff.getGraphics();
        g.drawImage(imagen,0,0,null);
        g.dispose();
        return imagenBuff;
    }
    public static String openFile(final String path){
        String con="";
        InputStream entradaBytes=null;
        try {
            entradaBytes = new FileInputStream(path);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        BufferedReader lector=new BufferedReader(new InputStreamReader(entradaBytes));
        String linea;
        try{
            while((linea=lector.readLine())!=null){//Se atribuye
                con+=linea+'\n';
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(entradaBytes!=null){
                    entradaBytes.close();
                }
                if(lector!=null){
                    lector.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return con;
    }
    public static int [][] openExcelFileLexico(final String filePath){
        int [][] matriz=new int[67][36];
        try {
            FileInputStream fileInputStream=new FileInputStream(new File(filePath));
            XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);

            XSSFSheet hssfSheet=workBook.getSheetAt(0);

            int rows=hssfSheet.getLastRowNum();

            for (int i=1;i<=rows;i++){
                Row fila =hssfSheet.getRow(i);
                int col=fila.getLastCellNum();
                for (int j=1;j<col;j++){
                    Cell cell=fila.getCell(j);
                    switch (cell.getCellTypeEnum().toString()){
                        case "NUMERIC":
                            int aux=(int)cell.getNumericCellValue();
                            matriz[i-1][j-1]=aux;
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matriz;
    }
    public static int [][] openExcelFileSintaxis(final String filePath){
        int [][] matriz=new int[93][125];

        try {
            FileInputStream fileInputStream=new FileInputStream(new File(filePath));
            XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);

            XSSFSheet hssfSheet=workBook.getSheetAt(0);

            int rows=hssfSheet.getLastRowNum();

            for (int i=1;i<=rows;i++){
                Row fila =hssfSheet.getRow(i);
                int col=fila.getLastCellNum();
                for (int j=2;j<col;j++){
                    Cell cell=fila.getCell(j);
                    switch (cell.getCellTypeEnum().toString()){
                        case "NUMERIC":
                            int aux=(int)cell.getNumericCellValue();
                            matriz[i-1][j-2]=aux;
                            break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return matriz;
    }
    public static void writeToExcel(final LinkedList listaToken,final LinkedList listaErrores,final int[] contadores,final int lexico, final int sintaxis,final String path){
        XSSFWorkbook book=new XSSFWorkbook();
        XSSFSheet tokenSheet=book.createSheet("Token");
        Row headerRow1=tokenSheet.createRow(0);
        /*
         * Tokens
         */
        //headers
        for(int i=0;i<rowHeadTokens.length;i++){
            Cell cell=headerRow1.createCell(i);
            cell.setCellValue(rowHeadTokens[i]);
        }
        //data rows
        for(int i=0;i<listaToken.size();i++){
            Row dataRow = tokenSheet.createRow(i+1);
            Token aux = (Token) listaToken.get(i);
            dataRow.createCell(0).setCellValue(aux.getToken());
            dataRow.createCell(1).setCellValue(aux.getLexema());
            dataRow.createCell(2).setCellValue(aux.getLinea());
        }
        /*
         * Errores
         */
        XSSFSheet erroresSheet=book.createSheet("Errores");
        Row headerRow2=erroresSheet.createRow(0);
        //headers
        for(int i=0;i<rowHeadErrores.length;i++){
            Cell cell=headerRow2.createCell(i);
            cell.setCellValue(rowHeadErrores[i]);
        }
        //data rows
        for(int i=0;i<listaErrores.size();i++){
            Row dataRow = erroresSheet.createRow(i+1);
            Errores aux1 = (Errores) listaErrores.get(i);
            dataRow.createCell(0).setCellValue(aux1.getToken());
            dataRow.createCell(1).setCellValue(aux1.getDesc());
            dataRow.createCell(2).setCellValue(aux1.getLexema());
            dataRow.createCell(3).setCellValue(aux1.getTipo());
            dataRow.createCell(4).setCellValue(aux1.getLinea());
        }
        /*
         * Contadores
         */
        XSSFSheet contadoresSheet=book.createSheet("Contadores");
        Row headerRow3=contadoresSheet.createRow(0);
        //headers
        for(int i=0;i<rowHeadContadores.length;i++){
            Cell cell=headerRow3.createCell(i);
            cell.setCellValue(rowHeadContadores[i]);
        }
        //data rows
        Row dataRow=contadoresSheet.createRow(1);
        for(int i=0;i<contadores.length;i++){
            dataRow.createCell(i).setCellValue(contadores[i]);
        }
        /**
         * Tipos de error
         */
        XSSFSheet tiposErrorSheet=book.createSheet("Tipos de error");
        Row tiposErrorR=tiposErrorSheet.createRow(0);
        //headers
        for(int i=0;i<rowHeadTER.length;i++){
            Cell cell=tiposErrorR.createCell(i);
            cell.setCellValue(rowHeadTER[i]);
        }

        //data rows
        Row dataRowTER = tiposErrorSheet.createRow(1);
            dataRowTER.createCell(0).setCellValue(lexico);
            dataRowTER.createCell(1).setCellValue(sintaxis);

        /**
         * Ambito
         * */
        XSSFSheet ambitoSheet=book.createSheet("Ambito");
        Row ambitoRows=ambitoSheet.createRow(0);
        //headers
        for(int i=0;i<rowHeadAmbito.length;i++){
            Cell cell=ambitoRows.createCell(i);
            cell.setCellValue(rowHeadAmbito[i]);
        }
        writeAmbitoExcel(ambitoSheet);

                /**= ambitoSheet.createRow(1);
        dataRowAmbito.createCell(0).setCellValue(lexico);
        dataRowAmbito.createCell(1).setCellValue(sintaxis);
            */

        /*
         *  Escribir archivo
         */
        FileOutputStream out;
        try {
            out=new FileOutputStream(new File(path+".xlsx"));
            book.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeAmbitoExcel(XSSFSheet ambitoSheet){
        Row dataRow;
        int ambitos=connectionSQL.ambitos();
        for(int i=0;i<ambitos;i++){
            //Añadir ambito
            dataRow = ambitoSheet.createRow(i+1);
            dataRow.createCell(0).setCellValue(i);
            dataRow.createCell(1).setCellValue(connectionSQL.calculateType("string",i));
            dataRow.createCell(2).setCellValue(connectionSQL.calculateType("number",i));
            dataRow.createCell(3).setCellValue(connectionSQL.calculateType("boolean",i));
            dataRow.createCell(4).setCellValue(connectionSQL.calculateType("real",i));
            dataRow.createCell(5).setCellValue(connectionSQL.calculateType("null",i));
            dataRow.createCell(6).setCellValue(connectionSQL.calculateType("void",i));
            dataRow.createCell(7).setCellValue(connectionSQL.classType(i));
        }

    }


    public final static String [] rowHeadContadores={
            "Postfix","Binarios","Control","Matemáticos",
            "Exponentes","Turno","Relacionales",
            "Sin igualdad","Lógicos","Ternario",
            "Asignación","Agrupamiento","Reservadas",
            "Comentarios","Cadenas","Enteros","Reales",
            "Booleans","Nulls","Identificadores","Errores"
    };
    private final static String [] rowHeadTokens={
            "State","Lexema","Linea"
    };
    private final static String [] rowHeadErrores={
            "Token","Descripcion","Lexema","Tipo de error","Linea"
    };
    private final static String [] rowHeadAmbito={
            "ambito","string","number","boolean","real","null",
            "void","# id","errores","total"
    };
    private final static String [] rowHeadTER={
            "Lexico","Sintaxis"
    };

}
