public class ObjectClass {
    private double[] vector;
    private String cat;
    public ObjectClass(double[] vector, String cat){
        this.vector = vector;
        this.cat = cat;
    }

    public double[] getVector() {
        return vector;
    }

    public String getCat() {
        return cat;
    }
}
