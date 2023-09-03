package ambito;

import java.util.Arrays;

public class MemberDetails {
    final private String id,classId;
    private String type,typeParametro;
    final private int ambito,arrayDimension;
    private int cantParametro;
    final private int [] arrayLength;

    public MemberDetails(String id, String type, String classId, String typeParametro, int ambito, int cantParametro, int arrayDimension, int[] arrayLength) {
        this.id = id;
        this.type = type;
        this.classId = classId;
        this.typeParametro = typeParametro;
        this.ambito = ambito;
        this.cantParametro = cantParametro;
        this.arrayDimension = arrayDimension;
        this.arrayLength = arrayLength;
    }

    public int getCantParametro() {
        return cantParametro;
    }

    public void setCantParametro(int cantParametro) {
        this.cantParametro = cantParametro;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeParametro() {
        return typeParametro;
    }

    public void setTypeParametro(String typeParametro) {
        this.typeParametro = typeParametro;
    }

    @Override
    public String toString() {
        return String.format("%10s%10s%15s%10d%15d%15s%15d", id, type, classId, ambito, cantParametro, typeParametro,arrayDimension);
    }
}
