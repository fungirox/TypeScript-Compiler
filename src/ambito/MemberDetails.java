package ambito;

import java.util.Arrays;

public class MemberDetails {
    final private String id,classId;
    private String type,typeParametro;
    final private int ambito,arrayDimension;
    private int cantPrametro;
    final private int [] arrayLength;

    public MemberDetails(String id, String type, String classId, String typeParametro, int ambito, int cantParametro, int arrayDimension, int[] arrayLength) {
        this.id = id;
        this.type = type;
        this.classId = classId;
        this.typeParametro = typeParametro;
        this.ambito = ambito;
        this.cantPrametro = cantParametro;
        this.arrayDimension = arrayDimension;
        this.arrayLength = arrayLength;
    }

    public int getCantPrametro() {
        return cantPrametro;
    }

    public void setCantPrametro(int cantPrametro) {
        this.cantPrametro = cantPrametro;
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
        return "MemberDetails{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", classId='" + classId + '\'' +
                ", typeParametro='" + typeParametro + '\'' +
                ", ambito=" + ambito +
                ", cantPrametro=" + cantPrametro +
                ", arrayDimension=" + arrayDimension +
                ", arrayLength=" + Arrays.toString(arrayLength) +
                '}';
    }
}
