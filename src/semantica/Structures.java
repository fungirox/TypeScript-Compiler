package semantica;

public class Structures {
    // String etiquetas
    private String type,name;
    private String switchLabel;
    private final int line;
    private int parameters;
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

    public int getLine() {
        return line;
    }

    public String getSwitchLabel() {
        return switchLabel;
    }

    public void setSwitchLabel(String switchLabel) {
        this.switchLabel = switchLabel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
