class PolymorphismTest {
    public static void main(String[] a) {
        Xinu.printint(new PolyDemo().start());
    }
}

class Animal {
    public int speak() { return 0; }
}

class Dog extends Animal {
    public int speak() { return 1; }
}

class Cat extends Animal {
    public int speak() { return 2; }
}

class PolyDemo {
    public int start() {
        Animal a1;
        Animal a2;
        int result;
        
        a1 = new Dog();
        a2 = new Cat();
        
        Xinu.printint(a1.speak());
        Xinu.printint(a2.speak());
        
        result = 99;
        return result;
    }
}