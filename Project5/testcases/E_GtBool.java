class E_GtBool { public static void main(String[] a) { Xinu.printint(new T().run()); } }
class T {
public int run() { boolean b; b = true > 1; return 0; }
}
