package com.service;

import com.dao.FileDAO;
import com.dao.StorageDAO;
import com.exception.BadRequestException;
import com.model.File;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
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
        return fileDAO.saveEntity(file);
    }

    public File findById(long id) {

        return fileDAO.findById(id);
    }

    public File update(File file) {

        return fileDAO.update(file);
    }

    public void delete(long id) {

        fileDAO.delete(id);
    }

    public File put(long storageId, long fileId) throws Exception {

        Long fileStorageId = fileDAO.getStorageId(fileId);

        if (fileStorageId != null)
            throw new BadRequestException("File id: " + fileId + " is already exists in storage " + fileStorageId);

        validationPut(storageId, fileId);
        fileDAO.putIntoStorage(storageId, fileId);
        return fileDAO.findById(fileId);
    }

    public void delete(long storageId, long fileId) throws Exception {

        if (fileDAO.deleteFileFromStorage(storageId, fileId) == 0)
            throw new NotFoundException("File id:" + fileId + " was not found in storage id: " + storageId);
    }

    public void transferFile(long storageFromId, long storageToId, long id) throws Exception {

        Long fileStorageId = fileDAO.getStorageId(id);

        if ((fileStorageId == null) || (fileStorageId != storageFromId))
            throw new BadRequestException("File id: " + id + " was not found in storage id: " + storageFromId);

        validationPut(storageToId, id);

        fileDAO.transferFile(storageFromId, storageToId, id);
    }

    public int transferAll(long storageFromId, long storageToId) throws Exception {

        validationTransferAll(storageFromId, storageToId);

        return fileDAO.transferAll(storageFromId, storageToId);
    }

    private void validationPut(long storageId, long fileId) throws Exception {

        //Size check
        if (fileDAO.getSize(fileId) > storageDAO.getMaxSize(storageId) - storageDAO.getOccupiedSize(storageId))
            throw new BadRequestException("Filed put file id:" + fileId + " to storage id: " + storageId +
                    " cause no enough free space");

        //Format check
        if (!storageDAO.getFormatSupported(storageId).contains(fileDAO.getFormat(fileId)))
            throw new BadRequestException("Format of file id: " + fileId +
                    " is not support for Storage id: " + storageId);

        //Duplicate check
        if (storageDAO.getContainsFileIds(storageId).contains(fileId))
            throw new BadRequestException("Storage id: " + storageId + " already contains file with id: " + fileId);
    }

    private void validationTransferAll(long storageFromId, long storageToId) throws Exception {

        long occupiedSizeFrom = storageDAO.getOccupiedSize(storageFromId);

        //Empty check
        if (occupiedSizeFrom == 0)
            throw new NotFoundException("Were is not found any files in storage id: " + storageFromId);

        //Size check
        if (storageDAO.getMaxSize(storageToId) - storageDAO.getOccupiedSize(storageToId) < occupiedSizeFrom)
            throw new BadRequestException("Filed transfer files from storage id: " + storageFromId +
                    " to storage id: " + storageToId + " cause no enough free space");

        //Format check
        if (!storageDAO.getFormatSupported(storageToId).containsAll(storageDAO.getContainingFormats(storageFromId))) {
            throw new BadRequestException("Filed transfer files from storage id: " + storageFromId +
                    " to storage id: " + storageToId + " cause destination storage not support one or more file format");
        }

        //Duplicate check
        if (!Collections.disjoint(storageDAO.getContainsFileIds(storageFromId), storageDAO.getContainsFileIds(storageToId)))
            throw new BadRequestException("Filed transfer files from storage id: " + storageFromId +
                    " to storage id: " + storageToId + " cause destination storage contains one or more files with same id");
    }
}
