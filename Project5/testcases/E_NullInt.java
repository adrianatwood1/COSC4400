class E_NullInt { public static void main(String[] a) { Xinu.printint(new T().run()); } }
class T {
public int run() { int i; i = null; return 0; }
}
