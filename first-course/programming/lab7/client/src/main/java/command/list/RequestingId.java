package command.list;

import collection.CollectionService;

/**
 * Requests id
 */
public interface RequestingId {
    public boolean validateId(CollectionService collectionService);
}
