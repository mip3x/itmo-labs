package io.console.command.list;

import collection.CollectionManager;

/**
 * Requests id
 */
public interface RequestingId {
    public boolean validateId(CollectionManager collectionManager);
}
