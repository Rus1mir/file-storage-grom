package com.service;

import com.dao.FileDAO;
import com.dao.StorageDAO;
import com.exception.BadRequestException;
import com.model.File;
import com.model.Storage;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
//@Transactional(propagation = Propagation.REQUIRED)
public class FileService {

    private FileDAO fileDAO;
    private StorageDAO storageDAO;

    @Autowired
    public FileService(FileDAO fileDAO, StorageDAO storageDAO) {

        this.fileDAO = fileDAO;
        this.storageDAO = storageDAO;
    }

    public File save(File file) throws Exception {

        if (file.getName().length() > 10)
            throw new BadRequestException("File name id: " + file.getId() + " is too large, try with name < 10 symbols");
        return fileDAO.save(file);
    }

    public File findById(long id) throws NotFoundException {

        return fileDAO.findById(id);
    }

    public File update(File file) {

        return fileDAO.update(file);
    }

    public void delete(long id) {

        fileDAO.delete(id);
    }

    public File put(long storageId, long fileId) throws Exception {

        File file = fileDAO.findById(fileId);

        if (file.getStorage() != null)
            throw new BadRequestException("File id: " + fileId + " is already exists in storage " +
                    file.getStorage().getId() + " use transfer");

        Storage storageTo = storageDAO.findById(storageId);
        List<File> filesEx = fileDAO.getFilesByStorage(storageId);

        validatePut(file, filesEx, storageTo);

        file.setStorage(storageTo);
        return update(file);
    }

    public void delete(long storageId, long fileId) throws Exception {

        File file = findById(fileId);
        Storage storage = storageDAO.findById(storageId);

        if (!storage.equals(file.getStorage()))
            throw new NotFoundException("File id:" + fileId + " was not found in storage id: " + storageId);

        file.setStorage(null);
        update(file);
    }

    public void transferFile(long storageFromId, long storageToId, long id) throws Exception {

        File file = findById(id);
        Storage storageFrom = storageDAO.findById(storageFromId);

        if (!storageFrom.equals(file.getStorage()))
            throw new BadRequestException("File id: " + id + " was not found in storage id: " + storageFromId);

        Storage storageTo = storageDAO.findById(storageToId);
        List<File> filesEx = fileDAO.getFilesByStorage(storageToId);

        validatePut(file, filesEx, storageTo);

        file.setStorage(storageTo);
        update(file);
    }

    public int transferAll(long storageFromId, long storageToId) throws Exception {

        Storage storageTo = storageDAO.findById(storageToId);
        List<File> filesFrom = fileDAO.getFilesByStorage(storageFromId);
        List<File> filesTo = fileDAO.getFilesByStorage(storageToId);

        if (filesFrom.size() == 0)
            throw new NotFoundException("No files for transfer");

        validatePut(filesFrom, filesTo, storageTo);

        return fileDAO.transferAll(storageFromId, storageToId);
    }

    private void validatePut(File filePut, List<File> filesEx, Storage storage) throws Exception {

        validation(filePut, filesEx, storage, getFreeSpace(storage, filesEx));
    }

    private void validatePut(List<File> filesPut, List<File> filesEx, Storage storage) throws Exception {

        long freeSpace = getFreeSpace(storage, filesEx);

        for (File file : filesPut) {
            validation(file, filesEx, storage, freeSpace);
            freeSpace -= file.getSize();
        }
    }

    private long getFreeSpace(Storage storage, List<File> files) {

        long freeSpace = storage.getStorageSize();
        for (File file : files) freeSpace -= file.getSize();
        return freeSpace;
    }

    private void validation(File file, List<File> filesEx, Storage storage, long freeSpace) throws Exception {

        //Size check
        if (file.getSize() > freeSpace)
            throw new BadRequestException("Filed put file to storage id: " + storage.getId() +
                    " cause no enough free space");
        //Duplicate
        if (filesEx.contains(file))
            throw new BadRequestException("Filed put file to storage id: " + storage.getId() +
                    " cause storage already contains one or more equal files");
        //Format
        if (!Arrays.asList(storage.getFormatsSupported()).contains(file.getFormat()))
            throw new BadRequestException("Filed put file to storage id: " + storage.getId() +
                    " cause format " + file.getFormat() + " not supported");
    }
}
