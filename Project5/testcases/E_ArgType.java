class E_ArgType { public static void main(String[] a) { Xinu.printint(new T().run()); } }
class T {
public int f(int x) { return x; } public int run() { return this.f(true); }
}
