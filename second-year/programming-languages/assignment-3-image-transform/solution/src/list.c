#include <stdlib.h>

#include "list.h"

void list_destroy(struct list* list) {
    if (!list) return;
    struct list* prev_node;
    while (list) {
        prev_node = list;
        list = list->next;
        if (prev_node->resource) free(prev_node->resource);
        free(prev_node);
    }
}

struct list* list_last(struct list* list) {
    if (!list || !list->next) return list;
    while (list->next) {
        list = list->next;
    }
    return list;
}

struct list* node_create(void* resource) {
    struct list* node = malloc(sizeof(struct list));
    if (node) {
        node->resource = resource;
        node->next = NULL;
    }
    return node;
}

void list_add_back(struct list** old, void* resource) {
    if (!*old) {
        struct list* first_node = node_create(resource);
        *old = first_node;
        return;
    }
    struct list* last_node = list_last(*old);
    last_node->next = node_create(resource);
}
