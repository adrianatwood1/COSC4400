class E_NoField { public static void main(String[] a) { Xinu.printint(new T().run()); } }
class T {
public int run() { T t; t = new T(); return t.zzz; }
}
