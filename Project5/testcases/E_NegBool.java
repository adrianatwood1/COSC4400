class E_NegBool { public static void main(String[] a) { Xinu.printint(new T().run()); } }
class T {
public int run() { int i; i = -true; return i; }
}
