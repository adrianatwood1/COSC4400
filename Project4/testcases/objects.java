class ObjectsTest {
    public static void main(String[] args) {
        Xinu.printint(new ObjDemo().run());
    }
}

class ObjDemo {
    public int run() {
        ObjDemo obj;
        obj = new ObjDemo();
        Xinu.printint(99);
        return 0;
    }
}