package semantica;

public class Structures {
    // String etiquetas
    private final String type,name;
    private String label1,label2,label3,label4;
    private final int line;
    private int cuadruploId,parameters;
    private boolean switchType; // true = number , false = string
    private ObjectData calling_array;
    public Structures(final String type,final String name,final int line) {
        this.type = type;
        this.name = name;
        this.line = line;
    }
    public Structures(final String type,final String name,final int line,final boolean swtichType) {
        this.type = type;
        this.name = name;
        this.line = line;
        this.switchType = swtichType;
    }

    @Override
    public String toString() {
        return String.format("%15s%15s%15d%15s%15d",this.getType(),this.getName(),this.getLine(),this.isSwitchType()? "number" : "string" ,this.getParameters());
    }

    public int getParameters() {
        return parameters;
    }

    public void setParameters(int parameters) {
        this.parameters = parameters;
    }

    public ObjectData getCalling_array() {
        return calling_array;
    }

    public void setCalling_array(ObjectData calling_array) {
        this.calling_array = calling_array;
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


    public String getName() {
        return name;
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

    public int getCuadruploId() {
        return cuadruploId;
    }

    public void setCuadruploId(int cuadruploId) {
        this.cuadruploId = cuadruploId;
    }
}
