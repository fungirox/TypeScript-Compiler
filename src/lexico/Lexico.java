package lexico;

import resources.CargarRecursos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Lexico {
    private int[][] matrizLexico;
    private String text;
    private LinkedList<Token> tokenListLexico =new LinkedList<>();
    private LinkedList<Token> tokenListSintaxis =new LinkedList<>();
    private LinkedList<Errores> erroresList =new LinkedList<>();
    private int []contadores=new int[21];
    private int lastToken=-57;
    private int linea=1;
    private int lineaComentario=0;
    private boolean comentario=false;
//    Map<Integer,String> errores=new HashMap<Integer,String>();
    private JTable tblErrores,tblTokens,tblContadores;
    private DefaultTableModel mdTblErrores,mdTblTokens,mdTblContadores;
    public Lexico(int[][] matrizLexico,final String text,JTable tblTokens,JTable tblErrores,JTable tblContadores){
        this.matrizLexico=matrizLexico;
        this.text=text;
        this.tblErrores=tblErrores;
        this.tblTokens=tblTokens;
        this.mdTblErrores=(DefaultTableModel) tblErrores.getModel();
        this.mdTblTokens=(DefaultTableModel) tblTokens.getModel();
        this.mdTblContadores=(DefaultTableModel) tblContadores.getModel();
    }
    public void compilar(){
        int estado=0,col=0;
        char charac;
        String lexema="";
        for(int i=0;i<text.length();i++){
            //Selecciona el caracter actual
            charac=text.charAt(i);
            col=getCol(charac);
//            System.out.print(charac+" "+col+" ");
            estado=matrizLexico[estado][col];

            if(estado<0){//Si es Token
                if(estado==-1&&palabras_reservadas.containsKey(lexema)){//Evaluar palabras reservadas
                    estado=lastToken-palabras_reservadas.get(lexema);
                }
//                int resta=comentario?lineaComentario:0;
//                comentario=comentario?false:comentario;
//                System.out.println("L "+linea);
//                System.out.println("LC "+lineaComentario);
//                System.out.print(resta+"\n ------------");
                tokenListLexico.add(new Token(lexema,estado,linea));
//                System.out.print(lexema);
                if(categorizarTokens(estado)){
                    tokenListSintaxis.add(tokenListLexico.getLast());
                }
                else if (estado==-54){
                    comentario=false;
                    tokenListLexico.getLast().setLinea(lineaComentario);
                }
                estado=0;
                lexema="";
                i--;
            }
            else if(estado>=500&&estado<=599){//Si es error de léxico
                if(estado!=500){
                    i--;
                }
                else{
                    lexema+=charac;
                }
                erroresList.add(new Errores(lexema,estado,linea));
                estado=0;
                lexema="";
                contadores[20]++;
            }
            else if(estado!=0){
                lexema+=charac;
                if(charac=='\n'){
                    linea++;
                }
                if(lexema.equals("/*")&&!comentario){
                    comentario=true;
                    lineaComentario=linea;
                }
            }
            else if(charac=='\n'){
                linea++;
            }

        }
        if(estado!=0){
            erroresList.add(new Errores(lexema,estado,linea));
            estado=0;
            lexema="";
            contadores[20]++;
        }
        llenarTablas();
    }
    private boolean categorizarTokens(int token){
        switch (token){
            case -1:
                //Identificadores
                contadores[19]++;
                return true;
            case -54:
            case -53:
                //Comentarios
                contadores[13]++;
                return false;
                //Palabras reservadas está en default
            case -46:
            case -47:
                //Constantes de cadena
                contadores[14]++;
                return true;
            case -55:
                //Constantes numericas
                contadores[15]++;
                return true;
            case -56:
            case -57:
                //Constantes reales
                contadores[16]++;
                return true;
            case -58:
            case -59:
                //booleanas
                contadores[17]++;
                return true;
            case -60:
                //nulas
                contadores[18]++;
                return true;
            case -36:
            case -39:
                //Postfix
                contadores[0]++;
                return true;
            case -5:
            case -21:
            case -22:
            case -24:
                //binarios
                contadores[1]++;
                return true;
            case -12:
            case -13:
            case -14:
            case -16:
                //de control
                contadores[2]++;
                return true;
            case -8:
            case -34:
            case -37:
            case -48:
            case -51:
                //matemáticos
                contadores[3]++;
                return true;
            case -49:
                //exponente
                contadores[4]++;
                return true;
            case -27:
            case -41:
            case -43:
                //turno
                contadores[5]++;
                return true;
            case -3:
            case -26:
            case -29:
            case -31:
            case -40:
            case -42:
                //relacionales
                contadores[6]++;
                return true;
            case -4:
            case -32:
                //sin igualdad
                contadores[7]++;
                return true;
            case -2:
            case -6:
            case -25:
                //logicos
                contadores[8]++;
                return true;
            case -15:
                //ternario
                contadores[9]++;
                return true;
            case -7:
            case -9:
            case -23:
            case -28:
            case -30:
            case -33:
            case -35:
            case -38:
            case -44:
            case -45:
            case -50:
            case -52:
                //asignacion
                contadores[10]++;
                return true;
            case -10:
            case -11:
            case -17:
            case -18:
            case -19:
            case -20:
                //agrupamiento
                contadores[11]++;
                return true;
            //Faltan las palabras reservadas son el 14
            default:
                contadores[12]++;
                return true;
        }

    }
    public void clean(){
        text="";
        mdTblErrores.setRowCount(0);
        mdTblTokens.setRowCount(0);
        erroresList.clear();
        tokenListLexico.clear();
        tokenListSintaxis.clear();
        for (int i=0;i<contadores.length;i++){
            contadores[i]=0;
        }
        linea=1;
    }
    private void llenarTablas(){
        llenarTablaError();
        llenarTablaTokens();
        llenarContadores();
    }
    private void llenarContadores(){
        for (int i=0;i<contadores.length;i++){
            mdTblContadores.setValueAt(contadores[i],i,1);
        }
    }
    private void llenarTablaError(){
        for(int i = 0; i< erroresList.size(); i++){
            mdTblErrores.addRow(erroresList.get(i).getRow());
        }
    }
    private void llenarTablaTokens(){
        for(int i = 0; i< tokenListLexico.size(); i++){
            mdTblTokens.addRow(tokenListLexico.get(i).getRow());
        }
    }
    private int getCol(final char charac){
        if(charac>='a'&&charac<='z'){
            return 0;
        }
        else if (charac>='A'&&charac<='Z'){
            return 1;
        }
        else if(charac>='0'&&charac<='9'){
            return 29;
        }
        else {
            return switch (charac){
                case '@'->2;
                case '!'->3;
                case '&'->4;
                case '%'->5;
                case '('->6;
                case ')'->7;
                case '.'->8;
                case ','->9;
                case ':'->10;
                case ';'->11;
                case '?'->12;
                case '['->13;
                case ']'->14;
                case '{'->15;
                case '}'->16;
                case '~'->17;
                case '^'->18;
                case '|'->19;
                case '<'->20;
                case '='->21;
                case '+'->22;
                case '-'->23;
                case '>'->24;
                case '"'->25;
                case '\''->26;
                case '*'->27;
                case '/'->28;
                case '_'->30;
                case '\n'->32;
                case ' '->33;
                case '\t'->34;
                default ->31;
            };
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public void writeExcel(final String path){
        CargarRecursos.writeToExcel(tokenListSintaxis,erroresList,contadores,path);
    }

    private final Map<String,Integer> palabras_reservadas= new HashMap<String,Integer>()
    {{
        put("true",1);
        put("false",2);
        put("null",3);
        put("if",4);
        put("else",5);
        put("switch",6);
        put("for",7);
        put("do",8);
        put("while",9);
        put("console",10);
        put("log",11);
        put("forEach",12);
        put("break",13);
        put("continue",14);
        put("let",15);
        put("const",16);
        put("undefined",17);
        put("interface",18);
        put("typeof",19);
        put("Number",20);
        put("String",21);
        put("any",22);
        put("set",23);
        put("get",24);
        put("class",25);
        put("toLowerCase",26);
        put("toUpperCase",27);
        put("length",28);
        put("trim",29);
        put("charAt",30);
        put("startsWith",31);
        put("endsWith",32);
        put("indexOf",33);
        put("Includes",34);
        put("slice",35);
        put("replace",36);
        put("split",37);
        put("push",38);
        put("shift",39);
        put("in",40);
        put("of",41);
        put("splice",42);
        put("concat",43);
        put("find",44);
        put("findIndex",45);
        put("filter",46);
        put("map",47);
        put("sort",48);
        put("reverse",49);
    }};

}
