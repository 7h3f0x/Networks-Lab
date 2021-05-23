import java.util.Scanner;

class verifier {
    public static void main(String args[]) {
        // Read Input
        Scanner in = new Scanner(System.in);
        String message = in.nextLine().strip();
        String polynomial = in.nextLine().strip();
        in.close();

        // Remove preceding 0 chars from polynomial
        int idx;
        for (idx = 0; idx < polynomial.length(); ++idx) {
            if (polynomial.charAt(idx) != '0') {
                break;
            }
        }
        polynomial = polynomial.substring(idx);

        if (!checkCoeff(message) || !checkCoeff(polynomial)) {
            System.err.println("Invalid input");
            System.exit(1);
        }

        if (!checkPolynomial(polynomial)) {
            System.err.println("Invalid polynomial");
            System.exit(1);
        }

        String res = divide(message, polynomial);
        if (Integer.parseInt(res) != 0) {
            System.out.print("Incorrect, Remainder is: ");
            System.out.println(res);
        }
        else {
            System.out.println("Correct");
        }
    }

    public static String divide(String message, String polynomial) {
        char[] message_array = message.toCharArray();

        int n = message.length() - polynomial.length() + 1;
        int ptr;
        for (ptr = 0; ptr < n; ++ptr) {
            char ch = message_array[ptr];
            if (ch == '1') {
                for (int j = 0; j < polynomial.length(); ++j) {
                    ch = polynomial.charAt(j);
                    message_array[ptr + j] =  (char)('0' + (message_array[ptr + j] ^ ch));
                }
            }
        }

        // Remainder
        StringBuilder builder = new StringBuilder();
        for (; ptr < message_array.length ; ++ptr) {
            builder.append(message_array[ptr]);
        }

        return builder.toString();
    }

    public static boolean checkCoeff(String str) {
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (ch != '0' && ch != '1') {
                return false;
            }
        }
        return true;
    }

    public static boolean checkPolynomial(String polynomial) {
        char[] coeff = polynomial.toCharArray();

        // If polynomial is divisible by x
        if (coeff[coeff.length - 1] != '1') {
            return false;
        }

        // If polynomial is divisible by x + 1
        int n = coeff.length;
        int sum = 0;
        for (int i = 0; i < n; ++i) {
            if ((i & 1) != 0) {
                sum += coeff[n - i - 1];
            }
            else {
                sum -= coeff[n - i - 1];
            }
        }
        if (sum == 0)
            return false;
        return true;
    }
}

