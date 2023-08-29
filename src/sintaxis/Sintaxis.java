package sintaxis;

import ambito.Ambito;
import ambito.Area;
import lexico.Errores;
import lexico.Token;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Sintaxis {
    private final int[][] matrizSintactica;
    private final LinkedList<Token> tokenList;
    private final LinkedList<Errores> erroresList;
    private final LinkedList<Area> areasList;
    private final Stack<Integer> syntacticStack;
    private final Stack<Ambito> ambitoStack;
    private int ambito=0;
    private String stringTxt ="";
    private final String txtPath ="src/resources/20130044_resultado.txt";
    public Sintaxis(final int [][]matriz,LinkedList<Errores> listErrores, LinkedList<Token>sintaxis){
        this.matrizSintactica = matriz;
        this.tokenList = sintaxis;
        this.erroresList = listErrores;
        this.syntacticStack = new Stack<>();
        this.ambitoStack = new Stack<>();
        this.areasList = new LinkedList<>();
        syntacticStack.push(200);
    }
    public void analize() throws IOException {
        int matrizData;
        while(!tokenList.isEmpty()&&!syntacticStack.isEmpty()){
            if(syntacticStack.peek()>=200&&syntacticStack.peek()<=292){ //Esto quiere decir que es un NO terminal

                matrizData=mapearToken();

                if(matrizData>499){//Caso Error
                    erroresList.add(new Errores(tokenList.getFirst().getLexema(), tokenList.getFirst().getToken(), tokenList.getFirst().getLinea(),errores_sintaxis.get(matrizData)));
                    tokenList.removeFirst();
                }
                else if(matrizData==183){//Caso epsilon
                    syntacticStack.pop();
                }
                else{//Caso produccion
                    syntacticStack.pop();
                    addProduction(matrizData);
                }

            }
            else if(syntacticStack.peek()==1000){ //Creación de ambito
                gestionAmbito(true);
            }
            else if(syntacticStack.peek()==1001){ //Eliminación de ambito
                gestionAmbito(false);
            }
            else if(syntacticStack.peek()==1002){ //Abre area de ejecución
                addArea(true);
            }
            else if(syntacticStack.peek()==1003){ //Cierra area de ejecución
                closeArea(true);
            }
            else if(syntacticStack.peek()==1004){ //Abre area de declaracion
                addArea(false);
            }
            else if(syntacticStack.peek()==1005){ //Cierra area de declaracion
                closeArea(false);
            }
            else if(syntacticStack.peek()<0){ //Esto quiere decir que es un token
                if(tokenList.getFirst().getToken()==syntacticStack.peek()){//Si el token de la lista y pila son iguales
                    delete();
                }
                else if((tokenList.getFirst().getToken()==(-47)&&syntacticStack.peek()==(-46))||(tokenList.getFirst().getToken()==(-57)&&syntacticStack.peek()==(-56))){//Caso especifico de cadenas -47 y reales -57
                    delete();
                }
                else{
                    System.out.println("Error de fuerza bruta, linea: "+ tokenList.getFirst().getLinea()+" lexema: "+ tokenList.getFirst().getLexema());
                    delete();
                }
            }

        }
        if(!syntacticStack.isEmpty()){
            System.out.println("Parece que no terminaste tu codigo");

        }
        Files.write(Paths.get(txtPath), stringTxt.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    private void addArea(boolean m){ //true:ejecucion false:declaracion
        areasList.add(new Area(tokenList.getFirst().getLinea(),ambitoStack.peek().getNumber(),0,m));
        syntacticStack.pop();
        String a=m?"ejecucion":"declaracion";
        stringTxt +=areasList.getLast().getLineStart()+", "+a+", apertura\n";
    }
    private void closeArea(boolean m){
        areasList.getLast().setLineFinish(tokenList.getFirst().getLinea());
        syntacticStack.pop();
        String a=m?"ejecucion":"declaracion";
        stringTxt +=areasList.getLast().getLineFinish()+", "+a+", cerradura\n";
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
        syntacticStack.pop();
        if (m){
            ambitoStack.push(new Ambito(ambito,tokenList.getFirst().getLinea(),0,"placeholder"));
            stringTxt += "Creacion ambito: [#"+ambitoStack.peek().getNumber()+", #"+ ambitoStack.peek().getLineStart()+"]\n";
            ambito++;
        }
        else{
            ambitoStack.peek().setLineFinish(tokenList.getFirst().getLinea());
            stringTxt += "Eliminacion ambito: [#"+ambitoStack.peek().getNumber()+", #"+ ambitoStack.peek().getLineFinish()+"]\n";
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
        ambito=0;
    }
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

            put(700,"Error Sintactico temporal");
    }};


    //Contenidos: del 200 a 292 son NO terminales (ver en matriz)
    //            del -1 al -124 son tokens, ver en pila
    //Longitud del arreglo: 0 al 182

    private final int[][] producciones = {//Siempre insertar al reves
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
            {-94,-1,1000,1004,-19,246,208,249,209,1005,1001,-20}, 	                                                    // 11 <----- Ambito ; Declaración
            {-14,246,208}, 	                                                                                            // 12
            {249,209}, 	                                                                                                // 13
            {-70,-1,1000,1004,-10,246,211,1005,-11,212,-19,1002,254,213,1003,1001,-20}, 	                            // 14 <----- Ambito ; Ejecución ; Declaración
            {-16,246,211}, 	                                                                                            // 15
            {-13,218}, 	                                                                                                // 16
            {-14,254,213}, 	                                                                                            // 17
            {-92,-1,1000,-10,1004,246,215,1005,-11,-19,1002,254,216,1003,1001,-20}, 	                                // 18 <----- Ambito ; Ejecución ; Declaración
            {-16,246,215}, 	                                                                                            // 19
            {-14,254,216}, 	                                                                                            // 20
            {-93,-1,1000,-10,1004,1005,-11,-13,218,-19,1002,254,217,1003,1001,-20}, 	                                // 21 <----- Ambito ; Ejecución ; Declaración
            {-14,254,217}, 	                                                                                            // 22
            {-91}, 	                                                                                                 	// 23
            {-90},  	                                                                                            	// 24
            {-72}, 	 	                                                                                                // 25
            {-61}, 	 	                                                                                                // 26
            {-71}, 	 	                                                                                                // 27
            {-46}, 	 	                                                                                                // 28 CADENAS Eliminé el token -47 " " ' '
            {-55}, 	 	                                                                                                // 29
            {-59}, 	 	                                                                                                // 30
            {-60}, 	 	                                                                                                // 31
            {-56}, 	 	                                                                                                // 32 REALES Eliminé el token -57 882.31 9288.2^+56
            {-61}, 	 	                                                                                                // 33
            {-88,-1,221}, 	 	                                                                                        // 34
            {-30,222}, 	                                                                                                // 35
            {-70,1000,-10,1004,246,223,1005,-11,224,-19,1002,254,225,1003,1001,-20}, 	                                // 36 <----- Ambito ; Ejecución ; Declaración
            {-16,246,223}, 	                                                                                            // 37
            {-13,218}, 	                                                                                                // 38
            {-14,254,226}, 	                                                                                            // 39
            {1000,-10,1004,246,226,1005,-11,-33,1002,254,1003,1001}, 	                                                // 40 <----- Ambito ; Ejecución ; Declaración
            {-16,246,226}, 	                                                                                            // 41
            {-13,227}, 	                                                                                                // 42
            {-73,-26,228,-40,-30,229}, 	                                                                                // 43
            {218}, 	                                                                                                    // 44
            {-1}, 	                                                                                                    // 45
            {-17,230,-18},                                                                                              // 46
            {273,231}, 	                                                                                                // 47
            {-16,273,231}, 	                                                                                            // 48
            {-74,-73,-10,-11}, 	                                                                                        // 49
            {218,232}, 	                                                                                                // 50
            {-30,233}, 	                                                                                                // 51
            {219}, 	                                                                                                    // 52
            {-19,1000,1004,246,-16,234,214,235,249,236,1005,1001,-20}, 	                                                // 53 <----- Ambito ; Declaración
            {246,-16}, 	                                                                                                // 54
            {-16,214}, 	                                                                                                // 55
            {-16,249}, 	                                                                                                // 56
            {-1,237}, 	                                                                                                // 57
            {-30,238}, 	                                                                                                // 58
            {219}, 	                                                                                                    // 59
            {-19,1000,1004,246,-16,239,214,240,249,241,1005,1001,-20}, 	                                                // 60 <----- Ambito ; Declaración
            {246,-16}, 	                                                                                                // 61
            {-16,214}, 	                                                                                                // 62
            {-16,249}, 	                                                                                                // 63
            {242,-26,218,-16,218,-40,-30,243}, 	                                                                        // 64
            {-109}, 	                                                                                                // 65
            {-74,-109,-10,-11}, 	                                                                                    // 66
            {-17,244,-18},                                                                                              // 67
            {273,245}, 	                                                                                                // 68
            {-16,273,245}, 	                                                                                            // 69
            {-1,-13,218}, 	                                                                                            // 70
            {-89,-1,1000,1004,-19,246,248,1005,1001,-20}, 	                                                            // 71 <----- Ambito ; Declaración
            {-14,246,248}, 	                                                                                            // 72
            {-1,1000,-10,1004,246,250,1005,-11,251,-19,1002,254,252,1003,1001,-20}, 	                                // 73 <----- Ambito ; Ejecución ; Declaración
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
            {275,274},                                                                                                  // 138
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
            {219},                                                                                                      // 166
            {286,-1,287},                                                                                               // 167
            {292,-10,273,-11},                                                                                          // 168
            {270},                                                                                                      // 169
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

}
