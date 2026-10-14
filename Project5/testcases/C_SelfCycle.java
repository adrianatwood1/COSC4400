class C_SelfCycle { public static void main(String[] a) { Xinu.printint(0); } }
class A extends A { public int f() { return 0; } }
