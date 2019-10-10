package com.service;

import com.dao.FileDAO;
import com.dao.StorageDAO;
import com.exception.BadRequestException;
import com.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

        long freeSize;
        freeSize = storageDAO.getMaxSize(storageId) - storageDAO.getOccupiedSize(storageId);

        File file = fileDAO.findById(fileId);

        if (freeSize < file.getSize())
            throw new BadRequestException("Free space in storage id: " + storageId +
                    " is not enough for file id: " + fileId);

        file.setStorage(storageDAO.findById(storageId));
        return file;
    }

    @Transactional
    public void delete(long storageId, long fileId) throws BadRequestException {

        if (fileDAO.deleteFileFromStorage(storageId, fileId) == 0)
            throw new BadRequestException("File id:" + fileId + " was not found in storage id: " + storageId);
    }

    @Transactional
    public void transferFile(long storageFromId, long storageToId, long id) throws Exception {

        storageDAO.findById(storageFromId);

        if (fileDAO.getSize(id) > storageDAO.getMaxSize(storageToId) - storageDAO.getOccupiedSize(storageToId))
            throw new BadRequestException("Filed transfer file id:" + id + " from storage id: " + storageFromId +
                    " to storage id: " + storageToId + " cause no enough free space");

        if (fileDAO.transferFile(storageFromId, storageToId, id) == 0)
            throw new BadRequestException("Filed transfer file id:" + id + " from storage id: " + storageFromId +
                    " to storage id: " + storageToId + " cause file not found in from side");
    }

    @Transactional
    public int transferAll(long storageFromId, long storageToId) throws Exception {

        storageDAO.findById(storageFromId);

        if (storageDAO.getMaxSize(storageToId) - storageDAO.getOccupiedSize(storageToId) < storageDAO.getOccupiedSize(storageFromId))
            throw new BadRequestException("Filed transfer files from storage id: " + storageFromId +
                    " to storage id: " + storageToId + " cause no enough free space");

        return fileDAO.transferAll(storageFromId, storageToId);
    }
}
