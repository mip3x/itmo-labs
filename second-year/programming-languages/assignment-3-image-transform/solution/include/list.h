#ifndef LIST_H
#define LIST_H

struct list {
    void* resource;
    struct list* next;
};

void list_destroy(struct list* list);

struct list* list_last(struct list* list);

struct list* node_create(void* resource);

void list_add_back(struct list** old, void* resource);

#endif
