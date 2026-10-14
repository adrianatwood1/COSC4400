class E_ArrIdx { public static void main(String[] a) { Xinu.printint(new T().run()); } }
class T {
public int run() { int[] a; a = new int[3]; return a[true]; }
}
