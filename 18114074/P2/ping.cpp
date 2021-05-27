#include <iostream>
#include <chrono>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netdb.h>
#include <string.h>
#include <unistd.h>
#include <netinet/ip_icmp.h>


short checksum(short *addr, size_t len) {
    int ans = 0;
    for (size_t idx = 0; idx < len / 2; ++idx) {
        ans += addr[idx];
    }

    while (ans >> 16) {
        ans = (ans >> 16) + (ans & 0xffff);
    }

    return (short)(~ans);
}

int main(int argc, char *argv[]) {
    if (argc != 2) {
        std::cerr << "Usage: sudo " << (argc == 0 ? "./ping" : argv[0])
            << " <server-address>" << std::endl;
        std::exit(1);
    }

    sockaddr_in server_addr, client_addr;
    timeval timeout;
    timeout.tv_sec = 2;
    timeout.tv_usec = 0;
    int sock_fd, yes = 1;

    if((sock_fd = socket(AF_INET, SOCK_RAW, IPPROTO_ICMP)) == -1) {
        std::cerr << "Failed to create socket" << std::endl;
        std::exit(1);
    }

    if(setsockopt(sock_fd, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeval)) == -1) {
        std::cerr << "Failed to set socker options" << std::endl;
        std::exit(1);
    }

    bzero(&server_addr, sizeof(sockaddr_in));
    server_addr.sin_family = AF_INET;

    hostent *host;
    if ((host = gethostbyname(argv[1])) == nullptr) {
        std::cerr << "Couldn't resolve hostname" << std::endl;
        std::exit(1);
    }

    memcpy(&server_addr.sin_addr, host->h_addr, host->h_length);

    pid_t process_pid = getpid();


    std::chrono::time_point<std::chrono::system_clock> start, end;

    for (int i = 0; ; ++i) {
        icmp packet;
        memset(&packet, 0, sizeof(icmp));
        packet.icmp_type = ICMP_ECHO;
        packet.icmp_id = process_pid;
        packet.icmp_code = 0;
        packet.icmp_seq = i;
        packet.icmp_cksum = checksum((short *)&packet, sizeof(icmp));
        start = std::chrono::system_clock::now();
        if ((sendto(sock_fd, &packet, sizeof(icmp), 0,(sockaddr *)&server_addr, sizeof(sockaddr_in))) < 0) {
            std::cerr << "Unable to reach host" << std::endl;

        } else {
            char buf[0x100];
            socklen_t len = sizeof(sockaddr_in);
            if ((recvfrom(sock_fd, (void *)&buf, sizeof(buf), 0, (sockaddr *)&client_addr, &len)) < 0){
                std::cerr << "Server reply timed out" << std::endl;
            } else {
                end = std::chrono::system_clock::now();
                std::chrono::duration<double> time_elapsed = end - start;
                ip* ip_pkt = (ip *)&buf;
                size_t header_len = ip_pkt->ip_hl * 4;
                icmp *icmp_packet = (icmp *)(buf + header_len);
                if (icmp_packet->icmp_type == ICMP_ECHOREPLY) {
                    std::cout << "ECHO REPLY for seq no. "
                        << icmp_packet->icmp_seq << " received in "
                        << time_elapsed.count() * 1000 << " milliseconds" << std::endl;
                }
            }
        }
        sleep(1);
    }




    return 0;
}
