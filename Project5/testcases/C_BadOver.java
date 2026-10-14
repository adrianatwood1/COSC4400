class C_BadOver { public static void main(String[] a) { Xinu.printint(0); } }
class A { public int f(int x) { return x; } }
class B extends A { public int f(boolean x) { return 0; } }
