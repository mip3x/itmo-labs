#include <errno.h>
#include <stdio.h>
#include <unistd.h>

#include "ipc.h"
#include "process.h"

/** Send a message to the process specified by id.
 *
 * @param self    Any data structure implemented by students to perform I/O
 * @param dst     ID of recepient
 * @param msg     Message to send
 *
 * @return 0 on success, any non-zero value on error
 */
int send(void * self, local_id dst, const Message * msg) {
    Process* process = (Process*)self;

    int fd = process->pipes[process->id][dst][1];
    size_t total = sizeof(MessageHeader) + msg->s_header.s_payload_len;
    size_t sent = 0;

    const char *data = (const char*)msg;
    while (sent < total) {
        ssize_t result = write(fd, data + sent, total - sent);
        if (result == -1) {
            if (errno == EINTR)
                continue;
            return -1;
        }

        if (result == 0)
            return -1;

        sent += (size_t)result;
    }

    return 0;
}

//------------------------------------------------------------------------------

/** Send multicast message.
 *
 * Send msg to all other processes including parrent.
 * Should stop on the first error.
 * 
 * @param self    Any data structure implemented by students to perform I/O
 * @param msg     Message to multicast.
 *
 * @return 0 on success, any non-zero value on error
 */
int send_multicast(void * self, const Message * msg) {
    Process* process = (Process*)self;

    for (local_id dst = 0; dst < process->total_processes; dst++) {
        if (dst == process->id)
            continue;

        if (send(self, dst, msg) != 0)
            return -1;
    }
    
    return 0;
}

// reads data in a cycle
static int read_exact(int fd, void * buffer, size_t size) {
    char *data = buffer;
    size_t received = 0;

    while (received < size) {
        ssize_t result = read(fd, data + received, size - received);
        if (result == -1) {
            if (errno == EINTR)
                continue;
            return -1;
        }

        if (result == 0)
            return -1;

        received += (size_t)result;
    }

    return 0;
}

//------------------------------------------------------------------------------

/** Receive a message from the process specified by id.
 *
 * Might block depending on IPC settings.
 *
 * @param self    Any data structure implemented by students to perform I/O
 * @param from    ID of the process to receive message from
 * @param msg     Message structure allocated by the caller
 *
 * @return 0 on success, any non-zero value on error
 */
int receive(void * self, local_id from, Message * msg) {
    Process *process = (Process*)self;
    int fd = process->pipes[from][process->id][0];

    if (read_exact(fd, &msg->s_header, sizeof(MessageHeader)) != 0)
        return -1;

    if (msg->s_header.s_magic != MESSAGE_MAGIC || msg->s_header.s_payload_len > MAX_PAYLOAD_LEN)
        return -1;

    return read_exact(fd, msg->s_payload, msg->s_header.s_payload_len);
}

//------------------------------------------------------------------------------

/** Receive a message from any process.
 *
 * Receive a message from any process, in case of blocking I/O should be used
 * with extra care to avoid deadlocks.
 *
 * @param self    Any data structure implemented by students to perform I/O
 * @param msg     Message structure allocated by the caller
 *
 * @return 0 on success, any non-zero value on error
 */
int receive_any(void * self, Message * msg) {
    return 0;
}