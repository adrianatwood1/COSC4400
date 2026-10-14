class C_Cycle2 { public static void main(String[] a) { Xinu.printint(0); } }
class A extends B { public int f() { return 0; } }
class B extends A { public int g() { return 0; } }
