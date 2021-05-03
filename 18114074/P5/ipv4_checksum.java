import java.util.Scanner;
import java.net.InetAddress;
import java.net.UnknownHostException;

class ipv4_checksum {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        byte version = get4BitField("Version", in);
        byte IHL = get4BitField("IHL", in);
        while (IHL < 5) {
            System.out.println("IHL should be >= 5");
            IHL = get4BitField("IHL", in);
        }

        byte precedence = 0;
        System.out.println("Enter 3 bit value for field precedence : ");
        precedence = in.nextByte();
        while (precedence > 0x7 || precedence < 0) {
            System.out.println("This value is not allowed, try again : ");
            precedence = in.nextByte();
        }

        byte D = getBitField("D", in);
        byte T = getBitField("T", in);
        byte R = getBitField("R", in);

        short totalLength = getShortField("Total Length", in);
        short indentification = getShortField("Indentification", in);

        byte DF = getBitField("DF", in);
        byte MF = getBitField("MF", in);

        byte ttl = getByteField("Time to live", in);
        byte protocol = getByteField("Protocol", in);

        short fragmentOffset = 0;
        System.out.println("Enter 13 bit value for field precedence : ");
        fragmentOffset = in.nextByte();
        while (fragmentOffset > 0x1fff || fragmentOffset < 0) {
            System.out.println("This value is not allowed, try again : ");
        }

        in.nextLine(); // consume newline ar end of last nextInt call
        byte[] src = getIPAddress("source address", in);
        byte[] dest = getIPAddress("destination address", in);
        in.close();

        short[] vals = new short[9];
        vals[0] = (short)((version << 12) | (IHL << 8) | (precedence << 5 ) |
                    (D << 4) | (T << 3) | (R << 2));

        vals[1] = (totalLength);
        vals[2] = indentification;
        vals[3] = (short)((DF << 14) | (MF << 13) | fragmentOffset);
        vals[4] = (short)((ttl << 8 ) | protocol);
        vals[5] = (short)((src[0] << 8) | (src[1]));
        vals[6] = (short)((src[2] << 8) | (src[3]));
        vals[7] = (short)((dest[0] << 8) | (dest[1]));
        vals[8] = (short)((dest[2] << 8) | (dest[3]));

        calculateChecksum(vals);

    }

    public static void calculateChecksum(short[] vals) {
        long sum = 0;
        System.out.println("The 16-bit values for the header are : ");
        for (short num :vals) {
            System.out.printf("                  %04x\n", num);
            sum += num;
        }
        System.out.printf("\nThe sum is :  %08x\n", sum);
        while ((sum >>> 16) != 0) {
            System.out.println("Wrap the result by adding the overflow part back : ");
            long part1 = (sum >>> 16);
            long part2 = (sum & 0xffff);
            System.out.printf("                  %04x\n", part2);
            System.out.printf("                  %04x\n", part1);
            sum = part1 + part2;
            System.out.printf("\nThe sum is :  %08x\n", sum);
        }

        System.out.println("\nNow finally take 1s Complement of it");
        System.out.printf("The value is:     %04x\n", ~((short)sum));

    }

    public static byte get4BitField(String fieldName, Scanner in) {
        byte val = 0;
        System.out.println("Enter 4 bit value for field " + fieldName + " : ");
        val = in.nextByte();
        while (val > 0xf || val < 0) {
            System.out.println("This value is not allowed, try again : ");
        }
        return val;
    }

    public static byte[] getIPAddress(String fieldName, Scanner in) {
        System.out.println("Enter " + fieldName + " IP address in dotted notation : ");
        while (true) {
            try {
                InetAddress addr = InetAddress.getByName(in.nextLine());
                return addr.getAddress();
            } catch (UnknownHostException e) {
                System.out.println("Couldn't parse that, try again : ");
                continue;
            }
        }
    }

    public static byte getByteField(String fieldName, Scanner in) {
        short val = 0;
        System.out.println("Enter 8 bit value for field " + fieldName + " : ");
        val = in.nextShort();
        while (val > 0xff || val < 0) {
            System.out.println("This value is not allowed, try again : ");
        }
        return (byte)val;
    }

    public static short getShortField(String fieldName, Scanner in) {
        int val = 0;
        System.out.println("Enter 16 bit value for field " + fieldName + " : ");
        val = in.nextInt();
        while (val > 0xffff || val < 0) {
            System.out.println("This value is not allowed, try again : ");
        }
        return (short)val;
    }

    public static byte getBitField(String fieldName, Scanner in) {
        byte val = 0;
        System.out.println("Enter bit value for field " + fieldName + " : ");
        val = in.nextByte();
        while (val > 1 || val < 0) {
            System.out.println("This value is not allowed, try again : ");
        }
        return val;
    }

}
