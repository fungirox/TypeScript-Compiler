package lexico;

public abstract class Elemento {
    protected String lexema;
    protected int token;
    protected int linea;

    public Elemento(String lexema,final int token,int linea) {
        this.lexema = lexema;
        this.linea = linea;
        this.token = token;
    }

    public String getLexema() {
        return lexema;
    }
    public int getLinea() {
        return linea;
    }
    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public Object[] getRow(){
        return new Object[] {this.token,this.lexema,this.linea};
    }
}
