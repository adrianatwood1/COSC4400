class ControlFlowTest {
    public static void main(String[] args) {
        Xinu.printint(new CFDemo().run());
    }
}

class CFDemo {
    public int run() {
        int a;
        a = 5;
        if (a < 10) { 
            Xinu.printint(1); 
        } else { 
            Xinu.printint(0); 
        }
        while (a > 0) { 
            a = a - 1; 
        }
        return a;
    }
}