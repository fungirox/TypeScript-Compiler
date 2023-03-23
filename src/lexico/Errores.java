package lexico;

import java.util.HashMap;
import java.util.Map;

public class Errores extends Elemento {
    private String desc;
    private final String tipo="Error léxico";//es temporal
    public Errores(final String lexema, final int token, final int linea) {
        super(lexema,token,linea);
        establecerError(token);

    }

    private void establecerError(final int token){
        switch (token){
            case 500:
                this.desc="Carácter invalido";
                break;
            case 501:
            case 46:
            case 49:
                this.desc="Constante cadena invalida";
                break;
            case 57:
            case 58:
                this.desc="Faltó cerrar comentario multilinea";
                this.setToken(506);
                break;
            case 502:
                this.desc="Constante real invalida, se espera un número";
                break;
            case 503:
                this.desc="Constante real invalida, se espera un número, + o -";
                break;
            default:
                this.desc="Faltó cerrar comentario multilinea";
                break;
        }
    }
    public Object[] getRow(){
        return new Object[] {this.token,this.desc,this.lexema,this.tipo,this.linea};
    }

    public String getDesc() {
        return desc;
    }

    public String getTipo() {
        return tipo;
    }
}
