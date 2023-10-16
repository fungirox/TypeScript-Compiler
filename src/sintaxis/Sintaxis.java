package sintaxis;

import ambito.Ambito;
import ambito.Area;
import ambito.State;
import ambito.MemberDetails;
import lexico.Errores;
import lexico.Token;
import resources.CargarRecursos;
import resources.SqlQuerys;
import semantica.*;

import java.io.IOException;
import java.util.*;

public class Sintaxis {
    private final SqlQuerys sqlQuerys;
    private final int[][] matrizSintactica;
    private final int [][][] matrizSemantica;
    private final LinkedList <Token> tokenList;
    private final LinkedList <Errores> erroresList;
    private final LinkedList <Area> areasList;
    private final LinkedList <MemberDetails> memberDetailsList;
    private final LinkedList <Semantica> semanticaRulesList;
    private final Stack <Operand> operandsStack;
    private final Stack <Operator> operatorStack;
    private final Stack <Integer> syntacticStack;
    private final Stack <Ambito> ambitoStack;
    private final Stack <State> generalStateStack;
    private final Stack <Boolean> switchTypeStack;
    private final ArrayList <Integer> arrayLength;
    private int ambito;
    private int erroresAmbito;
    private int parametro = 0;
    private String stringTxt ="";
    private String plusminus = "temp";
    private String memberString;
    private String letID;
    private boolean contieneParametro = false;
    private int memberPositionVar;
    private int memberPositionClass;
    private int excelSheetOperator;
    private int operatorPriority;
    private boolean let = false;
    private boolean classOVar = false;
    private boolean anon = false;
    private boolean errorAmbito = false;
    private boolean posfix = false;
    private boolean switchError = false;
    private boolean forSentence = false;
    private boolean forEach = false;
    private Operator operator;
    private StatusState statusState;
    private SematicaState sematicaState;
    private SematicaState sematicaStateAux;

    public Sintaxis(final int [][]matriz,final LinkedList<Errores> listErrores,final LinkedList<Token>sintaxis,final int [][][]matrizSemantica,final LinkedList<Semantica> semanticaLinkedList){
        this.matrizSintactica = matriz;
        this.tokenList = sintaxis;
        this.erroresList = listErrores;
        this.syntacticStack = new Stack<>();
        this.generalStateStack = new Stack<>();
        this.ambitoStack = new Stack<>();
        this.memberDetailsList = new LinkedList<>();
        this.areasList = new LinkedList<>();
        this.arrayLength = new ArrayList<>();
        this.semanticaRulesList = semanticaLinkedList;
        this.matrizSemantica = matrizSemantica;
        this.sqlQuerys = CargarRecursos.connectionSQL;
        this.statusState = StatusState.NONE;
        this.operandsStack = new Stack<>();
        this.operatorStack = new Stack<>();
        this.switchTypeStack = new Stack<>();
        syntacticStack.push(200);
        generalStateStack.push(State.NONE);
        sematicaState = SematicaState.NONE;
        ambito = 0;

    }
    public int analize() throws IOException {
        int matrizData;
        while(!tokenList.isEmpty()&&!syntacticStack.isEmpty()){
//            System.out.println(tokenList.getFirst().getLexema()+" line: "+tokenList.getFirst().getLinea()+" generalState: "+stateStack.peek()+" error: "+ errorAmbito +" topStack: "+syntacticStack.peek()+" classOVar: "+classOVar+" ambito "+ (ambitoStack.isEmpty() ? "vacio":ambitoStack.peek().getNumber()));
            System.out.println(tokenList.getFirst().getLexema()+" line: "+tokenList.getFirst().getLinea()+" statusState "+statusState+" error: "+errorAmbito+" topStack: "+syntacticStack.peek()+" sematicState: "+sematicaState);
            if(syntacticStack.peek()>=200&&syntacticStack.peek()<=292){ // Esto quiere decir que es un NO terminal

                matrizData = mapearToken();

                if(matrizData>499){ // Caso Error
                    erroresList.add(new Errores(tokenList.getFirst().getLexema(), tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),errores_sintaxis.get(matrizData),"Error sintactico"));
                    tokenList.removeFirst();
                }
                else if(matrizData==183){ // Caso epsilon
                    syntacticStack.pop();
                }
                else { // Caso produccion
                    syntacticStack.pop();
                    addProduction(matrizData);
                }

            }
            else if(syntacticStack.peek()>=1000 && syntacticStack.peek()<1500) { // Declaracion de miembros // Apertura de ambitos, areas de declaracion y ejecución // Estados de OR
                codeState(syntacticStack.peek());
                syntacticStack.pop();

            }
            else if(syntacticStack.peek()<0){ //Esto quiere decir que es un token
                if(tokenList.getFirst().getToken()==syntacticStack.peek()||(tokenList.getFirst().getToken()==(-47)&&syntacticStack.peek()==(-46))||(tokenList.getFirst().getToken()==(-57)&&syntacticStack.peek()==(-56))){//Si el token de la lista y pila son iguales y Caso especifico de cadenas -47 y reales -57
                    if (!errorAmbito){
                        if(generalStateStack.peek()==State.STATUS){
                            execute();
                        }
                        else {
                            declaration();
                        }
                    }

                    delete();
                }
                else {
                    System.out.println("Error de fuerza bruta, linea: "+ tokenList.getFirst().getLinea()+" lexema: "+ tokenList.getFirst().getLexema());
                    delete();
                }
            }
            System.out.println("O P E R A N D S   S T A C K");
            printOperandStack();
            System.out.println("O P E R A T O R S   S T A C K");
            printOperatorStack();
            System.out.println("------------------------------------------------------------------------------------------------------------------------");
        }

        if(!syntacticStack.isEmpty()) {
            System.out.println("Parece que no terminaste tu codigo");
        }
        System.out.println();
        printSemanticaRules();

