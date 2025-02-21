#include "status.h"
#include "image.h"
#include "io.h"

const char* get_description_by_status(status status) {
    switch (status.type) {
        case STATUS_TYPE_IO: 
            switch (status.value.io) {
                case IO_OK: return "Success";
                case IO_ENOENT: return "No such file or directory";
                case IO_EUNKNOWN: return "Unknown error";
                case IO_E2BIG: return "Too many arguments";
                case IO_E2SMALL: return "Not enough arguments";
                case IO_EACCESS: return "Permission denied";
                case IO_ECLOSE: return "Failed to close file";
                case IO_EUNKNOWN_OP: return "Unknown operation";
                default: return "Unknown IO status";
            }
        case STATUS_TYPE_READ_IMAGE:
            switch (status.value.read_image) {
                case READ_OK: return "Success";
                case READ_INVALID_HEADER: return "Invalid header";
                case READ_INVALID_SIGNATURE: return "Invalid signature";
                case READ_INVALID_BODY: return "Invalid body of image";
                case READ_ENOMEM: return "Out of memory";
                case READ_INVALID_BITS: return "Invalid bit count";
                case READ_UNSUPPORTED_HEADER: return "Unsupported type of image";
                case READ_UNEXPECTED_ERROR: return "Unexpected error";
                default: return "Unknown READ_IMAGE status";
            }
        case STATUS_TYPE_WRITE_IMAGE:
            switch (status.value.write_image) {
                case WRITE_OK: return "Success";
                case WRITE_HEADER_ERROR: return "Can't write header";
                case WRITE_BODY_ERROR: return "Can't write body";
                case WRITE_UNEXPECTED_ERROR: return "Unexpected error";
                default: return "Unknown WRITE_IMAGE status";
            }
        case STATUS_TYPE_TRANSFORM_IMAGE:
            switch (status.value.transform_image) {
                case TRANSFORM_OK: return "Success";
                case TRANSFORM_ERROR: return "Can't create new image";
                default: return "Unknown TRANSFORM_IMAGE status";
            }
    }
    return "Unknown status. Error occurred";
}
