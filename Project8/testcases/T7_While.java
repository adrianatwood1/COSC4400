class T7_While { public static void main(String[] a) { Xinu.printint(new W().f(3)); } }
class W { public int f(int n) { int s; s = 0; while (n > 0 && s < 9) if (n < 2) s = s + 1; else s = s + 2; return s; } }
