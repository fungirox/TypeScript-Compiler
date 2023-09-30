package semantica;

public class PolishNotationElement {
    final private String lexema;
    final private int token;
    final private short type;

    public PolishNotationElement(final String lexema, final int token, final short type) {
        this.lexema = lexema;
        this.token = token;
        this.type = type;
    }

    public String getLexema() {
        return lexema;
    }

    public int getToken() {
        return token;
    }

    public short getType() {
        return type;
    }
}
