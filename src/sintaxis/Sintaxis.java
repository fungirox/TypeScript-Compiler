package sintaxis;

import lexico.Errores;
import lexico.Token;

import java.util.*;

public class Sintaxis {
    private final int[][] matrizSintactica;
    private LinkedList<Token> tokenListSintaxis;
    private LinkedList<Errores> erroresList;
    private Stack<Integer> syntacticStack;
    public Sintaxis(final int [][]matriz,LinkedList<Errores> listErrores, LinkedList<Token>sintaxis){
        this.matrizSintactica=matriz;
        this.tokenListSintaxis=sintaxis;
        this.erroresList=listErrores;
        this.syntacticStack=new Stack<>();
        syntacticStack.push(200);
    }

    public void analize(){
        int i=0;
        int colToken,rowNT=0,prod;
        int topStack;
        while(!tokenListSintaxis.isEmpty()&&!syntacticStack.isEmpty()){
            System.out.print(syntacticStack.size()+" "+tokenListSintaxis.getFirst().getLexema()+" ");
            topStack=syntacticStack.peek();
            System.out.print(topStack+": ");
            Iterator<Integer> iterator = syntacticStack.iterator();
            while (iterator.hasNext()) {
                System.out.print(iterator.next() + " ");
            }

            if(topStack>=200&&topStack<=292){ //Esto quiere decir que es un NO terminal
                colToken=tokenListSintaxis.getFirst().getToken();
                colToken*=-1;colToken--;

                prod=matrizSintactica[topStack-200][colToken];
                if(prod>500){//Caso Error
                    System.out.println("Error");
                    erroresList.add(new Errores(tokenListSintaxis.getFirst().getLexema(),tokenListSintaxis.getFirst().getToken(),tokenListSintaxis.getFirst().getLinea(),errores_sintaxis.get(prod)));
                    System.out.println(errores_sintaxis.get(prod));
                    tokenListSintaxis.removeFirst();
                }
                else if(prod==180){//Caso epsilon
                    System.out.println("epsilon");
                    syntacticStack.pop();
                }
                else{//Caso produccion
                    syntacticStack.pop();
                    System.out.print("prod "+prod);
                    for(int k=producciones[prod].length-1;k>=0;k--){
                        syntacticStack.push(producciones[prod][k]);
                    }
                }

            }
            else if(topStack<0){ //Esto quiere decir que es un token
                if(tokenListSintaxis.getFirst().getToken()==topStack){//Si el token de la lista y pila son iguales
                    System.out.print(tokenListSintaxis.getFirst().getLexema()+" ");
                    syntacticStack.pop();
                    tokenListSintaxis.removeFirst();
                }
                else if(tokenListSintaxis.getFirst().getToken()==(-47)&&topStack==(-46)){//Caso especifico de cadenas -47
                    System.out.print(tokenListSintaxis.getFirst().getLexema()+" ");
                    syntacticStack.pop();
                    tokenListSintaxis.removeFirst();
                }
                else if(tokenListSintaxis.getFirst().getToken()==(-57)&&topStack==(-56)){//Caso especifico de reales -57
                    System.out.print(tokenListSintaxis.getFirst().getLexema()+" ");
                    syntacticStack.pop();
                    tokenListSintaxis.removeFirst();
                }
                else{
                    System.out.println("Error de fuerza bruta");
                    syntacticStack.pop();
                    tokenListSintaxis.removeFirst();
                }
            }
            System.out.println(" lp "+syntacticStack.size());

        }
        if(!syntacticStack.isEmpty()){
            System.out.println("Parece que no terminaste tu codigo");
        }
    }
    public void clean(){
        syntacticStack.clear();
        syntacticStack.push(200);
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
            put(513,"Se esperaba un tipo de variable");
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
    //Longitud del arreglo: 0 al 179

    private final int[][] producciones = {//Siempre insertar al reves
            {201,-19,254,206,-20}, 	// 0
            {247,201}, 	// 1
            {207,201}, 	// 2
            {220,202,203}, 	// 3
            {210,204}, 	// 4
            {-14,220,202}, 	// 5
            {210,205}, 	// 6
            {-14,210,204}, 	// 7
            {-14,210,205}, 	// 8
            {-14,254,206}, 	// 9
            {-14,254,206}, 	// 10

            {-93,-1,-19,246,208,249,209,-20}, 	// 11
            {-14,246,208}, 	// 12
            {249,209}, 	// 13
            {-69,-1,-10,246,211,-11,212,-19,254,213,-20}, 	// 14
            {-16,246,211}, 	// 15
            {-13,218}, 	// 16
            {-14,254,213}, 	// 17
            {-91,-1,-10,246,215,-11,-19,254,216,-20}, 	// 18
            {-16,246,215}, 	// 19
            {-14,254,216}, 	// 20
            {-92,-1,-10,-11,-13,218,-19,254,217,-20}, 	// 21
            {-14,254,217}, 	// 22
            {-90}, 	// 23
            {-89}, 	// 24
            {-71}, 	// 25
            {-60}, 	// 26
            {-70}, 	// 27
            {-46}, 	// 28 CADENAS Eliminé el token -47 " " ' '
            {-55}, 	// 29
            {-58}, 	// 30
            {-59}, 	// 31
            {-56}, 	// 32 REALES Eliminé el token -57 882.31 9288.2^+56
            {-60}, 	// 33
            {-87,-1,221}, 	// 34
            {-30,222}, 	// 35
            {-69,-10,246,223,-11,224,-19,254,225,-20}, 	// 36
            {-16,246,223}, 	// 37
            {-13,218}, 	// 38
            {-14,254,226}, 	// 39
            {-10,246,226,-11,-33,254}, 	// 40
            {-16,246,226}, 	// 41
            {-13,227}, 	// 42
            {-72,-26,228,-40,-30,229}, 	// 43
            {218}, 	// 44
            {-1}, 	// 45
            {-17,230,-18}, //46
            {273,231}, 	// 47
            {-16,273,231}, 	// 48
            {-73,-72,-10,-11}, 	// 49
            {218,232}, 	// 50
            {-30,233}, 	// 51
            {219}, 	// 52
            {-19,246,-16,234,214,235,249,236,-20}, 	// 53
            {246,-16}, 	// 54
            {-16,214}, 	// 55
            {-16,249}, 	// 56
            {-1,237}, 	// 57
            {-30,238}, 	// 58
            {219}, 	// 59
            {-19,246,-16,239,214,240,249,241,-20}, 	// 60
            {246,-16}, 	// 61
            {-16,214}, 	// 62
            {-16,249}, 	// 63
            {242,-26,218,-16,218,-40,-30,243}, 	// 64
            {-108}, 	// 65
            {-73,-108,-10,-11}, 	// 66
            {-17,244,-18}, // 67
            {273,245}, 	// 68
            {-16,273,245}, 	// 69
            {-1,-13,218}, 	// 70
            {-88,-1,-19,246,248,-20}, 	// 71
            {-14,246,248}, 	// 72
            {-1,-10,246,250,-11,251,-19,254,252,-20}, 	// 73
            {-16,246,250}, 	// 74
            {-13,218}, 	// 75
            {-14,254,252}, 	// 76
            {-30}, 	// 77
            {-35}, 	// 78
            {-52}, 	// 79
            {-50}, 	// 80
            {-38}, 	// 81
            {-9}, 	// 82
            {-7}, 	// 83
            {-23}, 	// 84
            {-28}, 	// 85
            {-45}, 	// 86
            {-44}, 	// 87
            {-67,-12,255}, 	// 88
            {-61,-10,273,-11,254,257}, 	// 89
            {-63,-10,273,-11,-19,-75,273,-13,258,254,259,-86,260,-20}, 	// 90
            {-19,254,263,-20}, 	// 91
            {-66,-10,273,-11,254}, 	// 92
            {273,-14}, 	// 93
            {-77,273,-14}, 	// 94
            {-65,254,-66,-10,273,-11,-14}, 	// 95
            {-64,-10,264,-11,254}, 	// 96
            {-74,-10,273,256,-11}, 	// 97
            {-68,-10,273,-11}, 	// 98
            {-16,273,256}, 	// 99
            {-62,273}, 	// 100
            {-75,273,-13,258}, 	// 101
            {-14,254,259}, 	// 102
            {-75,273,-13,268,-86,260}, 	// 103
            {-76,-13,254,262}, 	// 104
            {-14,254,261}, //105
            {-14,254,262}, //106
            {-14,254,263}, //107
            {273,265,-14,254,-14,273,266},  //108
            {-87,-1,267,-1},  //109
            {-16,273,265},  //110
            {-16,273,266},  //111
            {-106},  //112
            {-107},  //113
            {254,261},  //114
            {-94,-10,-11},  //115
            {-95,-10,-11},  //116
            {-76},  //117
            {-97,-10,-11},  //118
            {-98,-10,273,-11},  //119
            {-99,-10,273,-11},  //120
            {-100,-10,273,-11},  //121
            {-101,-10,273,-11},  //122
            {-102,-10,273,-11},  //123
            {-103,-10,273,-16,273,-11},  //124
            {-104,-10,273,-16,273,-11},  //125
            {-105,-10,273,-11},  //126
            {-78,-10,273,-16,273,-11},  //127
            {-79,-10,273,-16,273,-11},  //128
            {-80,-10,273,-16,273,-16,-11},  //129
            {-81,-10,273,-11},  //130
            {-82,-10,273,-11},  //131
            {-83,-10,273,-11},  //132
            {-84,-10,273,-11},  //133
            {-85,-10,273,-11},  //134
            {269},  //135
            {-17,273,272,-18},  //136
            {-16,273,272},  //137
            {275,274},  //138
            {-25,275,274},  //139
            {-24,275,274},  //140
            {277,276},  //141
            {-6,277,276},  //142
            {-5,277,276},  //143
            {-22,277,276},  //144
            {279,278},  //145
            {-26,279,278},  //146
            {-29,279,278},  //147
            {-31,279,278},  //148
            {-3,279,278},  //149
            {-42,279,278},  //150
            {-40,279,278},  //151
            {-32,279,278},  //152
            {-4,279,278},  //153
            {281,280},  //154
            {-37,281,280},  //155
            {-34,281,280},  //156
            {-27,281,280},  //157
            {-41,281,280},  //158
            {-43,281,280},  //159
            {283,282},  //160
            {-48,283,282},  //161
            {-51,283,282},  //162
            {-8,283,282},  //163
            {285,284},  //164
            {-49,285,284},  //165
            {219},  //166
            {286,-1,287},  //167
            {292,-10,273,-11},  //168
            {270},  //169
            {-36},  //170
            {-39},  //171
            {271,288},  //172
            {-10,290,-11},  //173
            {253,273,289},  //174
            {-15,273,-13,273},  //175
            {273,291},  //176
            {-16,273,291},  //177
            {-2},  //178
            {-21},  //179
    };

}
