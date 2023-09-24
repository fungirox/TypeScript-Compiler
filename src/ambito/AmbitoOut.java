package ambito;



public class AmbitoOut {
    private int strings,numbers,booleans,reals,nulls,classType,voids,errors,total;
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
            case "void" -> this.voids++;
            case "classType" -> this.classType++;
        }

    }
    public void calcuteTotal(){
        this.total=strings+numbers+booleans+reals+nulls+classType+voids+errors;
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
        return String.format("%10d%10d%10d%10d%10d%10d%10d%10d%10d%10d",ambito,strings,numbers,booleans,reals,nulls,classType,voids,errors,total);
    }

}
