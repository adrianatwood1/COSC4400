class T7_Arrays { public static void main(String[] a) { Xinu.printint(new B().f()); } }
class B { public int f() { int[] q; q = new int[3]; q[1] = 5; return q[1] + q.length; } }
