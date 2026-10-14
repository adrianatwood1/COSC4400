class Upcasting {
    public static void main(String[] a) {
        Xinu.printint(new Demo().run());
    }
}

class Parent { 
    public int foo() { return 1; } 
}

class Child extends Parent { 
    public int foo() { return 2; } 
}

class Demo {
    public int run() {
        Parent p;
        p = new Child(); 
        Xinu.printint(p.foo()); 
        return 0;
    }
}