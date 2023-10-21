package semantica;

import java.util.Arrays;

public class Operand extends PolishNotationElement{

    final private String dataType;
    final private String []types={"number","real","boolean","string","null","custom","var"};

    public Operand(String lexema, int token, int type, int line) {
        super(lexema, token, type, line);
        this.dataType = types[type];
    }
    public String getDataType() {
        return dataType;
    }
    @Override
    public String toString() {
        return String.format("%15s%15d%15d%15s%15s",this.getLexema(),this.getToken(),this.getType(),this.getDataType(),this.getLine());
    }
    /* * data types // number = excel column
     * 0 : number
     * 1 : real
     * 2 : boolean
     * 3 : string
     * 4 : null
     * 5 : var
     * */
    /* *
     * Operands examples
     * 1            number  -55
     * 1.1          real    -56
     * 1.1^1        real    -57
     * true         boolean -59
     * false        boolean -60
     * "string"     string  -46
     * 'string'     string  -47
     * null         null    -61
     * un_id        puede tener cualquier valor anterior
     * */
}
