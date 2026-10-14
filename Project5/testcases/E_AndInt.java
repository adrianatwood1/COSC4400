class E_AndInt { public static void main(String[] a) { Xinu.printint(new T().run()); } }
class T {
public int run() { boolean b; b = 1 && 2; return 0; }
}
