class MultilevelInheritance {
    public static void main(String[] a) {
        System.out.println(new C().test());
    }
}

class A {
    int val;
    
    public int init() {
        val = 10;
        return val;
    }
    
    public int foo() {
        return 1;
    }
}

class B extends A {
    public int foo() {
        return 2;
    }
}

class C extends B {
    public int test() {
        int temp;
        temp = this.init();
        System.out.println(temp);
        return this.foo();
    }
    
    public int foo() {
        return 3;
    }
}