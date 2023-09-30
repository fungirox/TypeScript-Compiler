package semantica;

public class Operand extends PolishNotationElement{

    final private String dataType;
    public Operand(String lexema, int token, short type,String dataType) {
        super(lexema, token, type);
        this.dataType =  dataType;
    }

    /**
     * Operands examples
     * 1            number
     * 1.1          real
     * 1.1^1        real
     * true         boolean
     * false        boolean
     * "string"     string
     * 'string'     string
     * null         null
     * un_id        puede tener cualquier valor anterior
     * */
}
