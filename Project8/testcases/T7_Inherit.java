class T7_Inherit { public static void main(String[] a) { A p; p = new B(); Xinu.printint(p.g()); } }
class A { int x; public int g() { return x; } }
class B extends A { int y; public int g() { return x + y; } }
