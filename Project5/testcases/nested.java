class NestedTest {
    /*
     *Pushes the parser and visitor through nested while loops, if-else chainsand block scopes 
     */
    public static void main(String[] args) {
        int i;
        boolean flag;
        i = 0;
        flag = true;

        while(i < 10){
            if(i < 5){
                {
                    i = i + 1;
                    flag = !flag;
                }
            } else {
                if(i == 7){
                    i = i + 2;
                } else {
                    i = i + 1;
                }
            }
        }
    }

}