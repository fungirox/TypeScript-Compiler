package ambito;

public class Area {
    private final int lineStart,ambito,type;
    private final boolean execute;
    private int lineFinish;

    public Area(int lineStart, int ambito, int type, boolean execute) {
        this.lineStart = lineStart;
        this.ambito = ambito;
        this.type = type;
        this.execute = execute;
    }

    public int getLineStart() {
        return lineStart;
    }

    public int getAmbito() {
        return ambito;
    }

    public int getType() {
        return type;
    }

    public boolean isExecute() {
        return execute;
    }

    public int getLineFinish() {
        return lineFinish;
    }

    public void setLineFinish(int lineFinish) {
        this.lineFinish = lineFinish;
    }
}
//execute: true:ejecucion false:declaracion
//type: solo para declaracion, like dec_var