package sintaxis;

import java.util.HashMap;
import java.util.Map;

public class Sintaxis {



    private final Map<Integer,String> errores_sintaxis=new HashMap<Integer,String>(){{
            put(504,"Solo puedes comenzar un programa con let class funcion o interface");
            put(505,"Se esperaba let, interface o class");
            put(506,"Se esperaba fuction");
            put(507,"Se esperaba class");
            put(508,"Se esperaba ;");
            put(509,"Se esperaba un identificador");
            put(510,"Se esperaba ,");
            put(511,"Se esperaba :");
            put(512,"Se esperaba set o get");
            put(513,"Se esperaba un tipo de variable");
    }};
    private final int[][] producciones = {
            {201, -19, 254, 206, -20}, // 0
            {247, 201},                // 1
            {207, 201}                 // 2
    };

}
