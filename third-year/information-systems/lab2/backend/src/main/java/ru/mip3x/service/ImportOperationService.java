package ru.mip3x.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;
import ru.mip3x.model.ImportOperation;
import ru.mip3x.model.ImportStatus;
import ru.mip3x.repository.ImportOperationRepository;

@Service
@RequiredArgsConstructor
public class ImportOperationService {
    private final ImportOperationRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportOperation start() {
        ImportOperation op = new ImportOperation();
        op.setStatus(ImportStatus.IN_PROGRESS);
        return repository.save(op);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSuccess(Long id, int addedCount) {
        updateOperation(id, op -> {
            op.setStatus(ImportStatus.SUCCESS);
            op.setAddedCount(addedCount);
            op.setErrorMessage(null);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long id, String errorMessage) {
        updateOperation(id, op -> {
            op.setStatus(ImportStatus.FAILED);
            op.setAddedCount(null);
            op.setErrorMessage(errorMessage);
        });
    }

    public Page<ImportOperation> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    private void updateOperation(Long id, java.util.function.Consumer<ImportOperation> updater) {
        Optional<ImportOperation> maybeOp = repository.findById(id);
        if (maybeOp.isEmpty()) return;
        ImportOperation op = maybeOp.get();
        updater.accept(op);
        repository.save(op);
    }
}