        return erroresAmbito;
    }
    private void execute(){
        if (tokenList.getFirst().getToken() == -1) { // Si es ID
            if (!findMember(ambitoStack,tokenList.getFirst().getLexema())){
                erroresList.add(new Errores(tokenList.getFirst().getLexema(), tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),"Elemento no declarado","Error de ámbito ("+ambitoStack.peek().getNumber()+")",ambitoStack.peek().getNumber()));
                erroresAmbito++;
                operandsStack.push(new Operand("No declarado",tokenList.getFirst().getToken(),6,tokenList.getFirst().getLinea()));
                return;
            }

        }

        // Empieza semantica
        // Añade a la pila de operadores y operandos elementos

        switch (statusState){
            case OPERAND -> {
                int operandType;
                if (posfix && tokenList.getFirst().getToken() == -1 && sematicaState == SematicaState.ASIG) {
                    sqlQuerys.addAsignations(tokenList.getFirst().getLexema(),plusminus,findMemberType(ambitoStack,tokenList.getFirst().getLexema()),tokenList.getFirst().getLinea(),String.valueOf(sqlQuerys.getOneIDType(ambitoStack.peek().getNumber(),tokenList.getFirst().getLexema())));

                    int returnExcel = matrizSemantica[1][0][findMemberType(ambitoStack,tokenList.getFirst().getLexema())];
                    Operand op  = switch (returnExcel){
                        case -90 -> new Operand("temp number",-90,0,tokenList.getFirst().getLinea());   // NUMBER
                        case -71 -> new Operand("temp real",-71,1,tokenList.getFirst().getLinea());   // REAL
                        case -72 -> new Operand("temp boolean",-72,2,tokenList.getFirst().getLinea());   // boolean
                        case -91 -> new Operand("temp string",-91,3,tokenList.getFirst().getLinea());   // string
                        case -61 -> new Operand("temp null",-61,4,tokenList.getFirst().getLinea());   // null
                        default -> new Operand("temp var",-200,6,tokenList.getFirst().getLinea());   // var
                    };
                    sqlQuerys.addTemporal(op);
                    sqlQuerys.updateAsignations(op.getLexema(),op.getType());

                    boolean state = true;
                    String symbol = sqlQuerys.getAsignationOperator(op.getLine());
                    String [] a = sqlQuerys.getTypeDataAsignation(op.getLine());

                    if(sqlQuerys.getErrorSemantica(op.getLine())){ // Ver si son del mismo tipo la asignación y el resultado final
                        erroresList.add(new Errores(sqlQuerys.getNameIdAsignation(op.getLine()),-1,op.getLine(),"Incompatibilidad de tipos","Error semantico"));
                        state = false;
                    }
                    semanticaRulesList.add(new Semantica(forSentence ? 1082 : symbol.equals("=") ? 1020 : symbol.equals("+=") ? 1021 : 1022,
                            op.getLine(),ambitoStack.peek().getNumber(),a[0],op.getDataType()+"",state));

                    operatorStack.clear();
                    sematicaState = sematicaStateAux;
                    statusState = StatusState.NONE;
                    posfix = false;

                    if (sematicaState == SematicaState.IF || sematicaState == SematicaState.WHILE || sematicaState == SematicaState.DOWHILE){
                        operandsStack.push(op);
                    }
                    break;
                }

                operandType =  tokenList.getFirst().getToken() == -1 ? findMemberType(ambitoStack,tokenList.getFirst().getLexema()) : idTypeToken(tokenList.getFirst().getToken());
                operandsStack.push(new Operand(tokenList.getFirst().getLexema(),tokenList.getFirst().getToken(),operandType,tokenList.getFirst().getLinea()));


            }
            case OPERATOR -> { // temporal
                operator = new Operator(tokenList.getFirst().getLexema(),tokenList.getFirst().getToken(),excelSheetOperator,tokenList.getFirst().getLinea(),operatorPriority);

                if (operatorStack.isEmpty()){ // Si la pila de operadores está vacia
                    operatorStack.push(operator);
                }
                else {
                    // Hay que recorrer la pila aquí
                    while (!operatorStack.isEmpty()){
                        if(operator.getPriority() <= operatorStack.peek().getPriority()){
                            // Se mete a la pila pq al final al vaciarla se realizarian primero
                            operatorStack.push(operator);
                            break;
                        }
                        else {
                            // Operator se queda intacto pq después se metera a la pila
                            // Se necesita obtener los ultimos 2 operandos para realizar las "comparaciones"
                            // temporalmente ignoraremos las de prioridad 0 pq solo necesitan un elemento para interactuar ++ -- ! ~
                            pushOperatorPerPriority();

                        }
                    }
                    if (operatorStack.isEmpty()){ // Si la pila de operadores está vacia
                        operatorStack.push(operator);
                    }
                    statusState = StatusState.NONE;
                }

            }

        }


    }

    private void pushOperatorPerPriority() {
        Operand op1 = operandsStack.pop();
        Operand op2 = operatorStack.peek().getPriority() == 0 ? new Operand("temp number",-90,0,op1.getLine()) : operandsStack.pop();
        int returnExcel = matrizSemantica[operatorStack.peek().getType()][op1.getType()==6?5:op1.getType()][op2.getType()==6?5:op2.getType()];
        op2 = switch (returnExcel){
            case -90 -> new Operand("temp number",-90,0,op1.getLine());   // NUMBER
            case -71 -> new Operand("temp real",-71,1,op1.getLine());   // REAL
            case -72 -> new Operand("temp boolean",-72,2,op1.getLine());   // boolean
            case -91 -> new Operand("temp string",-91,3,op1.getLine());   // string
            case -61 -> new Operand("temp null",-61,4,op1.getLine());   // null
            default -> new Operand("temp var",-200,6,op1.getLine());   // var
        };
//        if(sematicaState == SematicaState.ASIG)
        sqlQuerys.addTemporal(op2);
        operandsStack.push(op2);
        operatorStack.pop();
    }

    private void declaration(){
        switch(generalStateStack.peek()){
            case DEC_VAR -> DEC_VAR();
            case CLASS_TYPE -> CLASS_TYPE();
            case DEC_MET -> DEC_MET_FUN("metodo");
            case DEC_FUN -> DEC_MET_FUN("fuction");
            case DEC_SET -> DEC_MET_FUN("set");
            case DEC_GET -> DEC_MET_FUN("get");
            case INTERFACE -> INTERFACE_CLASS(true);
            case CLASS -> INTERFACE_CLASS(false);
            case ANON_FUN -> DEC_MET_FUN("@fuction");
            case ARROW_FUN -> DEC_MET_FUN("arrow fuction");
            case ARRAY -> ARRAY();
            case LET_VAR -> LET_VAR();
            case LET_ID -> LET_ID();
            case CLASS_ANON -> CLASS_ANON();
            case SAVEID -> {
                if(tokenList.getFirst().getToken()==-1)
                    letID = tokenList.getFirst().getLexema();
                setOldState();
            }

        }
    }
    private boolean findMember(Stack<Ambito> stack, String id){
        Stack<Ambito> copyStack = (Stack<Ambito>)stack.clone();
        while (!copyStack.isEmpty()){
            if (sqlQuerys.isDeclarated(copyStack.pop().getNumber(),id)){
                return true;
            }
        }
        return false;
    }
    private void codeState(int topStack){
        switch (topStack) {
            case 1000:
                if(!errorAmbito){
                    gestionAmbito(true); // Creación de ámbito
                }
                break;
            case 1001:
                if(!errorAmbito){
                    gestionAmbito(false); // Eliminación de ámbito
                }
                break;
            case 1002:
                if(!errorAmbito){
                    addArea(true); // Abre área de ejecución
                    updateState(State.STATUS);
                    operatorStack.clear();
                    operandsStack.clear();
                    statusState = StatusState.NONE;
                }
                break;
            case 1003:
                if(!errorAmbito){
                    closeArea(true); // Cierra área de ejecución
                    setOldState();
                    operatorStack.clear();
                    operandsStack.clear();
                    statusState = StatusState.NONE;
                    switchTypeStack.clear();
                }
                break;
            case 1004:
                addArea(false); // Abre área de declaración
                break;
            case 1005:
                closeArea(false); // Cierra área de declaración
                break;
            case 1200: // Abre Declaración de variable en DEC_VAR
                if(!errorAmbito){
                    updateState(State.DEC_VAR);
                }
                break;
            case 1201: // Cierra Declaración de variable en DEC_VAR
                if(!errorAmbito){
                    sqlQuerys.addMember(memberDetailsList.getLast());
                }
                if(!(classOVar && errorAmbito)){
                    setOldState();
                    errorAmbito = false;
                }
                break;
            case 1202: // DEC_MET
                if(!errorAmbito){
                    updateState(State.DEC_MET);
                }
                break;
            case 1204: // DEC_FUN
                if(!errorAmbito){
                    updateState(State.DEC_FUN);
                }
                break;
            case 1206: // DEC_SET
                if(!errorAmbito){
                    updateState(State.DEC_SET);
                }
                break;
            case 1208: // DEC_GET
                if(!errorAmbito){
                    updateState(State.DEC_GET);
                }
                break;
            case 1210: // INTERFACE
                if(!errorAmbito){
                    updateState(State.INTERFACE);
                }
                break;
            case 1212: // Funcion anonima
                if(!errorAmbito){
                    updateState(State.ANON_FUN);
                    let = true;
                }
                break;
            case 1214: // Arrow fuction
                if(!errorAmbito){
                    updateState(State.ARROW_FUN);
                    let = true;
                }
                break;
            case 1216: // Class
                if(!errorAmbito){
                    updateState(State.CLASS);
                }
                break;
            case 1203: // DEC_MET
            case 1205: // DEC_FUN
            case 1207: // DEC_SET
            case 1209: // DEC_GET
            case 1213: // Funcion anonima
            case 1215: // Arrow fuction
                if(!errorAmbito){
                    memberDetailsList.get(memberPositionClass).setCantParametro(parametro);
                }
            case 1211: // Interface
                if(!errorAmbito){
                    memberDetailsList.get(memberPositionClass).setTypeParametro(ambitoStack.peek().getNumber()+"");
                    sqlQuerys.addMember(memberDetailsList.get(memberPositionClass));
                }
                parametro = 0;
                classOVar = false;
                contieneParametro = false;
                if(!(generalStateStack.peek() == State.CLASS || generalStateStack.peek() == State.CLASS_ANON ) && !errorAmbito){
                    errorAmbito = false;
                    setOldState();
                }
                else if(generalStateStack.peek() == State.ARROW_FUN || generalStateStack.peek() == State.ANON_FUN){
                    errorAmbito = false;
                    setOldState();
                }
                if(!(generalStateStack.peek() == State.CLASS_ANON)){
                    anon = false;
                }
                let = false;
                break;
            case 1217: // Class
                if(!errorAmbito){
                    memberDetailsList.get(memberPositionClass).setTypeParametro(ambitoStack.peek().getNumber()+"");
                    sqlQuerys.addMember(memberDetailsList.get(memberPositionClass));
                }
                parametro = 0;
                classOVar = false;
                contieneParametro = false;
                break;
            case 1227:
                setOldState();
                errorAmbito = false;
                break;
            case 1218: // ARRAY
                if(!errorAmbito){
                    updateState(State.ARRAY);
                    let = true;
                }
                break;
            case 1219: // CIERRE ARRAY
                setOldState();
                if(!errorAmbito){
                    memberDetailsList.get(memberPositionClass).setArrayDimension(arrayLength.size());
                    memberDetailsList.get(memberPositionClass).setArrayLength(!arrayLength.isEmpty() ? arrayLength.stream().mapToInt(Integer::intValue).toArray():null);
                    sqlQuerys.addMember(memberDetailsList.get(memberPositionClass));
                }
                errorAmbito = false;
                arrayLength.clear();
                break;
            case 1220: // LET VAR (SIN CLASS)
                if(!errorAmbito){
                    updateState(State.LET_VAR);
                    let = true;
                }
                break;
            case 1221: // Cierra LET VAR
                setOldState();
                if(!errorAmbito){
                    sqlQuerys.addMember(memberDetailsList.get(memberPositionClass));
                }
                break;
            case 1222: // LET ID (SIN CLASS)
                if(!errorAmbito){
                    updateState(State.LET_ID);
                    let = true;
                    anon = true;
                }
                break;
            case 1223: // CIERRA LET ID (SIN CLASS)
                if(!errorAmbito){
                    memberDetailsList.addLast(new MemberDetails(memberDetailsList.getLast().getType(),"","@anonima","",ambitoStack.peek().getNumber(),0,0,null));
                    sqlQuerys.addMember(memberDetailsList.getLast());
                }
                setOldState();
                break;
            case 1224: // Abre clase anonima
                setOldState();
                if(!errorAmbito){
                    updateState(State.CLASS_ANON);
                    memberDetailsList.addLast(new MemberDetails(memberDetailsList.getLast().getType(),"","@anonima","",ambitoStack.peek().getNumber(),0,0,null));
                }
                break;
            case 1225: // Cierra clase anonima 1
                if(!errorAmbito){
                    memberDetailsList.getLast().setTypeParametro(ambitoStack.peek().getNumber()+"");
                    sqlQuerys.addMember(memberDetailsList.getLast());
                }
                else{
                    classOVar = true;
                }
                break;
            case 1226:
                errorAmbito = false;
                anon = false;
                setOldState();
                break;
            case 1270: // Save ID para Let
                updateState(State.SAVEID);
                break;

                /**
                 * Semantica
                 * */
            case 1399: // INICIA UN NUEVO CICLO OR
                // Vaciar pila
                if(!errorAmbito){
                    if (sematicaState != SematicaState.NONE){
                        while (!operatorStack.isEmpty()){
                            pushOperatorPerPriority();

                            System.out.println("O P E R A N D S   S T A C K");
                            printOperandStack();
                            System.out.println("O P E R A T O R S   S T A C K");
                            printOperatorStack();
                        }
                        // Siempre queda un operando
                        if(!operandsStack.isEmpty()){
                            switch (sematicaState){
                                case ASIG -> { // Regla 2
                                    sqlQuerys.updateAsignations(operandsStack.peek().getLexema(),operandsStack.peek().getType());

                                    String [] a = sqlQuerys.getTypeDataAsignation(operandsStack.peek().getLine());
                                    boolean state = true;

                                    String symbol = sqlQuerys.getAsignationOperator(operandsStack.peek().getLine());
                                    if(!(symbol.equals("+=")&&a[0].equals("string"))){
                                        if(sqlQuerys.getErrorSemantica(operandsStack.peek().getLine())){ // Ver si son del mismo tipo la asignación y el resultado final
                                            erroresList.add(new Errores(sqlQuerys.getNameIdAsignation(operandsStack.peek().getLine()),-1,operandsStack.peek().getLine(),"Incompatibilidad de tipos","Error semantico"));
                                            state = false;
                                        }
                                    }
                                    semanticaRulesList.add(new Semantica( forSentence ? 1080 :
                                            symbol.equals("=") ? 1020 : symbol.equals("+=") ? 1021 : 1022,operandsStack.peek().getLine(),ambitoStack.peek().getNumber(),symbol.equals("+=") ? "any" : a[0] , a[1],state));

                                }
                                case IF -> {
                                    semanticaRulesList.add(new Semantica(forSentence ? 1081 : 1010,operandsStack.peek().getLine(),ambitoStack.peek().getNumber(), "boolean", operandsStack.peek().getDataType(), operandsStack.peek().getType() == 2 ));
                                }
                                case WHILE -> {
                                    semanticaRulesList.add(new Semantica(1011,operandsStack.peek().getLine(),ambitoStack.peek().getNumber(), "boolean", operandsStack.peek().getDataType(), operandsStack.peek().getType() == 2 ));
                                }
                                case DOWHILE -> {
                                    semanticaRulesList.add(new Semantica(1012,operandsStack.peek().getLine(),ambitoStack.peek().getNumber(), "boolean", operandsStack.peek().getDataType(), operandsStack.peek().getType() == 2));
                                }
                                case SWITCH -> {
                                    semanticaRulesList.add(new Semantica(1031,operandsStack.peek().getLine(),ambitoStack.peek().getNumber(), "string/number", operandsStack.peek().getDataType(), operandsStack.peek().getType() == 0 || operandsStack.peek().getType() == 3));
                                    if(operandsStack.peek().getType() == 0 || operandsStack.peek().getType() == 3){
                                        switchTypeStack.push(operandsStack.peek().getType() == 0); // true number false string
                                    }
                                    else{
                                        switchError = true;
                                    }
                                }
                                case CASE -> {
                                    if (switchError){
                                        if(operandsStack.peek().getType() == 0 || operandsStack.peek().getType() == 3){
                                            switchTypeStack.push(operandsStack.peek().getType() == 0); // true number false string
                                            switchError = false;
                                        }
                                        else {
                                            semanticaRulesList.add(new Semantica(1030,operandsStack.peek().getLine(),ambitoStack.peek().getNumber(), "error switch",operandsStack.peek().getDataType(),false));
                                            break;
                                        }
                                    }
                                    boolean state = operandsStack.peek().getDataType().equals(switchTypeStack.peek() ? "number" : "string");
                                    semanticaRulesList.add(new Semantica(1030,operandsStack.peek().getLine(),ambitoStack.peek().getNumber(), switchTypeStack.peek() ? "number" : "string",operandsStack.peek().getDataType(),state));
                                }

                            }

                        }
                    }
                    operandsStack.clear();
                    statusState = StatusState.NONE;
                    sematicaState = SematicaState.NONE;
                }
                break;

            case 1370: // =
                if(!errorAmbito){
                    sematicaState = SematicaState.ASIG;
                    sqlQuerys.addAsignations(operandsStack.peek().getLexema(),tokenList.getFirst().getLexema(),operandsStack.peek().getType(),tokenList.getFirst().getLinea(),operandsStack.peek().getDataType());
                    operandsStack.pop();
                }
                break;
            case 1371: // ++
                if(!errorAmbito){
                    if (! (sematicaState == SematicaState.ASIG) && operandsStack.empty() && operatorStack.isEmpty() ){
                        plusminus = "++";
                        posfix = true;
                        sematicaStateAux = sematicaState;
                        statusState = StatusState.ASIG;
                        sematicaState = SematicaState.ASIG;
                    }
                }
                break;
            case 1372: // --
                if(!errorAmbito){
                    if (! (sematicaState == SematicaState.ASIG) && operandsStack.empty() && operatorStack.isEmpty() ){
                        plusminus = "--";
                        posfix = true;
                        sematicaStateAux = sematicaState;
                        statusState = StatusState.ASIG;
                        sematicaState = SematicaState.ASIG;
                    }
                }
                break;
            case 1300:  // Abre operando
                if(!errorAmbito){
                    statusState = StatusState.OPERAND;
                }
                break;
            case 1301:  // Cierra operando
                if(!errorAmbito){
                    statusState = StatusState.NONE;
                }
                break;
            case 1302:  // Operadores lógicos && ||
                setExcelSheetOperator(8);
                break;
            case 1303:  // ADD
                setExcelSheetOperator(0);
                break;
            case 1304:  // MINUS
                setExcelSheetOperator(1);
                break;
            case 1305:  // MULTI
                setExcelSheetOperator(2);
                break;
            case 1306:  // DIVISION
                setExcelSheetOperator(3);
                break;
            case 1307:  // NUMBERS
                setExcelSheetOperator(4);
                break;
            case 1308:  // COMPARISON
                setExcelSheetOperator(5);
                break;
            case 1309:  // EQUALS
                setExcelSheetOperator(6);
                break;
            case 1310:  // EQUALTYPE
                setExcelSheetOperator(7);
                break;
            case 1311:  // TURN
                setExcelSheetOperator(9);
                break;

            case 1350: // FACTOR
                operatorPriority = 0;
                break;
            case 1351: // ELEV
                operatorPriority = 1;
                break;
            case 1352: // TP
                operatorPriority = 2;
                break;
            case 1353: // SEP
                operatorPriority = 3;
                break;
            case 1354: // EP
                operatorPriority = 4;
                break;
            case 1355: // AND
                operatorPriority = 5;
                break;
            case 1356: // OR
                operatorPriority = 6;
                break;

                /* Reglas Semantica 2
                * */
            case 1400: // Abre OR en if dentro o fuera de for
                sematicaState = SematicaState.IF;
                break;
            case 1401: // Cierra OR en if dentro o fuera de for
                sematicaState = SematicaState.NONE;
                break;
            case 1402: // Abre OR en while
                sematicaState = SematicaState.WHILE;
                break;
            case 1403: // Cierra OR en while
                sematicaState = SematicaState.NONE;
                break;
            case 1404: // Abre OR en do while
                sematicaState = SematicaState.DOWHILE;
                break;
            case 1405: // Cierra OR en do while
                sematicaState = SematicaState.NONE;
                break;
            case 1406: // Abre Switch
                sematicaState = SematicaState.SWITCH;
                break;
            case 1407: // Cierra Swich
                sematicaState = SematicaState.NONE;
                break;
            case 1408: // Abre case
                sematicaState = SematicaState.CASE;
                break;
            case 1409: // Cierra case
                sematicaState = SematicaState.NONE;
                break;
            case 1410: // Cierra un switch completamente
                switchTypeStack.pop();
                break;
            case 1411:
                forSentence = true;
                break;
            case 1412:
                forSentence = false;
                break;
            case 1413: // Abre for con ambito
                forEach = true;
                System.out.println("For each abre");
                System.out.print("ambitoStack: "+ambitoStack.peek().getNumber()+ " ");
                if(!errorAmbito){
                    gestionAmbito(true); // Creación de ámbito
                    System.out.println(ambitoStack.peek().getNumber());
                }
                // TODO añadir un dec_var
                 if (tokenList.getFirst().getToken() == -1){
                     System.out.println("holoa");
                     if(findMember(ambitoStack,tokenList.getFirst().getLexema())){
                         erroresList.add(new Errores(tokenList.getFirst().getLexema(), tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),"Elemento repetido","Error de ámbito ("+ambitoStack.peek().getNumber()+")",ambitoStack.peek().getNumber()));
                         erroresAmbito ++;
                         errorAmbito = true;
                     }
                     else {
                         memberDetailsList.addLast(new MemberDetails(tokenList.getFirst().getLexema(),"","variable",contieneParametro?memberString:"",ambitoStack.peek().getNumber(),contieneParametro?parametro+1:0,0,null));
                         sqlQuerys.addMember(memberDetailsList.getLast());
                     }
                 }

                break;
            case 1414:
                // TODO este solo define y verifica el tipo de dato del dec_var anterior
                if (tokenList.getFirst().getToken() == -1){
                    System.out.println("type: "+String.valueOf(sqlQuerys.getOneIDType(ambitoStack.peek().getNumber(),tokenList.getFirst().getLexema())));
                    if(String.valueOf(sqlQuerys.getOneIDType(ambitoStack.peek().getNumber(),tokenList.getFirst().getLexema())).equals("string")){ // Si es string
                        memberDetailsList.getLast().setType(String.valueOf(sqlQuerys.getOneIDType(ambitoStack.peek().getNumber(),tokenList.getFirst().getLexema())));
                        sqlQuerys.updateTypeMember(memberDetailsList.getLast().getType());
                        semanticaRulesList.add(new Semantica(1083,operandsStack.peek().getLine(),ambitoStack.peek().getNumber(), String.valueOf(sqlQuerys.getOneIDType(ambitoStack.peek().getNumber(),tokenList.getFirst().getLexema())),memberDetailsList.getLast().getType(),true));
                    }
                    else if(sqlQuerys.isArray(ambitoStack.peek().getNumber(),tokenList.getFirst().getLexema())){ // Si es Array
                        memberDetailsList.getLast().setType(String.valueOf(sqlQuerys.getOneIDType(ambitoStack.peek().getNumber(),tokenList.getFirst().getLexema())));
                        sqlQuerys.updateTypeMember(memberDetailsList.getLast().getType());
                        semanticaRulesList.add(new Semantica(1084,tokenList.getFirst().getLinea(),ambitoStack.peek().getNumber(), String.valueOf(sqlQuerys.getOneIDType(ambitoStack.peek().getNumber(),tokenList.getFirst().getLexema())),memberDetailsList.getLast().getType(),true));
                    }
                    else{
                        semanticaRulesList.add(new Semantica(1085,tokenList.getFirst().getLinea(),ambitoStack.peek().getNumber(),"string/Array",memberDetailsList.getLast().getType(),false));
                    }

                }

                break;
            case 1415: // Cierra for con ambito
                if(forEach){
                    System.out.println("For each cierra");
                    System.out.print("ambitoStack: "+ambitoStack.peek().getNumber()+ " ");
                    if(!errorAmbito){
                        gestionAmbito(false); // Eliminación de ámbito
                        System.out.println(ambitoStack.peek().getNumber());
                    }
                    forEach = false;

                }

            default:
                // Acción por defecto si el valor no coincide con ninguno de los casos anteriores
                break;
        }
    }
    private void setExcelSheetOperator(final int sheet){
        if(!errorAmbito){
            if(sematicaState != SematicaState.NONE){
                statusState = StatusState.OPERATOR;
                excelSheetOperator = sheet;
            }
        }

    }
    private void updateState(State newState){
        generalStateStack.push(newState);
        switch (newState){
            case DEC_VAR, NONE, ARRAY -> classOVar = false;
            case CLASS_TYPE -> {
            }
            default -> classOVar = true;
        }
    }
    private void setOldState(){
        generalStateStack.pop();
        if(generalStateStack.isEmpty()){
            generalStateStack.push(State.NONE);
            classOVar = false;
        }
        switch (generalStateStack.peek()){
            case DEC_VAR, NONE, ARRAY, STATUS-> classOVar = false;
            case CLASS_TYPE -> {
            }
            default -> classOVar = true;
        }
    }
    private void CLASS_ANON(){

    }
    private void isDuplicateLet(String classID){
        if(findMember(ambitoStack,letID)){
            erroresList.add(new Errores(letID, tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),"Elemento repetido","Error de ámbito ("+ambitoStack.peek().getNumber()+")",ambitoStack.peek().getNumber()));
            erroresAmbito++;
            errorAmbito = true;
        }
        else{
            memberDetailsList.addLast(new MemberDetails(letID,"",classID,"",ambitoStack.peek().getNumber(),0,0,null));
            memberPositionClass = memberDetailsList.size()-1;
        }
        let = false;
    }
    private void LET_ID(){
        if(let && (generalStateStack.peek() == State.LET_ID)){
            isDuplicateLet("var let");
        }
        switch (tokenList.getFirst().getToken())
        {
            case -1 -> {
                memberDetailsList.get(memberPositionClass).setType("@"+tokenList.getFirst().getLexema()); // id
                sqlQuerys.addMember(memberDetailsList.get(memberPositionClass));
            }
        }
    }
    private void LET_VAR(){
        if(let && (generalStateStack.peek() == State.LET_VAR)){
            isDuplicateLet("var let");
        }
        switch (tokenList.getFirst().getToken())
        {
            case -90,-91,-72,-61,-71 -> {
                memberDetailsList.get(memberPositionClass).setType(tokenList.getFirst().getLexema()); // number // string // boolean // null // real
            }
            case -58 -> { // #
                updateState(State.CLASS_TYPE);
            }
        }
    }
    private void ARRAY(){
        if(let && (generalStateStack.peek() == State.ARRAY)){
            isDuplicateLet("Array");
        }
        switch (tokenList.getFirst().getToken())
        {
            case -1 -> memberDetailsList.get(memberPositionClass).setType("#"+tokenList.getFirst().getLexema()); // id
            case -90,-91,-72,-61,-71 -> {
                memberDetailsList.get(memberPositionClass).setType(tokenList.getFirst().getLexema()); // number // string // boolean // null // real
            }
            case -58 -> { // #
                updateState(State.CLASS_TYPE);
            }
            case -55 -> { // constante number
                arrayLength.add(Integer.valueOf(tokenList.getFirst().getLexema()));
            }
        }

    }
    private void INTERFACE_CLASS(boolean m){
        classOVar = true;
        if(tokenList.getFirst().getToken()==-1)
        {
            if(findMember(ambitoStack,tokenList.getFirst().getLexema())){
                erroresList.add(new Errores(tokenList.getFirst().getLexema(), tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),"Elemento repetido","Error de ámbito ("+ambitoStack.peek().getNumber()+")",ambitoStack.peek().getNumber()));
                erroresAmbito++;
                errorAmbito = true;
            }
            else{
                memberDetailsList.addLast(new MemberDetails(tokenList.getFirst().getLexema(),"",m?"interface":"class","",ambitoStack.peek().getNumber(),0,0,null));
                memberPositionClass = memberDetailsList.size()-1;
            }

        }
    }
    private void CLASS_TYPE(){
        if(tokenList.getFirst().getToken()==-1){
            if(classOVar){
                String aux=anon?"@ #":"#";
                memberDetailsList.get(memberPositionClass).setType(aux+tokenList.getFirst().getLexema());

            }
            else{
                memberDetailsList.get(memberPositionVar).setType("#"+tokenList.getFirst().getLexema());
            }

        }
        setOldState();
    }
    private void DEC_VAR(){
        switch (tokenList.getFirst().getToken())
        {
            case -1 -> { // id
                if(findMember(ambitoStack,tokenList.getFirst().getLexema())){
                    erroresList.add(new Errores(tokenList.getFirst().getLexema(), tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),"Elemento repetido","Error de ámbito ("+ambitoStack.peek().getNumber()+")",ambitoStack.peek().getNumber()));
                    erroresAmbito++;
                    errorAmbito = true;
                }
                else {
                    memberDetailsList.addLast(new MemberDetails(tokenList.getFirst().getLexema(),"","variable",contieneParametro?memberString:"",ambitoStack.peek().getNumber(),contieneParametro?parametro+1:0,0,null));
                    if(contieneParametro)
                        parametro++;
                }
            }
            case -90,-91,-72,-61,-71 -> {
                memberDetailsList.getLast().setType(tokenList.getFirst().getLexema()); // number // string // boolean // null // real
            }
            case -58 -> { // #
                memberPositionVar = memberDetailsList.size()-1;
                updateState(State.CLASS_TYPE);
            }

        }
    }
    private void DEC_MET_FUN(String classFun){
        contieneParametro = true;
        if(let && (generalStateStack.peek() == State.ANON_FUN|| generalStateStack.peek() == State.ARROW_FUN)){
            if(findMember(ambitoStack,letID)){
                erroresList.add(new Errores(letID, tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),"Elemento repetido","Error de ámbito ("+ambitoStack.peek().getNumber()+")",ambitoStack.peek().getNumber()));
                erroresAmbito++;
                errorAmbito = true;
            }
            else{
                memberDetailsList.addLast(new MemberDetails(letID,"void",classFun,"",ambitoStack.peek().getNumber(),0,0,null));
                memberPositionClass = memberDetailsList.size()-1;
                memberString = letID;
            }
            let = false;
            return;
        }
        switch (tokenList.getFirst().getToken())
        {
            case -1 -> { // id
                if(findMember(ambitoStack,tokenList.getFirst().getLexema())){

                    if(generalStateStack.peek()==State.DEC_SET|| generalStateStack.peek() == State.DEC_GET){ // Es un set
                        if(memberGetSet(ambitoStack.peek().getNumber(),tokenList.getFirst().getLexema(), generalStateStack.peek() == State.DEC_GET?"get":"set")){
                            erroresList.add(new Errores(tokenList.getFirst().getLexema(), tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),"Elemento repetido","Error de ámbito ("+ambitoStack.peek().getNumber()+")",ambitoStack.peek().getNumber()));
                            erroresAmbito++;
                            errorAmbito = true;
                        }
                        else {
                            memberDetailsList.addLast(new MemberDetails(tokenList.getFirst().getLexema(),"void",classFun,"",ambitoStack.peek().getNumber(),0,0,null));
                            memberPositionClass = memberDetailsList.size()-1;
                            memberString = tokenList.getFirst().getLexema();
                        }
                    }
                    else{
                        erroresList.add(new Errores(tokenList.getFirst().getLexema(), tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),"Elemento repetido","Error de ámbito ("+ambitoStack.peek().getNumber()+")",ambitoStack.peek().getNumber()));
                        erroresAmbito++;
                        errorAmbito = true;
                    }

                }
                else{
                    memberDetailsList.addLast(new MemberDetails(tokenList.getFirst().getLexema(),"void",classFun,"",ambitoStack.peek().getNumber(),0,0,null));
                    memberPositionClass = memberDetailsList.size()-1;
                    memberString = tokenList.getFirst().getLexema();
                }
            }
            case -90,-91,-72,-61,-71 -> {
                memberDetailsList.get(memberPositionClass).setType(tokenList.getFirst().getLexema()); // number // string // boolean // null // real
            }
            case -58 -> { // #
                updateState(State.CLASS_TYPE);
            }

        }
    }
    private boolean memberGetSet(int ambito,String id,String classId){
        return sqlQuerys.isDeclaratedGetSet(ambito,id,classId);
    }
    private void printDetailMember() {
        System.out.printf("%10s%10s%15s%10s%15s%15s%15s%15s\n","id", "type", "classId","ambito", "cantParametro", "typeParametro","arrayDimension","arrayLength");
        Iterator<MemberDetails> iterator = memberDetailsList.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
    private void addArea(boolean m){ //true:ejecucion false:declaracion
        areasList.add(new Area(tokenList.getFirst().getLinea(),ambitoStack.peek().getNumber(),0,m));
        String a=m?"ejecucion":"declaracion";
        stringTxt += areasList.getLast().getLineStart()+", "+a+", apertura\n";

    }
    private void closeArea(boolean m){
        areasList.getLast().setLineFinish(tokenList.getFirst().getLinea());
        String a=m?"ejecucion":"declaracion";
        stringTxt += areasList.getLast().getLineFinish()+", "+a+", cerradura\n";

    }
    private int mapearToken(){
        int token=tokenList.getFirst().getToken();
        token*=-1;
        token--;
        return matrizSintactica[syntacticStack.peek()-200][token];
    }
    private void addProduction(int produccion){
        for(int k=producciones[produccion].length-1;k>=0;k--){
            syntacticStack.push(producciones[produccion][k]);
        }
    }
    private void gestionAmbito(boolean m){//true crear false eliminar
        if (m){
            ambitoStack.push(new Ambito(ambito,tokenList.getFirst().getLinea(),0,"placeholder"));
//            stringTxt += "Creacion ambito: [#"+ambitoStack.peek().getNumber()+", #"+ ambitoStack.peek().getLineStart()+"]\n";
//            System.out.println("abre "+ambitoStack.peek().getNumber());
            ambito++;
        }
        else{
            ambitoStack.peek().setLineFinish(tokenList.getFirst().getLinea());
//            stringTxt += "Eliminacion ambito: [#"+ambitoStack.peek().getNumber()+", #"+ ambitoStack.peek().getLineFinish()+"]\n";
//            System.out.println("cierra "+ambitoStack.peek().getNumber());
            ambitoStack.pop();
        }
        printStackAmbito(ambitoStack);
    }
    private void delete(){
        syntacticStack.pop();
        tokenList.removeFirst();
    }
    private void printStackAmbito(Stack<Ambito> stack){
        if (stack.isEmpty()){
            stringTxt +="\tLa pila esta vacia\n";
            return;
        }
        Iterator<Ambito> iterator = stack.iterator();
        stringTxt +="Pila -> [";
        while (iterator.hasNext()) {
            String a= String.valueOf(iterator.next().getNumber());
            stringTxt +=a;
            if(iterator.hasNext())
                stringTxt +=", ";
        }
        stringTxt +="]\n";
    }

    public void clean(){
        syntacticStack.clear();
        syntacticStack.push(200);
        ambitoStack.clear();
        ambito = 0;
        parametro = 0;
        memberDetailsList.clear();
        erroresAmbito = 0;
        errorAmbito = false;
        sqlQuerys.truncateTable();
        operandsStack.clear();
        operatorStack.clear();
        semanticaRulesList.clear();
    }
    private void printOperandStack() {
        System.out.printf("%15s%15s%15s%15s%15s\n","lexema","token","excel column","tipo string","line");
        Iterator<Operand> iterator = operandsStack.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
    private void printOperatorStack() {
        System.out.printf("%15s%15s%15s%15s%15s\n","lexema","token","excel sheet","priority","line");
        Iterator<Operator> iterator = operatorStack.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
    private void printSemanticaRules() {
        System.out.printf("%15s%15s%15s%15s%15s%15s\n","rule","topStack","real value","state","line","ambito");
        Iterator<Semantica> iterator = semanticaRulesList.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
    private int findMemberType(Stack<Ambito> stack, String id){
        Stack<Ambito> copyStack = (Stack<Ambito>) stack.clone();
        while (!copyStack.isEmpty()){
            String type = String.valueOf(sqlQuerys.getOneIDType(copyStack.pop().getNumber(),id));
            switch (type){
                case "number" -> {
                    return 0;
                }
                case "real" -> {
                    return 1;
                }
                case "boolean" -> { // por ahora se usara el valor true pero
                    return 2;
                }
                case "string" -> {
                    return 3;
                }
                case "null" -> {
                    return 4;
                }

            }

        }
        return 5;
    }
    private int idTypeToken(int token){
        switch(token) {
            case -55 -> { // number
                return 0;
            }
            case -56,-57 -> { // real
                return 1;
            }
            case -59,-60 -> { // boolean
                return 2;
            }
            case -46,47 -> { // string
                return 3;
            }
            case -61 -> { // null
                return 4;
            }
            default -> {
                return 5;
            }
        }
    }

    // Contenidos: del 200 a 292 son NO terminales (ver en matriz)
    //             del -1 al -124 son tokens, ver en pila
    // Longitud del arreglo: 0 al 182
    private final int[][] producciones = { // Siempre insertar al reves
            {1000,1004,201,1005,-19,1002,254,206,1003,1001,-20}, 	                                                    // 0 <----- Ambito ; Ejecución ; Declaración
            {247,201}, 	                                                                                                // 1
            {207,201}, 	                                                                                                // 2
            {220,202,203}, 	                                                                                            // 3
            {210,204}, 	                                                                                                // 4
            {-14,220,202}, 	                                                                                            // 5
            {210,205}, 	                                                                                                // 6
            {-14,210,204}, 	                                                                                            // 7
            {-14,210,205}, 	                                                                                            // 8
            {-14,254,206}, 	                                                                                            // 9
            {-14,254,206}, 	                                                                                            // 10
            {-94,1216,-1,1000,1004,1217,-19,246,208,249,209,1227,1001,1005,-20}, 	                                    // 11 <----- Ambito ; Declaración
            {-14,246,208}, 	                                                                                            // 12
            {249,209}, 	                                                                                                // 13
            {-70,1204,-1,1000,1004,-10,246,211,1005,-11,212,-19,1002,254,213,1003,1001,1205,-20}, 	                    // 14 <----- Ambito ; Ejecución ; Declaración
            {-16,246,211}, 	                                                                                            // 15
            {-13,218}, 	                                                                                                // 16
            {-14,254,213}, 	                                                                                            // 17
            {-92,1206,-1,1000,-10,1004,246,215,1005,-11,-19,1002,254,216,1003,1001,1207,-20}, 	                        // 18 <----- Ambito ; Ejecución ; Declaración
            {-16,246,215}, 	                                                                                            // 19
            {-14,254,216}, 	                                                                                            // 20
            {-93,1208,-1,1000,-10,1004,1005,-11,-13,218,1209,-19,1002,254,217,1003,1001,-20}, 	                        // 21 <----- Ambito ; Ejecución ; Declaración
            {-14,254,217}, 	                                                                                            // 22
            {-91}, 	                                                                                                 	// 23 // string
            {-90},  	                                                                                            	// 24 // number
            {-72}, 	 	                                                                                                // 25 // boolean
            {-61}, 	 	                                                                                                // 26 // null
            {-71}, 	 	                                                                                                // 27 // real
            {-46}, 	 	                                                                                                // 28 CADENAS Eliminé el token -47 " " ' '
            {-55}, 	 	                                                                                                // 29
            {-59}, 	 	                                                                                                // 30
            {-60}, 	 	                                                                                                // 31
            {-56}, 	 	                                                                                                // 32 REALES Eliminé el token -57 882.31 9288.2^+56
            {-61}, 	 	                                                                                                // 33
            {-88,1270,-1,221}, 	 	                                                                                    // 34
            {-30,222}, 	                                                                                                // 35
            {1212,-70,1000,-10,1004,246,223,1005,-11,224,-19,1002,254,225,1003,1001,1213,-20}, 	                        // 36 <----- Ambito ; Ejecución ; Declaración
            {-16,246,223}, 	                                                                                            // 37
            {-13,218}, 	                                                                                                // 38
            {-14,254,226}, 	                                                                                            // 39
            {1214,-10,1000,1004,246,226,1005,-11,-33,1002,254,1003,1001,1215}, 	                                        // 40 <----- Ambito ; Ejecución ; Declaración // Arrow fuction
            {-16,246,226}, 	                                                                                            // 41
            {-13,227}, 	                                                                                                // 42
            {-73,-26,1218,228,-40,-30,229}, 	                                                                        // 43
            {218}, 	                                                                                                    // 44
            {-1}, 	                                                                                                    // 45
            {-17,230,1219,-18},                                                                                         // 46
            {273,231}, 	                                                                                                // 47
            {-16,273,231}, 	                                                                                            // 48
            {-74,-73,1219,-10,-11}, 	                                                                                // 49
            {1220,218,1221,232}, 	                                                                                    // 50
            {-30,233}, 	                                                                                                // 51
            {219}, 	                                                                                                    // 52
            {-19,1224,1000,1004,1225,246,-16,234,214,235,249,236,1001,1005,1226,-20}, 	                                // 53 <----- Ambito ; Declaración
            {246,-16}, 	                                                                                                // 54
            {-16,214}, 	                                                                                                // 55
            {-16,249}, 	                                                                                                // 56
            {1222,-1,237}, 	                                                                                            // 57
            {-30,238}, 	                                                                                                // 58
            {1223,219}, 	                                                                                            // 59
            {-19,1224,1000,1004,1225,246,-16,239,214,240,249,241,1001,1005,1226,-20}, 	                                // 60 <----- Ambito ; Declaración
            {246,-16}, 	                                                                                                // 61
            {-16,214}, 	                                                                                                // 62
            {-16,249}, 	                                                                                                // 63
            {242,-26,218,-16,218,-40,-30,243}, 	                                                                        // 64
            {-109}, 	                                                                                                // 65
            {-74,-109,-10,-11}, 	                                                                                    // 66
            {-17,244,-18},                                                                                              // 67
            {273,245}, 	                                                                                                // 68
            {-16,273,245}, 	                                                                                            // 69
            {1200,-1,-13,218,1201}, 	                                                                                // 70 // DEC_VAR
            {-89,1210,-1,1000,1004,-19,246,248,1005,1001,1211,-20}, 	                                                // 71 <----- Ambito ; Declaración
            {-14,246,248}, 	                                                                                            // 72
            {1202,-1,1000,-10,1004,246,250,1005,-11,251,1203,-19,1002,254,252,1003,1001,-20}, 	                        // 73 <----- Ambito ; Ejecución ; Declaración ; DEC_MET
            {-16,246,250}, 	                                                                                            // 74
            {-13,218}, 	                                                                                                // 75
            {-14,254,252}, 	                                                                                            // 76
            // ASIG
            {1370,-30,1301}, 	                                                                                        // 77
            {1370,-35,1301}, 	                                                                                        // 78
            {1370,-52,1301}, 	                                                                                        // 79
            {1370,-50,1301}, 	                                                                                        // 80
            {1370,-38,1301}, 	                                                                                        // 81
            {1370,-9,1301}, 	                                                                                        // 82
            {1370,-7,1301}, 	                                                                                        // 83
            {1370,-23,1301}, 	                                                                                        // 84
            {1370,-28,1301}, 	                                                                                        // 85
            {1370,-45,1301}, 	                                                                                        // 86
            {1370,-44,1301}, 	                                                                                        // 87
            // STATUS
            {-68,-12,255}, 	                                                                                            // 88
            // IF
            {-62,-10,1400,273,1401,-11,254,257}, 	                                                                    // 89
            {-64,-10,1406,273,1407,-11,-19,-76,1408,273,1409,-13,258,254,259,-87,260,-20}, 	                            // 90
            {-19,254,263,-20}, 	                                                                                        // 91
            // WHILE
            {-67,-10,1402,273,1403,-11,254}, 	                                                                        // 92
            {273,-14}, 	                                                                                                // 93
            {-78,273,-14}, 	                                                                                            // 94
            // DO WHILE
            {-66,254,-67,-10,1404,273,1405,-11,-14}, 	                                                                // 95
            // FOR
            {-65,-10,1411,264,1412,-11,254,1415}, 	                                                                        // 96
            {-75,-10,273,256,-11}, 	                                                                                    // 97
            {-69,-10,273,-11}, 	                                                                                        // 98
            {-16,273,256}, 	                                                                                            // 99
            {-63,254}, 	                                                                                                // 100
            {-76,1408,273,1409,-13,258}, 	                                                                            // 101
            {-14,254,259}, 	                                                                                            // 102
            {-76,1408,273,1409,-13,268,-87,260}, 	                                                                    // 103
            {-77,-13,254,262}, 	                                                                                        // 104
            {-14,254,261},                                                                                              // 105
            {-14,254,262},                                                                                              // 106
            {-14,254,263},                                                                                              // 107
            {273,265,-14,1400,273,1401,-14,273,266},                                                                    // 108
            {-87,1413,-1,267,1414,-1},                                                                                            // 109
            {-16,273,265},                                                                                              // 110
            {-16,273,266},                                                                                              // 111
            {-107},                                                                                                     // 112
            {-108},                                                                                                     // 113
            {254,261},                                                                                                  // 114
            {-95,-10,-11},                                                                                              // 115
            {-96,-10,-11},                                                                                              // 116
            {-97},                                                                                                      // 117
            {-98,-10,-11},                                                                                              // 118
            {-99,-10,273,-11},                                                                                          // 119
            {-100,-10,273,-11},                                                                                         // 120
            {-101,-10,273,-11},                                                                                         // 121
            {-102,-10,273,-11},                                                                                         // 122
            {-103,-10,273,-11},                                                                                         // 123
            {-104,-10,273,-16,273,-11},                                                                                 // 124
            {-105,-10,273,-16,273,-11},                                                                                 // 125
            {-106,-10,273,-11},                                                                                         // 126
            {-79,-10,273,-16,273,-11},                                                                                  // 127
            {-80,-10,273,-16,273,-11},                                                                                  // 128
            {-81,-10,273,-16,273,-16,-11},                                                                              // 129
            {-82,-10,273,-11},                                                                                          // 130
            {-83,-10,273,-11},                                                                                          // 131
            {-84,-10,273,-11},                                                                                          // 132
            {-85,-10,273,-11},                                                                                          // 133
            {-86,-10,273,-11},                                                                                          // 134
            {269},                                                                                                      // 135
            {-17,273,272,-18},                                                                                          // 136
            {-16,273,272},                                                                                              // 137
            // OR
            {275,274,1399},                                                                                             // 138
            {1302,1356,-25,1301,275,274},                                                                               // 139
            {1307,1356,-24,275,274},                                                                                    // 140
            // AND
            {277,276},                                                                                                  // 141
            {1302,1355,-6/*,1301*/,277,276},                                                                            // 142
            {1307,1355,-5,277,276},                                                                                     // 143
            {1307,1355,-22,277,276},                                                                                    // 144
            // EXP_PAS EP
            {279,278},                                                                                                  // 145
            {1308,1354,-26/*,1301*/,279,278},                                                                           // 146
            {1308,1354,-29/*,1301*/,279,278},                                                                           // 147
            {1309,1354,-31/*,1301*/,279,278},                                                                           // 148
            {1309,1354,-3/*,1301*/,279,278},                                                                            // 149
            {1308,1354,-42/*,1301*/,279,278},                                                                           // 150
            {1308,1354,-40/*,1301*/,279,278},                                                                           // 151
            {1310,1354,-32/*,1301*/,279,278},                                                                           // 152
            {1310,1354,-4/*,1301*/,279,278},                                                                            // 153
            // SIMPLE EXP_PAS SEP
            {281,280},                                                                                                  // 154
            {1304,1353,-37/*,1301*/,281,280},                                                                           // 155
            {1303,1353,-34/*,1301*/,281,280},                                                                           // 156
            {1311,1353,-27/*,1301*/,281,280},                                                                           // 157
            {1311,1353,-41/*,1301*/,281,280},                                                                           // 158
            {1311,1353,-43/*,1301*/,281,280},                                                                           // 159
            // TERMINO PASCAL TP
            {283,282},                                                                                                  // 160
            {1305,1352,-48,/*1301,*/283,282},                                                                           // 161
            {1306,1352,-51,/*1301,*/283,282},                                                                           // 162
            {1307,1352,-8,/*1301,*/283,282},                                                                            // 163
            // ELEV
            {285,284},                                                                                                  // 164
            {1307,1351,-49,/*1301,*/285,284},                                                                           // 165
            // FACTOR
            {1300,219,1301},                                                                                            // 166
            {286,1300,-1,1301,287},                                                                                     // 167
            {292,-10,273,-11},                                                                                          // 168
            {270},                                                                                                      // 169
            {1371,1304,1350,-36,1301},                                                                                  // 170
            {1372,1304,1350,-39,1301},                                                                                  // 171
            {271,288},                                                                                                  // 172
            {1350,1371,-10,1301,290,-11},                                                                               // 173
            {253,273,289},                                                                                              // 174
            {-15,273,-13,273},                                                                                          // 175
            {273,291},                                                                                                  // 176
            {-16,273,291},                                                                                              // 177
            {1302,1350,-2,1301},                                                                                        // 178
            {1307,1350,-21,1301},                                                                                       // 179

            {-58,-1},                                                                                                   // 180 TIPO
            {253,273,289},                                                                                              // 181 FA1
            {-76,1408,273,1409,-13,268}                                                                                 // 182 case OR : S13
    };
    private final Map<Integer,String> errores_sintaxis=new HashMap<Integer,String>(){{
        put(504,"Solo puedes comenzar un programa con let class fuction o interface");
        put(505,"Se esperaba let, interface o class");
        put(506,"Se esperaba fuction");
        put(507,"Se esperaba class");
        put(508,"Se esperaba ;");
        put(509,"Se esperaba un identificador");
        put(510,"Se esperaba ,");
        put(511,"Se esperaba :");
        put(512,"Se esperaba set o get");
        put(513,"Se esperaba un tipo de variable o #");
        put(514,"Se esperaba =");
        put(515,"Se esperaba una constante");
        put(516,"Se esperaba let");
        put(517,"Se esperaba interface");
        put(518,"Se esperaba : o =");
        put(519,"Se esperaba ( o fuction");
        put(520,"Se esperaba Array, identificador o tipo de variable");
        put(521,"Se esperaba identificador o tipo de variable");
        put(522,"Se esperaba [ o new");
        put(523,"Se esperaba una función, constante, ++, --, ~, (, ! o identificador");
        put(524,"Se esperaba { o constante");
        put(525,"Se esperaba =, < o Map");
        put(526,"Se esperaba Map");
        put(527,"Se esperaba = += /= *= -= %= &= ^= <<= >>= o >>>=");
        put(528,"Se esperaba Console, if, switch, while, return, do, for, una función, constante, ++, --, ~, {, (, ! o identificador");
        put(529,"Se esperaba read o log");
        put(530,"Se esperaba else");
        put(531,"Se esperaba case");
        put(532,"Se esperaba case o default");
        put(533,"Se esperaba let, una función, constante, ++, --, ~, (, ! o identificador");
        put(534,"Se esperaba of o in");
        put(535,"Se esperaba un método para cadena");
        put(536,"Se esperaba una función o un método para cadena");
        put(537,"Se esperaba un [");
        put(538,"Se esperaba || o |");
        put(539,"Se esperaba && & o ^");
        put(540,"Se esperaba < <= == != > >= === o !==");
        put(541,"Se esperaba - + << >> o >>>");
        put(542,"Se esperaba * / o %");
        put(543,"Se esperaba **");
        put(544,"Se esperaba ");
        put(545,"Se esperaba ++ o --");
        put(546,"Se esperaba [ o (");
        put(547,"Se esperaba ?");
        put(548,"Se esperaba ! o ~");

        put(600,"Incompatibilidad de tipos en suma");
        put(601,"Incompatibilidad de tipos en resta");
        put(602,"Incompatibilidad de tipos en multiplicacion");
        put(603,"Incompatibilidad de tipos en division");
        put(604,"Incompatibilidad de tipos en operadores numericos");
        put(605,"Incompatibilidad de tipos en comparacion numerica");
        put(606,"Incompatibilidad de tipos en comparacion igualitaria");
        put(607,"Incompatibilidad de tipos en comparacion de tipos");
        put(608,"Incompatibilidad logica");
        put(609,"Incompatibilidad de tipos en comparacion numerica");
    }};
}

/* *
 * 1200 : declaracion de variable
 * 1201 : cierra declaración de variable
 * 1202 : abre declaracion de metodo o funcion
 * 1203 : cierra declaracion de metodo o funcion
 * 1204 : abre declaracion de metodo o funcion
 * 1205 : cierra declaracion de metodo o funcion
 */
