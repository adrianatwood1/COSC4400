class T7_Fields { public static void main(String[] a) { Xinu.printint(new B().f(3)); } }
class B { int x; public int f(int y) { x = y; return x; } }
