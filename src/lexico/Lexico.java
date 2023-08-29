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
    private LinkedList<Token> tokenListLexico = new LinkedList<>();
    private LinkedList<Token> tokenListSintaxis;
    private LinkedList<Errores> erroresList;
    private int []contadores;
    private int lastToken=-58;
    private int linea=1;
    private int lineaComentario=0;
    private boolean comentario=false;

//    Map<Integer,String> errores=new HashMap<Integer,String>();
    private JTable /*tblErrores,*/tblTokens,tblContadores;
    private DefaultTableModel mdTblErrores,mdTblTokens,mdTblContadores;
    public Lexico(int[][] matrizLexico,final String text,final JTable tblTokens/*,JTable tblErrores*/,final JTable tblContadores,LinkedList<Errores>listErrores,LinkedList<Token>sintaxis,int[]contadores){
        this.matrizLexico=matrizLexico;
        this.text=text;
        this.tblTokens=tblTokens;
        this.mdTblTokens=(DefaultTableModel) tblTokens.getModel();
        this.mdTblContadores=(DefaultTableModel) tblContadores.getModel();
        this.erroresList=listErrores;
        this.tokenListSintaxis=sintaxis;
        this.contadores=contadores;
    }
    public Object[] compilar(){
        int estado=0,col=0;
        char charac;
        String lexema="";
        for(int i=0;i<text.length();i++){
            //Selecciona el caracter actual
            charac=text.charAt(i);
            col=getCol(charac);
            estado=matrizLexico[estado][col];

            if(estado<0){//Si es Token
                if(estado==-1&&palabras_reservadas.containsKey(lexema)){//Evaluar palabras reservadas
                    estado=lastToken-palabras_reservadas.get(lexema);
                }
                tokenListLexico.add(new Token(lexema,estado,linea));
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

        return new Object[]{tokenListSintaxis,erroresList};
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
            case -59:
            case -60:
                //booleanas
                contadores[17]++;
                return true;
            case -61:
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
        llenarTablaTokens();
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
                case '\n'->33;
                case ' '->34;
                case '\t'->35;
                case '#'->31;
                default ->32;
            };
        }
    }

    public void setText(String text) {
        this.text = text;
    }



    private final Map<String,Integer> palabras_reservadas=new HashMap<String,Integer>()
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
        put("Console",10);
        put("log",11);
        put("fuction",12);
        put("real",13);
        put("boolean",14);
        put("Array",15);
        put("new",16);
        put("read",17);
        put("case",18);
        put("default",19);
        put("return",20);
        put("expo",21);
        put("sqrtv",22);
        put("ConvBase",23);
        put("asc",24);
        put("sen",25);
        put("val",26);
        put("cos",27);
        put("tan",28);
        put("break",29);
        put("let",30);
        put("interface",31);
        put("number",32);
        put("string",33);
        put("set",34);
        put("get",35);
        put("class",36);
        put("toLowerCase",37);
        put("toUpperCase",38);
        put("legth",39);
        put("trim",40);
        put("charAt",41);
        put("startsWith",42);
        put("endsWith",43);
        put("indexOf",44);
        put("includes",45);
        put("slice",46);
        put("replace",47);
        put("split",48);
        put("in",49);
        put("of",50);
        put("Map",51);
        put("forEach",52);
        put("any",53);
        put("const",54);
        put("continue",55);
        put("concat",56);
        put("find",57);
        put("findIndex",58);
        put("filter",59);
        put("push",60);
        put("shift",61);
        put("sort",62);
        put("reverse",63);
        put("splice",64);
        put("typeof",65);
        put("undefined",66);
    }};

}
