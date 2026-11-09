class T7_Bool { public static void main(String[] a) { Xinu.print("hi"); Xinu.print("hi"); Xinu.printint(new D().f(2)); } }
class D { public int f(int n) { boolean b; int r; b = n < 3 && n > 1; b = !b; b = n < 2 || b; r = 0; if (b) { r = 1; } else { r = 2; } return r; } }
