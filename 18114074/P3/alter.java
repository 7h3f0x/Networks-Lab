import java.util.Scanner;

class alter {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        char[] message = in.nextLine().strip().toCharArray();
        String polynomial = in.nextLine().strip();
        in.close();

        int pos = -1;
        if (args.length >= 1) {
            try {
                pos = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {}
        }

        if (pos >= 0) {
            message[pos] = (char)('0' + (message[pos] ^ '1'));
        }

        System.out.println(new String(message));
        System.out.println(polynomial);
    }
}

