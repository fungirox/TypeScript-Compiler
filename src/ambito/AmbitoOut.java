package ambito;

import java.util.Arrays;

public class AmbitoOut {
    private int strings,numbers,booleans,reals,nulls,classType,classes,interfaces,voids,arrays,errors,anons,total;
    private final int ambito;

    public AmbitoOut(int ambito) {
        this.ambito = ambito;
    }


    public void setInType(final String type){
        switch (type){
            case "string" -> this.strings++;
            case "number" -> this.numbers++;
            case "boolean" -> this.booleans++;
            case "real" -> this.reals++;
            case "null" -> this.nulls++;
            case "class" -> this.classes++;
            case "void" -> this.voids++;
            case "Array" -> this.arrays++;
            case "interface" -> this.interfaces++;
            case "classType" -> this.classType++;
            case "anon" -> this.anons++;
            default -> System.out.println("wtf : "+type);
        }

    }
    public void calcuteTotal(){
        this.total=strings+numbers+booleans+reals+nulls+classType+classes+interfaces+voids+arrays+anons+errors;
    }
    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public int getAmbito() {
        return ambito;
    }
    @Override
    public String toString() {
        return String.format("%10d%10d%10d%10d%10d%10d%10d%10d%10d%10d%10d%10d%10d%10d",ambito,strings,numbers,booleans,reals,nulls,classType,classes,interfaces,voids,arrays,errors,anons,total);
    }
}
/**
 * private static String [] rowHeadAmbito={
 *             "ambito","string","number","boolean","real","null","# id","class","interface","@fuction","Array","errores","total"
 *     };
 * */