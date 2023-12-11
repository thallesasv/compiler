package compiler.lexico;

public class Literal extends Token {
    public final String value;
    public Literal(String value){
        super(Tag.STRING);
        this.value = value;
    }
    public String toString(){
        return "<" + value + ", " + tag + ">";
    }
}
