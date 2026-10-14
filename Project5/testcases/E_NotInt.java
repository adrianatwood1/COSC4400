class E_NotInt { public static void main(String[] a) { Xinu.printint(new T().run()); } }
class T {
public int run() { boolean b; b = !5; return 0; }
}
