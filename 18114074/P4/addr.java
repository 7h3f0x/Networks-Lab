import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

class addr {
    public static void main(String args[]) {
        if (args.length < 1) {
            System.err.println("Usage: java addr <CIDR>");
            System.exit(1);
        }

        String[] parts = args[0].split("/");
        if (parts.length != 2) {
            System.err.println("Invalid CIDR notation");
            System.exit(1);
        }

        try {
            int addr = byteToInt(InetAddress.getByName(parts[0]).getAddress());
            int mask_val = Integer.parseInt(parts[1]);
            int subnetMask = (~0) << (32 - mask_val);
            int mask = (~0) >>> (mask_val);
            // System.out.printf("%x\n", subnetMask);
            // System.out.printf("%x\n", mask);
            String m1 = getDottedRepresentation(subnetMask);
            System.out.println("Subnet Mask in Dotted decimal notation is : " + m1);

            addr &= subnetMask;
            String m2 = getDottedRepresentation(addr);
            System.out.println("Network Address in dotted decimal notation is : " + m2);

            String prefix = "Usable Host IP Range is ";
            switch (mask_val) {
                case 32:
                    System.out.printf(
                            "%s : %s - %s\n",
                            prefix,
                            getDottedRepresentation(addr),
                            getDottedRepresentation(addr)
                            );
                    break;
                case 31:
                    System.out.println("There are no usable IP addresses");
                    break;
                default:
                    System.out.printf(
                            "%s : %s - %s\n",
                            prefix,
                            getDottedRepresentation(addr + 1),
                            getDottedRepresentation((addr | mask) - 1)
                            );
            }

        } catch (UnknownHostException e) {
            System.err.println("Error parsing IP address");
            System.exit(1);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing mask");
            System.exit(1);
        }
    }

    public static String getDottedRepresentation(int addr) throws UnknownHostException {
        return InetAddress.getByAddress(intToBytes(addr)).toString().substring(1);
    }

    public static byte[] intToBytes(int addr) {
        return new byte[] {
            (byte)(addr >> 24),
            (byte)((addr >> 16) & 0xff),
            (byte)((addr >> 8) & 0xff),
            (byte)(addr & 0xff)
        };
    }

    public static int byteToInt(byte[] bytes) {
        assert bytes.length == 4 : "byteToInt error";

        int num = 0;
        int n = bytes.length;
        for (int i = 0; i < n; ++i) {
            num += bytes[n - i - 1] << (i * 8);
        }

        return num;
    }
}
