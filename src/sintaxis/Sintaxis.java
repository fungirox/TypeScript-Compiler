package sintaxis;

import ambito.Ambito;
import ambito.Area;
import ambito.State;
import ambito.MemberDetails;
import lexico.Errores;
import lexico.Token;
import resources.CargarRecursos;
import resources.SqlQuerys;
import semantica.Operand;
import semantica.StatusState;

import java.io.IOException;
import java.util.*;

public class Sintaxis {
    private final SqlQuerys sqlQuerys;
    private int erroresAmbito;
    private final int[][] matrizSintactica;
    private final int [][][] matrizSemantica;
    private final LinkedList <Token> tokenList;
    private final LinkedList <Errores> erroresList;
    private final LinkedList <Area> areasList;
    private final Stack <Integer> syntacticStack;
    private final Stack <Ambito> ambitoStack;
    private int ambito;
    private String stringTxt ="";
    private final String txtPath ="src/resources/20130044_resultado.txt";
    private final LinkedList <MemberDetails> memberDetailsList;
    private final Stack <State> stateStack;
    private StatusState statusState;
    private int parametro = 0;
    private boolean contieneParametro = false;
    private int memberPositionVar;
    private int memberPositionClass;
    private String memberString;
    private String letID;
    private boolean let = false;
    private boolean classOVar = false;
    private final ArrayList <Integer> arrayLength;
    private boolean anon = false;
    private boolean error = false;
    private String lexemaOR;
    private final Stack<Operand> operandsStack;
    public Sintaxis(final int [][]matriz,final LinkedList<Errores> listErrores,final LinkedList<Token>sintaxis,final int [][][]matrizSemantica){
        this.matrizSintactica = matriz;
        this.tokenList = sintaxis;
        this.erroresList = listErrores;
        this.syntacticStack = new Stack<>();
        this.stateStack = new Stack<>();
        this.ambitoStack = new Stack<>();
        this.memberDetailsList = new LinkedList<>();
        this.areasList = new LinkedList<>();
        this.arrayLength = new ArrayList<>();
        this.matrizSemantica=matrizSemantica;
        this.sqlQuerys = CargarRecursos.connectionSQL;
        this.statusState = StatusState.NONE;
        this.operandsStack = new Stack<>();
        syntacticStack.push(200);
        stateStack.push(State.NONE);
        ambito = 0;

    }
    public int analize() throws IOException {
        int matrizData;
        while(!tokenList.isEmpty()&&!syntacticStack.isEmpty()){
            System.out.println(tokenList.getFirst().getLexema()+" line: "+tokenList.getFirst().getLinea()+" generalState: "+stateStack.peek()+" statusState "+statusState+" error: "+error+" topStack: "+syntacticStack.peek()+" classOVar: "+classOVar);
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
            else if(syntacticStack.peek()>=1000 && syntacticStack.peek()<1400) { // Declaracion de miembros // Apertura de ambitos, areas de declaracion y ejecución // Estados de OR
                codeState(syntacticStack.peek());
                syntacticStack.pop();

            }
            else if(syntacticStack.peek()<0){ //Esto quiere decir que es un token
                if(tokenList.getFirst().getToken()==syntacticStack.peek()||(tokenList.getFirst().getToken()==(-47)&&syntacticStack.peek()==(-46))||(tokenList.getFirst().getToken()==(-57)&&syntacticStack.peek()==(-56))){//Si el token de la lista y pila son iguales y Caso especifico de cadenas -47 y reales -57
                    if (!error){
                        if(stateStack.peek()==State.STATUS){
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
            System.out.println("------------------------------------------------------------------------------------------------------------------------");
        }

        if(!syntacticStack.isEmpty()) {
            System.out.println("Parece que no terminaste tu codigo");
        }
        System.out.println();
        printOperandStack();
//        printDetailMember();

        return erroresAmbito;
    }
    private void execute(){
        if (tokenList.getFirst().getToken() == -1) { // Si es ID
            if (!findMember(ambitoStack,tokenList.getFirst().getLexema())){
                erroresList.add(new Errores(tokenList.getFirst().getLexema(), tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),"Elemento no declarado","Error de ámbito ("+ambitoStack.peek().getNumber()+")",ambitoStack.peek().getNumber()));
                erroresAmbito++;
                return;
            }

        }
        System.out.println("Semantica: token "+tokenList.getFirst().getToken()+" lexema "+tokenList.getFirst().getLexema()+" line "+tokenList.getFirst().getLinea());
        lexemaOR = tokenList.getFirst().getLexema();
        // Empieza semantica
        switch (statusState){
            case OPERAND -> {
//                System.out.println("jojo "+findMemberType(ambitoStack,tokenList.getFirst().getLexema()));

                int operandType;
                operandType =  tokenList.getFirst().getToken() == -1 ? findMemberType(ambitoStack,tokenList.getFirst().getLexema()) : idTypeToken(tokenList.getFirst().getToken());
                /* *
                 * 0 : number
                 * 1 : real
                 * 2 : boolean
                 * 3 : string
                 * 4 : null
                 * 5 : var
                * */

                operandsStack.push(new Operand(tokenList.getFirst().getLexema(),tokenList.getFirst().getToken(),operandType,tokenList.getFirst().getLinea()));
            }
            case END -> {

            }

        }
    }
    private int idTypeToken(int token){
        switch(token) {
            case -55 -> { // number
                System.out.println("number");
                return 0;
            }
            case -56,-57 -> { // real
                System.out.println("real");
                return 1;
            }
            case -59,-60 -> { // boolean
                System.out.println("boolean");
                return 2;
            }
            case -46,47 -> { // string
                System.out.println("string");
                return 3;
            }
            case -61 -> { // null
                System.out.println("null");
                return 4;
            }
            default -> {
                System.out.println("another type (VAR) ...");
                return 5;
            }
        }
    }
    private void declaration(){
        switch(stateStack.peek()){
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
                if(!error){
                    gestionAmbito(true); // Creación de ámbito
                }
                break;
            case 1001:
                if(!error){
                    gestionAmbito(false); // Eliminación de ámbito
                }
                break;
            case 1002:
                addArea(true); // Abre área de ejecución
                updateState(State.STATUS);
                break;
            case 1003:
                closeArea(true); // Cierra área de ejecución
                setOldState();
                break;
            case 1004:
                addArea(false); // Abre área de declaración
                break;
            case 1005:
                closeArea(false); // Cierra área de declaración
                break;
            case 1200: // Abre Declaración de variable en DEC_VAR
                if(!error){
                    updateState(State.DEC_VAR);
                }
                break;
            case 1201: // Cierra Declaración de variable en DEC_VAR
                if(!error){

//                    System.out.println("Añadirá uno a la base de datos "+stateStack.peek()+" "+memberDetailsList.getLast().getId());
                    sqlQuerys.addMember(memberDetailsList.getLast());
                }
                if(!classOVar){
                    error = false;
                }
                boolean classOvarAnt = classOVar;
                setOldState();
                classOVar = classOvarAnt;
//                if(error&&classOvarAnt) error = true;
                break;
            case 1202: // DEC_MET
                if(!error){
                    updateState(State.DEC_MET);
                }
                break;
            case 1204: // DEC_FUN
                if(!error){
                    updateState(State.DEC_FUN);
                }
                break;
            case 1206: // DEC_SET
                if(!error){
                    updateState(State.DEC_SET);
                }
                break;
            case 1208: // DEC_GET
                if(!error){
                    updateState(State.DEC_GET);
                }
                break;
            case 1210: // INTERFACE
                if(!error){
                    updateState(State.INTERFACE);
                }
                break;
            case 1212: // Funcion anonima
                if(!error){
                    updateState(State.ANON_FUN);
                    let = true;
                }
                break;
            case 1214: // Arrow fuction
                if(!error){
                    updateState(State.ARROW_FUN);
                    let = true;
                }
                break;
            case 1216: // Class
                if(!error){
                    updateState(State.CLASS);
                }
                break;
            case 1203: // DEC_MET
            case 1205: // DEC_FUN
            case 1207: // DEC_SET
            case 1209: // DEC_GET
            case 1213: // Funcion anonima
            case 1215: // Arrow fuction
                if(!error){
                    memberDetailsList.get(memberPositionClass).setCantParametro(parametro);
                }
            case 1211: // Interface
                if(!error){
                    memberDetailsList.get(memberPositionClass).setTypeParametro(ambitoStack.peek().getNumber()+"");
//                    System.out.println("Añadirá uno a la base de datos "+stateStack.peek()+" "+memberDetailsList.get(memberPositionClass).getId());
                    sqlQuerys.addMember(memberDetailsList.get(memberPositionClass));
                }
                parametro = 0;
                classOVar = false;
                contieneParametro = false;
                if(!(stateStack.peek() == State.CLASS || stateStack.peek() == State.CLASS_ANON )){
                    error = false;
                    setOldState();
                }
                if(!(stateStack.peek() == State.CLASS_ANON)){
                    anon = false;
                }
                let = false;
                break;
            case 1217: // Class
                if(!error){
                    memberDetailsList.get(memberPositionClass).setTypeParametro(ambitoStack.peek().getNumber()+"");
//                    System.out.println("Añadirá uno a la base de datos "+stateStack.peek()+" "+memberDetailsList.get(memberPositionClass).getId());
                    sqlQuerys.addMember(memberDetailsList.get(memberPositionClass));
                }
                parametro = 0;
                classOVar = false;
                contieneParametro = false;
                break;
            case 1227:
                setOldState();
//                System.out.println("Añadirá uno a la base de datos "+stateStack.peek()+" "+memberDetailsList.get(memberPositionClass).getId());
//                sqlQuerys.addMember(memberDetailsList.get(memberPositionClass));
                error = false;
                break;
            case 1218: // ARRAY
                if(!error){
                    updateState(State.ARRAY);
                    let = true;
                }
                break;
            case 1219: // CIERRE ARRAY
                setOldState();
                if(!error){
                    memberDetailsList.get(memberPositionClass).setArrayDimension(arrayLength.size());
                    memberDetailsList.get(memberPositionClass).setArrayLength(!arrayLength.isEmpty() ? arrayLength.stream().mapToInt(Integer::intValue).toArray():null);
                    sqlQuerys.addMember(memberDetailsList.get(memberPositionClass));
//                    System.out.println("Añadirá uno a la base de datos "+stateStack.peek()+" "+memberDetailsList.get(memberPositionClass).getId());
                }
                error = false;
                arrayLength.clear();
                break;
            case 1220: // LET VAR (SIN CLASS)
                if(!error){
                    updateState(State.LET_VAR);
                    let = true;
                }
                break;
            case 1221: // Cierra LET VAR
                setOldState();
                if(!error){
                    sqlQuerys.addMember(memberDetailsList.get(memberPositionClass));
//                    System.out.println("Añadirá uno a la base de datos "+stateStack.peek()+" "+memberDetailsList.get(memberPositionClass).getId());
                }
                break;
            case 1222: // LET ID (SIN CLASS)
                if(!error){
                    updateState(State.LET_ID);
                    let = true;
                    anon = true;
                }
                break;
            case 1223: // CIERRA LET ID (SIN CLASS)
                if(!error){
                    memberDetailsList.addLast(new MemberDetails(memberDetailsList.getLast().getType(),"","@anonima","",ambitoStack.peek().getNumber(),0,0,null));
                    sqlQuerys.addMember(memberDetailsList.getLast());
//                    System.out.println("Añadirá uno a la base de datos "+stateStack.peek()+" "+memberDetailsList.getLast().getId());
                }
                setOldState();
                break;
            case 1224: // Abre clase anonima
                setOldState();
                if(!error){
                    updateState(State.CLASS_ANON);
                    memberDetailsList.addLast(new MemberDetails(memberDetailsList.getLast().getType(),"","@anonima","",ambitoStack.peek().getNumber(),0,0,null));
                }
                break;
            case 1225: // Cierra clase anonima 1
                if(!error){
                    memberDetailsList.getLast().setTypeParametro(ambitoStack.peek().getNumber()+"");
                    sqlQuerys.addMember(memberDetailsList.getLast());
//                    System.out.println("Añadirá uno a la base de datos "+stateStack.peek()+" "+memberDetailsList.getLast().getId());
                }
                else{
                    classOVar = true;
                }
                break;
            case 1226:
                error = false;
                anon = false;
//                sqlQuerys.addMember(memberDetailsList.getLast());
//                System.out.println("Añadirá uno a la base de datos "+stateStack.peek()+" "+memberDetailsList.getLast().getId());
                setOldState();
                break;
            case 1270: // Save ID para Let
                updateState(State.SAVEID);
                break;

                /**
                 * Semantica
                 * */

            case 1300: // Abre 'dato'
                statusState = StatusState.OPERAND;
                System.out.println("\tAbre OR "+tokenList.getFirst().getLexema());
                break;
            case 1301: // Cierra 'dato'
                System.out.println("\tCierra OR "+lexemaOR);
                statusState = StatusState.END;
                break;
            default:
                // Acción por defecto si el valor no coincide con ninguno de los casos anteriores
                break;
        }
    }
    private void updateState(State newState){
        stateStack.push(newState);
        switch (newState){
            case DEC_VAR, NONE, ARRAY -> classOVar = false;
            case CLASS_TYPE -> {
            }
            default -> classOVar = true;
        }
    }
    private void setOldState(){
        stateStack.pop();
        if(stateStack.isEmpty()){
            stateStack.push(State.NONE);
            classOVar = false;
        }
        switch (stateStack.peek()){
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
            erroresList.add(new Errores(letID, tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),"El elemento ya está declarado en el ambito "+ambitoStack.peek().getNumber(),"Error de ámbito",ambitoStack.peek().getNumber()));
            erroresAmbito++;
            error = true;
        }
        else{
            memberDetailsList.addLast(new MemberDetails(letID,"",classID,"",ambitoStack.peek().getNumber(),0,0,null));
            memberPositionClass = memberDetailsList.size()-1;
        }
        let = false;
    }
    private void LET_ID(){
        if(let && (stateStack.peek() == State.LET_ID)){
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
        if(let && (stateStack.peek() == State.LET_VAR)){
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
        if(let && (stateStack.peek() == State.ARRAY)){
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
                error = true;
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
                    error = true;
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
        if(let && (stateStack.peek() == State.ANON_FUN||stateStack.peek() == State.ARROW_FUN)){
            if(findMember(ambitoStack,letID)){
                erroresList.add(new Errores(tokenList.getFirst().getLexema(), tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),"Elemento repetido","Error de ámbito ("+ambitoStack.peek().getNumber()+")",ambitoStack.peek().getNumber()));
                erroresAmbito++;
                error = true;
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

                    if(stateStack.peek()==State.DEC_SET||stateStack.peek() == State.DEC_GET){ // Es un set
                        if(memberGetSet(ambitoStack.peek().getNumber(),tokenList.getFirst().getLexema(),stateStack.peek() == State.DEC_GET?"get":"set")){
                            erroresList.add(new Errores(tokenList.getFirst().getLexema(), tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),"Elemento repetido","Error de ámbito ("+ambitoStack.peek().getNumber()+")",ambitoStack.peek().getNumber()));
                            erroresAmbito++;
                            error = true;
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
                        error = true;
                    }

                }
                else{
                    memberDetailsList.addLast(new MemberDetails(tokenList.getFirst().getLexema(),"void",classFun,"",ambitoStack.peek().getNumber(),0,0,null));
                    memberPositionClass = memberDetailsList.size()-1;
                    memberString = tokenList.getFirst().getLexema();
                }
                break;
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
            stringTxt += "Creacion ambito: [#"+ambitoStack.peek().getNumber()+", #"+ ambitoStack.peek().getLineStart()+"]\n";
//            System.out.println("abre "+ambitoStack.peek().getNumber());
            ambito++;
        }
        else{
            ambitoStack.peek().setLineFinish(tokenList.getFirst().getLinea());
            stringTxt += "Eliminacion ambito: [#"+ambitoStack.peek().getNumber()+", #"+ ambitoStack.peek().getLineFinish()+"]\n";
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
        error = false;
        sqlQuerys.truncateTable();

    }
    private void printOperandStack() {
        System.out.printf("%15s%15s%15s%15s%15s\n","lexema","token","num tipo","tipo string","line");
        Iterator<Operand> iterator = operandsStack.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
    private int findMemberType(Stack<Ambito> stack, String id){
        Stack<Ambito> copyStack = (Stack<Ambito>)stack.clone();
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
    //Contenidos: del 200 a 292 son NO terminales (ver en matriz)
    //            del -1 al -124 son tokens, ver en pila
    //Longitud del arreglo: 0 al 182
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
            {-30}, 	                                                                                                    // 77
            {-35}, 	                                                                                                    // 78
            {-52}, 	                                                                                                    // 79
            {-50}, 	                                                                                                    // 80
            {-38}, 	                                                                                                    // 81
            {-9}, 	                                                                                                    // 82
            {-7}, 	                                                                                                    // 83
            {-23}, 	                                                                                                    // 84
            {-28}, 	                                                                                                    // 85
            {-45}, 	                                                                                                    // 86
            {-44}, 	                                                                                                    // 87
            {-68,-12,255}, 	                                                                                            // 88
            {-62,-10,273,-11,254,257}, 	                                                                                // 89
            {-64,-10,273,-11,-19,-76,273,-13,258,254,259,-87,260,-20}, 	                                                // 90
            {-19,254,263,-20}, 	                                                                                        // 91
            {-67,-10,273,-11,254}, 	                                                                                    // 92
            {273,-14}, 	                                                                                                // 93
            {-78,273,-14}, 	                                                                                            // 94
            {-66,254,-67,-10,273,-11,-14}, 	                                                                            // 95
            {-65,-10,264,-11,254}, 	                                                                                    // 96
            {-75,-10,273,256,-11}, 	                                                                                    // 97
            {-69,-10,273,-11}, 	                                                                                        // 98
            {-16,273,256}, 	                                                                                            // 99
            {-63,254}, 	                                                                                                // 100
            {-76,273,-13,258}, 	                                                                                        // 101
            {-14,254,259}, 	                                                                                            // 102
            {-76,273,-13,268,-87,260}, 	                                                                                // 103
            {-77,-13,254,262}, 	                                                                                        // 104
            {-14,254,261},                                                                                              // 105
            {-14,254,262},                                                                                              // 106
            {-14,254,263},                                                                                              // 107
            {273,265,-14,254,-14,273,266},                                                                              // 108
            {-87,-1,267,-1},                                                                                            // 109
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
            {1300,275,274},                                                                                                  // 138
            {-25,275,274},                                                                                              // 139
            {-24,275,274},                                                                                              // 140
            {277,276},                                                                                                  // 141
            {-6,277,276},                                                                                               // 142
            {-5,277,276},                                                                                               // 143
            {-22,277,276},                                                                                              // 144
            {279,278},                                                                                                  // 145
            {-26,279,278},                                                                                              // 146
            {-29,279,278},                                                                                              // 147
            {-31,279,278},                                                                                              // 148
            {-3,279,278},                                                                                               // 149
            {-42,279,278},                                                                                              // 150
            {-40,279,278},                                                                                              // 151
            {-32,279,278},                                                                                              // 152
            {-4,279,278},                                                                                               // 153
            {281,280},                                                                                                  // 154
            {-37,281,280},                                                                                              // 155
            {-34,281,280},                                                                                              // 156
            {-27,281,280},                                                                                              // 157
            {-41,281,280},                                                                                              // 158
            {-43,281,280},                                                                                              // 159
            {283,282},                                                                                                  // 160
            {-48,283,282},                                                                                              // 161
            {-51,283,282},                                                                                              // 162
            {-8,283,282},                                                                                               // 163
            {285,284},                                                                                                  // 164
            {-49,285,284},                                                                                              // 165
            {219,1301},                                                                                                      // 166
            {286,-1,287,1301},                                                                                               // 167
            {292,-10,273,-11,1301},                                                                                          // 168
            {270,1301},                                                                                                      // 169
            {-36},                                                                                                      // 170
            {-39},                                                                                                      // 171
            {271,288},                                                                                                  // 172
            {-10,290,-11},                                                                                              // 173
            {253,273,289},                                                                                              // 174
            {-15,273,-13,273},                                                                                          // 175
            {273,291},                                                                                                  // 176
            {-16,273,291},                                                                                              // 177
            {-2},                                                                                                       // 178
            {-21},                                                                                                      // 179

            {-58,-1},                                                                                                   // 180 TIPO
            {253,273,289},                                                                                              // 181 FA1
            {-76,273,-13,268}                                                                                           // 182 case OR : S13
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
    }};
}

/**
 * 1200 : declaracion de variable
 * 1201 : cierra declaración de variable
 * 1202 : abre declaracion de metodo o funcion
 * 1203 : cierra declaracion de metodo o funcion
 * 1204 : abre declaracion de metodo o funcion
 * 1205 : cierra declaracion de metodo o funcion
 */


