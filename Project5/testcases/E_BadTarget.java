class E_BadTarget { public static void main(String[] a) { Xinu.printint(new T().run()); } }
class T {
public int run() { int i; i = 3; return i.foo(); }
}
