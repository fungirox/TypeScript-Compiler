package ambito;

public class Ambito {
    private final int number,lineStart,type;
    private final String name;
    private int lineFinish;

    public Ambito(int number, int line, int type, String name) {
        this.number = number;
        this.lineStart = line;
        this.type = type;
        this.name = name;
    }

    public int getNumber() {
        return number;
    }


    public int getLineStart() {
        return lineStart;
    }


    public int getType() {
        return type;
    }


    public String getName() {
        return name;
    }

    public int getLineFinish() {
        return lineFinish;
    }

    public void setLineFinish(int lineFinish) {
        this.lineFinish = lineFinish;
    }
}
