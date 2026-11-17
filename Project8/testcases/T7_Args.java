class T7_Args { public static void main(String[] a) { Xinu.printint(new E().g(new E().h(1), 2)); } }
class E { int x; public int h(int p) { return x + p; } public int g(int p, int q) { return this.h(x) + p + q; } }
