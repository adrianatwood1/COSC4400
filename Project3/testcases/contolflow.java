class ControlFlowTest {
    /**
     * Tests conditional if-else branching and iterative while loops using numeric variables
     */
    public static void main(String[] args) {
        int x;
        x = 10;
        if(x < 15){
            System.out.println(1);
        } else {
            System.out.println(0);
        }

        while(0 < x){
            x = x - 1;
        }
    }
}