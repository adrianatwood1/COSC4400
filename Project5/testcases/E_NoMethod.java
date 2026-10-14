class E_NoMethod { public static void main(String[] a) { Xinu.printint(new T().run()); } }
class T {
public int run() { return this.nope(); }
}
