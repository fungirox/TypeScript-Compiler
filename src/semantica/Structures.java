package semantica;

public class Structures {
    // String etiquetas
    private String type,name,label1,label2,label3,label4;
    private int line,cuadruploId;
    private boolean switchType; // true = number , false = string
    private SemanticaState state;
    public Structures(final String type,final String name,final int line) {
        this.type = type;
        this.name = name;
        this.line = line;
    }

    @Override
    public String toString() {
        return String.format("%15s%15s%15d%15s%15s%15s%15s%15s%15s",this.getType(),this.getName(),this.getLine(),this.getState(),this.isSwitchType()? "number" : "string" ,this.getLabel1(),this.getLabel2(),this.getLabel3(),this.getLabel4());
    }

    public SemanticaState getState() {
        return state;
    }

    public void setState(SemanticaState state) {
        this.state = state;
    }

    public boolean isSwitchType() {
        return switchType;
    }

    public void setSwitchType(boolean switchType) {
        this.switchType = switchType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel1() {
        return label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    public String getLabel3() {
        return label3;
    }

    public void setLabel3(String label3) {
        this.label3 = label3;
    }

    public String getLabel4() {
        return label4;
    }

    public void setLabel4(String label4) {
        this.label4 = label4;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getCuadruploId() {
        return cuadruploId;
    }

    public void setCuadruploId(int cuadruploId) {
        this.cuadruploId = cuadruploId;
    }
}
