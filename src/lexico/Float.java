package lexico;

public class Float extends Token{
    public final float value;
    public Float(float value){
        super(Tag.FLOAT);
        this.value = value;
    }
    public String toString(){
        return "" + value;
    }
}
