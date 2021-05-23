import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


class ping {
    public static void main(String args[]) throws IOException, InterruptedException {
        if (args.length < 1) {
            System.err.println("Usage: java ping <IP Address/Hostname>");
            System.exit(1);
        }
        InetAddress inet;
        final String ipAddr = args[0];
        try {
            inet = InetAddress.getByName(ipAddr);
            while (true) {
                System.out.print("Sending ping request to " + ipAddr + " : ");
                final long t1 = System.currentTimeMillis();
                // Java does not support ICMP packets, which are used to "ping" a host
                // but this method internally uses this type of packet. Even then,
                // ICMP is a very low level packet, and a process needs root priviledges
                // to send these, thus the need to run this with sudo / as root user
                // "ping" does not need this as it is already a setuid binary and thus
                // runs as the root user always
                if (inet.isReachable(10000)) {
                    final long t2 = System.currentTimeMillis();
                    System.out.println("Host is reachable, ping : " + (t2 - t1)  + "ms");
                    Thread.sleep(500);
                }
                else {
                    System.out.println("Couldn't reach host");
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Could not get host by address : " + ipAddr);
            System.exit(1);
        }
    }
}
