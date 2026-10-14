class BadDowncast {
    public static void main(String[] a) {
        Xinu.printint(new FailDemo().run());
    }
}

class ClassA {}

class ClassB extends ClassA {}

class FailDemo {
    public int run() {
        ClassA parent;
        ClassB child;
        
        parent = new ClassA();
        child = parent; 
        
        return 0;
    }
}