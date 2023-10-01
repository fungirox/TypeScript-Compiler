package semantica;

public class PolishNotationElement {
    final private String lexema;
    final private int token;
    final private int type;
    final private int line;

    public PolishNotationElement(String lexema, int token, int type, int line) {
        this.lexema = lexema;
        this.token = token;
        this.type = type;
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public String getLexema() {
        return lexema;
    }

    public int getToken() {
        return token;
    }

    public int getType() {
        return type;
    }
}
