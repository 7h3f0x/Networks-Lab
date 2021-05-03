#include <ios>
#include <iostream>
#include <iomanip>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <ifaddrs.h>
#include <linux/if_packet.h>

void printMACAddress(unsigned char *buf) {
    std::ios state(nullptr);
    state.copyfmt(std::cout); // save current formatting settings

    std::cout
        << std::hex
        << std::setfill('0');

    for (int i = 0; i < 5; ++i) {
        std::cout << std::setw(2) << (int)buf[i] << ":";
    }

    std::cout << std::setw(2) << (int)buf[5] << std::endl;

    std::cout.copyfmt(state); // restore previous formatting settings
    return;
}

void printIPAddress(sockaddr_in *in_addr) {
  char *s_addr = inet_ntoa(in_addr->sin_addr);
  std::cout << s_addr << std::endl;
}

int main(void) {
    ifaddrs *ifaptr = (struct ifaddrs *)malloc(sizeof(ifaddrs));

    const size_t HOSTNAME_SZ = 0x100;
    char hostname[HOSTNAME_SZ] = {0};
    gethostname(hostname, HOSTNAME_SZ);

    std::cout << "Hostname is : " << hostname << std::endl;

    if (getifaddrs(&ifaptr) != 0) {
        std::cerr << "Unable to get network interfaces" << std::endl;
        exit(1);
    }

    // to iterate over results
    struct ifaddrs *it = ifaptr;
    while (it != nullptr) {
        if (it->ifa_name == nullptr || strcmp(it->ifa_name, "lo") != 0){
            sockaddr *addr = it->ifa_addr;
            // If it is an IPv4 addr
            if (addr->sa_family == AF_INET) {
                std::cout << "IP Address for " << it->ifa_name << " is: ";
                printIPAddress((sockaddr_in *)addr);
            // If it is a physical address
            } else if (addr->sa_family == AF_PACKET) {
                std::cout << "MAC Address for " << it->ifa_name << " is: ";
                sockaddr_ll *s = (sockaddr_ll *)addr;
                printMACAddress(s->sll_addr);
            }
        }
        it = it->ifa_next;
    }

    freeifaddrs(ifaptr);
    ifaptr = nullptr;
    return 0;
}

