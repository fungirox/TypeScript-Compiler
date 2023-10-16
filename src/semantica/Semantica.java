package semantica;

public class Semantica {
    private int rule,line,ambito;
    private String topStack,realValue;
    private boolean state; // true = aceptado false = error

    public Semantica(int rule, int line, int ambito, String topStack, String realValue, boolean state) {
        this.rule = rule;
        this.line = line;
        this.ambito = ambito;
        this.topStack = topStack;
        this.realValue = realValue;
        this.state = state;
    }
    @Override
    public String toString() {
        return String.format("%15d%15s%15s%15s%15d%15d",this.getRule(),this.getTopStack(),this.getRealValue(),this.isState()?"Aceptado":"ERROR",this.getLine(),this.getAmbito());
    }
    public int getRule() {
        return rule;
    }

    public void setRule(int rule) {
        this.rule = rule;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getAmbito() {
        return ambito;
    }

    public void setAmbito(int ambito) {
        this.ambito = ambito;
    }

    public String getTopStack() {
        return topStack;
    }

    public void setTopStack(String topStack) {
        this.topStack = topStack;
    }

    public String getRealValue() {
        return realValue;
    }

    public void setRealValue(String realValue) {
        this.realValue = realValue;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
