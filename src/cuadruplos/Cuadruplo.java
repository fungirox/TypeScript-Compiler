package cuadruplos;

public class Cuadruplo {
    private String label,action,value1,value2,result;
    private int line,cuadrupoId;
    private String type; // La fila del excel

    // Constructor para tipo JMP normal
    public Cuadruplo ( ) {

    }
    @Override
    public String toString() {
        return String.format("%15s%15s%15s%15s%15s%15d",
                this.label,
                this.action,
                this.value1,
                this.value2,
                this.result,
                this.type,
                this.line);
    }

    public void label(final int num,final String type){
        this.type = type;
        this.label = type + "-E" + num;
    }
    public void label_begin_end(final boolean begin,final String id){
        this.label = begin ? "begin" : "end";
        this.action = id;
    }
    public void action_JMP(final String result){
        this.action = "JMP";
        this.result = result;
        this.type = "JMP";
    }
    public void action_JF(final String value1,final String result){
        this.action = "JF";
        this.value1 = value1;
        this.result = result;
        this.type = "JF";
    }
    public void action_JT(final String value1,final String result){
        this.action = "JT";
        this.value1 = value1;
        this.result = result;
        this.type = "JT";
    }
    public void action_operation(final String action,final String value1,final String value2,final String result){
        this.action = action;
        this.value1 = value1;
        this.value2 = value2;
        this.result = result;

        this.type = switch (action){
            case "OR" -> "Ope-Log";
            case "=","!=","<",">" -> "Ope-Rel";
            case "add","sub","mult" -> "Oper-Arit";
            case "asig" -> "asig";

            default -> "error";
        };
    }
    public void array(final String id){
        this.action = "array";
        this.value1 = id;
        this.type = "array";
    }
    public void array_positions_fuction_parameters(final String value1, final String value2, final String result){
        this.value1 = value1;
        this.value2 = value2;
        this.result = result;
    }
    public void call_native(final String nativeF){
        this.action = "call";
        this.value1 = nativeF;
        this.type = "call";
    }


}
