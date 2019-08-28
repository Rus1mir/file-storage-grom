package controller;

import model.File;
import model.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import service.FileService;

public class FileController {

    private FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    public File save(File file) throws Exception {

        return fileService.save(file);
    }

    public File findById(long id) throws Exception {

        return fileService.findById(id);
    }

    public File update(File file) throws Exception {

        return fileService.update(file);
    }

    public void delete(long id) throws Exception {

        fileService.delete(id);
    }

    public File put(Storage storage, File file) throws Exception {

        return fileService.put(storage, file);
    }

    public void delete(Storage storage, File file) throws Exception {

        fileService.delete(storage, file);
    }

    public void transferAll(Storage storageFrom, Storage storageTo) throws Exception {

        fileService.transferAll(storageFrom, storageTo);
    }

    public void transferFile(Storage storageFrom, Storage storageTo, long id) throws Exception {

        fileService.transferFile(storageFrom, storageTo, id);
    }
}
