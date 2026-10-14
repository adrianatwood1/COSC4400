class E_UndefVar { public static void main(String[] a) { Xinu.printint(new T().run()); } }
class T {
public int run() { x = 1; return 0; }
}
