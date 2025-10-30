#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>

int main(int argc, char **argv) {
    if (argc != 2)
        return 1;

    char buffer[5000];
    int listenfd;
    struct sockaddr_in servaddr, cliaddr;

    memset(&servaddr, 0, sizeof(servaddr));
    listenfd = socket(AF_INET, SOCK_DGRAM, 0);
    servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
    servaddr.sin_port = htons(atoi(*(argv + 1)));
    servaddr.sin_family = AF_INET;

    int result = bind(listenfd, (struct sockaddr*)&servaddr, sizeof(servaddr));
    socklen_t len = sizeof(cliaddr);

    while (1) {
        int n = recvfrom(listenfd, buffer,
                         sizeof(buffer), 0, (struct sockaddr*)&cliaddr, &len);
        buffer[n] = '\0';

        if (strcmp(buffer, "OFF\n") == 0) {
            break;
        }

        puts(buffer);
    }

    close(listenfd);

    return 0;
}
