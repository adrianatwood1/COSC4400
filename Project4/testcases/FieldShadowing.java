class FieldShadowing {
    public static void main(String[] a) {
        Xinu.printint(new Sub().run());
    }
}

class Super {
    int x;
    public int setX(int val) { 
        x = val; 
        return x; 
    }
}

class Sub extends Super {
    int x; 
    public int run() {
        int temp;
        temp = this.setX(10);
        x = 20; 
        Xinu.printint(x); 
        return 0;
    }
}