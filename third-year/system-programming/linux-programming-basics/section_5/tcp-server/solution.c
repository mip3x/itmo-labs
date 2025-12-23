#include <netdb.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>
#include <stdio.h>

int comp(const void *elem1, const void *elem2) {
    char f = *((char*)elem1);
    char s = *((char*)elem2);

    if (f < s)
        return 1;
    if (f > s)
        return -1;

    return 0;
}

int main(int argc, char **argv) {
    if (argc != 2)
        return 1;

    struct sockaddr_in local;
    int ss = socket(AF_INET, SOCK_STREAM, 0);
    int cs;

    inet_aton("127.0.0.1", &local.sin_addr);
    local.sin_port = htons(atoi(*(argv + 1)));
    local.sin_family = AF_INET;

    int r = bind(ss, (struct sockaddr*)&local, sizeof(local));
    listen(ss, 5);

    cs = accept(ss, NULL, NULL);

    char buf[5000];

    int current_len = 0;
    
    while (1) {
        char current_char;
        int n = read(cs, &current_char, 1);

        if (n <= 0)
            break;

        if (current_char == '\0') {
            buf[current_len] = '\0';
            
            if (current_len == 3 && strcmp(buf, "OFF") == 0)
                break;

            qsort(buf, current_len, 1, comp);
            send(cs, buf, current_len + 1, 0); 

            current_len = 0;
            memset(buf, 0, 5000);

        } else {
            if (current_len < 4999)
                buf[current_len++] = current_char;
        }
    }

    close(ss);
    close(cs);

    return 0;
}
