class ExpressionsTest {
    public static void main(String[] args) {
        Xinu.printint(new ExprDemo().run());
    }
}

class ExprDemo {
    public int run() {
        int a; 
        int b;
        a = 10; 
        b = 20;
        Xinu.printint(a + b * 2);
        return 0;
    }
}