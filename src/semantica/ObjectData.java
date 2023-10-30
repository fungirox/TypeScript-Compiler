package semantica;

public class ObjectData {
    // Para arrays y funciones
    private final String lexema,dataType;
    private final int type,line,ambito,declarationID;
    private final int parDim;
    private int newAmbito;
    private final boolean isFun;

    public ObjectData(String lexema, String dataType, int type, int line, int ambito, int parDim, int declarationID) {
        this.lexema = lexema;
        this.dataType = dataType;
        this.type = type;
        this.line = line;
        this.ambito = ambito;
        this.declarationID = declarationID;
        this.parDim = parDim;
        this.isFun = false;
    }

    public ObjectData(String lexema, String dataType, int type, int line, int ambito, int parDim, int newAmbito, int declarationID) {
        this.lexema = lexema;
        this.dataType = dataType;
        this.type = type;
        this.line = line;
        this.ambito = ambito;
        this.declarationID = declarationID;
        this.parDim = parDim;
        this.newAmbito = newAmbito;
        this.isFun = true;
    }

    public String getLexema() {
        return lexema;
    }

    public String getDataType() {
        return dataType;
    }

    public int getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getAmbito() {
        return ambito;
    }

    public int getParDim() {
        return parDim;
    }

    public int getNewAmbito() {
        return newAmbito;
    }

    public boolean isFun() {
        return isFun;
    }

    @Override
    public String toString() {
        return "ObjectData{" +
                "lexema='" + lexema + '\'' +
                ", dataType='" + dataType + '\'' +
                ", type=" + type +
                ", line=" + line +
                ", ambito=" + ambito +
                ", parDim=" + parDim +
                ", newAmbito=" + newAmbito +
                ", isFun=" + isFun +
                '}';
    }
}
