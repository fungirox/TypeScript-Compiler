package semantica;
// + - / *
public class Operator extends PolishNotationElement{
    // Type is Excel sheet
    private final int priority;
    public Operator(String lexema, int token, int type, int line, int priority) {
        super(lexema, token, type, line);
        this.priority = priority;
    }
    @Override
    public String toString() {
        return String.format("%15s%15d%15d%15d%15d",this.getLexema(),this.getToken(),this.getType(),this.getPriority(),this.getLine());
    }

    public int getPriority() {
        return priority;
    }
}
