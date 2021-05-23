import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

class ipv4_checksum {
    public static void main(String args[]) {
        final Scanner in = new Scanner(System.in);
        final byte version = get4BitField("Version", in);
        byte IHL = get4BitField("IHL", in);
        while (IHL < 5) {
            System.out.println("IHL should be >= 5");
            IHL = get4BitField("IHL", in);
        }

        byte typeOfService = 0;
        System.out.println("Enter 6 bit value for field Type of Service : ");
        typeOfService = in.nextByte();
        while (typeOfService > 0x3f || typeOfService < 0) {
            System.out.println("This value is not allowed, try again : ");
            typeOfService = in.nextByte();
        }

        final short totalLength = getShortField("Total Length", in);
        final short indentification = getShortField("Indentification", in);

        final byte DF = getBitField("DF", in);
        final byte MF = getBitField("MF", in);


        short fragmentOffset = 0;
        System.out.println("Enter 13 bit value for field precedence : ");
        fragmentOffset = in.nextByte();
        while (fragmentOffset > 0x1fff || fragmentOffset < 0) {
            System.out.println("This value is not allowed, try again : ");
        }

        final byte ttl = getByteField("Time to live", in);
        final byte protocol = getByteField("Protocol", in);

        final short checksum = getShortField("Checksum", in);

        in.nextLine(); // consume newline ar end of last nextInt call
        final byte[] src = getIPAddress("source address", in);
        final byte[] dest = getIPAddress("destination address", in);
        in.close();

        final short[] vals = new short[10];
        vals[0] = (short)((version << 12) | (IHL << 8) | (typeOfService << 2));
        vals[1] = totalLength;
        vals[2] = indentification;
        vals[3] = (short)((DF << 14) | (MF << 13) | fragmentOffset);
        vals[4] = (short)((ttl << 8 ) | protocol);
        vals[5] = checksum;
        vals[6] = (short)((src[0] << 8) | (src[1]));
        vals[7] = (short)((src[2] << 8) | (src[3]));
        vals[8] = (short)((dest[0] << 8) | (dest[1]));
        vals[9] = (short)((dest[2] << 8) | (dest[3]));

        calculateChecksum(vals);

    }

    public static String shortToPaddedBinaryString(short num) {
        return String.format("%16s", Integer.toBinaryString(num)).replace(' ', '0');
    }

    public static void debugPrintAsBinary(short[] vals) {
        for (int idx = 0; idx < vals.length; ++idx) {
            final String s1 = shortToPaddedBinaryString(vals[idx]);
            if ((idx & 1) == 1) {
                System.out.println(s1);
            } else {
                System.out.print(s1 + " | ");
            }
        }
    }

    public static void calculateChecksum(short[] vals) {
        debugPrintAsBinary(vals);
        long sum = 0;
        System.out.println("\nThe 16-bit values for the header are : ");
        for (final short num :vals) {
            System.out.printf("                  %04x\n", num);
            sum += num;
        }
        System.out.printf("\nThe sum is :  %08x\n", sum);
        while ((sum >>> 16) != 0) {
            System.out.println("Wrap the result by adding the overflow part back : ");
            final long part1 = (sum >>> 16);
            final long part2 = (sum & 0xffff);
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
                final InetAddress addr = InetAddress.getByName(in.nextLine());
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
