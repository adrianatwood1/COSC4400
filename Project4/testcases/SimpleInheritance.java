class SimpleInheritance {
    public static void main(String[] a) {
        System.out.println(new SubClass().run());
    }
}

class BaseClass {
    public int run() {
        return 1;
    }
}

class SubClass extends BaseClass {
    public int run() {
        return 2;
    }
}