class ObjectTest{
    /*
     * Tests object instantiation, field access, 'this' references, and a bunch of arguments called across all the classes
     */
    public static void main(String[] args) {
        Tester t;
        int val;
        t = new Tester();
        val = t.compute(5, 10);
    }
}

class Tester{
    int field;

    public int compute(int x, int y){
        int res;
        field = x + y;
        res = this.helper();
        return res;
    }

    public int helper(){
        return field *2;
    }
}