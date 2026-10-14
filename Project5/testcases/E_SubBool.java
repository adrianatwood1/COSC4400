class E_SubBool { public static void main(String[] a) { Xinu.printint(new T().run()); } }
class T {
public int run() { int i; i = true - 1; return i; }
}
