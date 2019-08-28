package controller;

import model.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import service.StorageService;

public class StorageController {

    private StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    public Storage save(Storage storage) throws Exception {

        return storageService.save(storage);
    }

    public Storage findById(long id) throws Exception {

        return storageService.findById(id);
    }

    public Storage update(Storage storage) throws Exception {

        return storageService.update(storage);
    }

    public void delete(long id) throws Exception {

        storageService.delete(id);
    }
}
