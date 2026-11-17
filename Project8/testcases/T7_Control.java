class T7_Control { public static void main(String[] a) { Xinu.printint(new C().f(3)); } }
class C { public int f(int n) { int s; s = 0; while (n > 0) { if (n < 2 && !(s == 5) || n == 3) { s = s + n; } else { s = s - 1; } n = n - 1; } return s; } }
