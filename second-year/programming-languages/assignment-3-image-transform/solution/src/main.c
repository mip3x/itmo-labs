#include <stdlib.h>

#include "image.h"
#include "io.h"
#include "list.h"
#include "status.h"
#include "transformation.h"
#include "util.h"

int main(int argc, char** argv) {
    status op_status;

    input input = { 0 };
    io_status input_status = process_input(argc, argv, &input);
    op_status = (status){.type = STATUS_TYPE_IO, .value.io = input_status};
    handle_status(op_status, NULL);

    struct image image = {0};
    op_status = load_image(input.original_image_path, &image);
    struct list* resources_to_free = node_create(image.data);
    handle_status(op_status, resources_to_free);

    op_status = (status){.type = STATUS_TYPE_TRANSFORM_IMAGE, .value.transform_image = TRANSFORM_OK};
    struct image result_image = input.func(image);
    if (!result_image.data) op_status.value.transform_image = TRANSFORM_ERROR;
    if (input.func != none) {
        handle_status(op_status, resources_to_free);
        list_add_back(&resources_to_free, result_image.data);
    }

    op_status = save_image(input.transformed_image_path, &result_image);
    handle_status(op_status, resources_to_free);

    list_destroy(resources_to_free);

    return 0;
}
