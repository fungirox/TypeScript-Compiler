package ambito;

import java.util.Arrays;

public class MemberDetails {
    final private String id,classId;
    private String type,typeParametro;
    final private int ambito;
    private int cantParametro,arrayDimension;
    private int [] arrayLength;

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

    public int getAmbito() {
        return ambito;
    }

    public String getId() {
        return id;
    }

    public int getArrayDimension() {
        return arrayDimension;
    }

    public void setArrayDimension(int arrayDimension) {
        this.arrayDimension = arrayDimension;
    }

    public int[] getArrayLength() {
        return arrayLength;
    }

    public void setArrayLength(int[] arrayLength) {
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

    public String getClassId() {
        return classId;
    }

    @Override
    public String toString() {
        String arrayLengthStr = (arrayLength == null) ? "" : Arrays.toString(arrayLength);
        return String.format("%10s%10s%15s%10d%15d%15s%15d%15s", id, type, classId, ambito, cantParametro, typeParametro,arrayDimension,arrayLengthStr);
    }

}
