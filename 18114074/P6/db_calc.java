import java.util.Scanner;


class db_calc {
    public static void main(String args[]) {
        final Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("Enter transmit power : ");
            String input;
            try {
                input = in.nextLine().strip();
            } catch (java.util.NoSuchElementException e) {
                in.close();
                break;
            }
            final String[] parts = input.split(" ");
            double temp;
            try {
                temp = Double.parseDouble(parts[0]);
            } catch (java.lang.NumberFormatException e) {
                System.err.println("Some parsing error, try again.");
                continue;
            }
            switch (parts[1]) {
                case "dBW":
                    System.out.println("In W: " + from_dbw(temp));
                    break;
                case "dBm":
                    System.out.println("In W: " + (from_dbw(temp) / 1000));
                    break;
                case "W":
                    temp = to_db(temp);
                    System.out.println("In dBW : " + to_db(temp));
                    System.out.println("In dBm : " + (to_db(temp) + 30));
                    break;
                default:
                    System.out.println("Unknown unit : " + parts[1]);
            }
        }
    }

    public static double to_db(double power) {
        return Math.log10(power) * 10;
    }

    public static double from_dbw(double db) {
        return Math.pow(10, db / 10);
    }
}
