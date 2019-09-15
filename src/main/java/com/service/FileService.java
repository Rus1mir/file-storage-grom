package com.service;

import com.dao.FileDAO;
import com.dao.StorageDAO;
import com.exception.BadRequestException;
import com.model.File;
import com.model.Storage;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FileService {

    private FileDAO fileDAO;
    private StorageDAO storageDAO;

    @Autowired
    public FileService(FileDAO fileDAO, StorageDAO storageDAO) {

        this.fileDAO = fileDAO;
        this.storageDAO = storageDAO;
    }

    @Transactional
    public File save(File file) throws Exception {

        if (file.getName().length() > 10)
            throw new BadRequestException("File name id: " + file.getId() + " is too large, try with name < 10 symbols");
        return fileDAO.saveEntity(file);
    }

    @Transactional
    public File findById(long id) {

        return fileDAO.findById(id);
    }

    @Transactional
    public File update(File file) {

        return fileDAO.update(file);
    }

    @Transactional
    public void delete(long id) {

        fileDAO.delete(id);
    }

    @Transactional
    public File put(long storageId, long fileId) throws Exception {

       File file = fileDAO.findById(fileId);
       Storage storage = storageDAO.findById(storageId);

       return put(storage, file);
    }

    @Transactional
    public void delete(long storageId, long fileId) throws BadRequestException {

        File file = fileDAO.findById(fileId);
        Storage storage = storageDAO.findById(storageId);

        if (file.getStorage() == null || file.getStorage().getId() != storage.getId())
            throw new BadRequestException("File id: " + file.getId() + " was not found in storage id: " + storage.getId());

        file.setStorage(null);
        fileDAO.update(file);
    }

    @Transactional
    public void transferAll(long storageFromId, long storageToId) throws Exception {

        Storage storageFrom = storageDAO.findById(storageFromId);
        Storage storageTo = storageDAO.findById(storageToId);

        List<File> filesFrom = fileDAO.getFilesByStorageId(storageFrom.getId());

        for (File file : filesFrom) {
            file.setStorage(storageTo);
        }

        validatePut(storageTo, filesFrom);
        fileDAO.update(filesFrom);
    }

    @Transactional
    public void transferFile(long storageFromId, long storageToId, long id) throws Exception {

        Storage storageFrom = storageDAO.findById(storageFromId);
        Storage storageTo = storageDAO.findById(storageToId);
        File file = fileDAO.findById(id);

        if (file.getStorage() == null || file.getStorage().getId() != storageFrom.getId())
            throw new BadRequestException("File id: " + id + " was not found in storage id: " + storageFrom.getId());

        file.setStorage(null);
        put(storageTo, file);
    }

    private File put (Storage storage, File file) throws Exception {

        List<File> files = new ArrayList<>();

        files.add(file);

        validatePut(storage, files);

        file.setStorage(storage);
        fileDAO.update(file);
        return file;
    }

    private void validatePut(Storage storage, List<File> files) throws Exception {

        long spaceNeeded = 0;

        for (File file : files) {

            if (file.getStorage() != null && file.getStorage().getId() != storage.getId())
                throw new BadRequestException("File id: " + file.getId() + " is already putted in other storage");

            if (!Arrays.asList(storage.getFormatsSupported()).contains(file.getFormat()))
                throw new BadRequestException("File id: " + file.getId() + " type no support for storage id:" +
                        storage.getId());

            spaceNeeded += file.getSize();
        }

        List<File> storageFiles = fileDAO.getFilesByStorageId(storage.getId());
        long spaceOccupied = 0;

        for (File f : storageFiles) {
            spaceOccupied += f.getSize();
        }

        if (storage.getStorageSize() - spaceOccupied < spaceNeeded)
            throw new BadRequestException("No enough free space for put files");
    }
}
