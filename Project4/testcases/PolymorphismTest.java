class PolymorphismTest {
    public static void main(String[] a) {
        System.out.println(new PolyDemo().start());
    }
}

class Animal {
    public int speak() {
        return 0;
    }
}

class Dog extends Animal {
    public int speak() {
        return 1;
    }
}

class Cat extends Animal {
    public int speak() {
        return 2;
    }
}

class PolyDemo {
    public int start() {
        Animal a1;
        Animal a2;
        int result;
        
        a1 = new Dog();
        a2 = new Cat();
        
        System.out.println(a1.speak());
        System.out.println(a2.speak());
        
        result = 99;
        return result;
    }
}