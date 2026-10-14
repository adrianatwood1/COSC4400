class V_CoVar { public static void main(String[] a) { Xinu.printint(0); } }
class A { public A f() { return new A(); } }
class B extends A { public B f() { return new B(); } }
