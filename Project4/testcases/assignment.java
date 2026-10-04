class AssignmentTest{
   /*
    * Verifies variable assignments and nested block using integers and booleans
    */
    public static void main(String[] args) {
        int a;
        boolean b;
        a = 42;
        b = true;
        {
            a = a + 8;
            b = !b;
        }
    }
}