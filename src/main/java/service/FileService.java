package service;

import dao.FileDAO;
import exception.BadRequestException;
import model.File;
import model.Storage;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileService {

    @Autowired
    FileDAO fileDAO;

    @Transactional
    public File save(File file) throws Exception {

        try {
            return fileDAO.saveEntity(file);
        } catch (HibernateException e) {
            throw new Exception("Save file, id: " + file.getId() + " was filed", e);
        }
    }

    @Transactional
    public File findById(long id) throws Exception {

        try {
            File file = fileDAO.findById(id);
            return file;
        } catch (HibernateException e) {
            throw new Exception("Get file, id: " + id + " was filed", e);
        }
    }

    @Transactional
    public File update(File file) throws Exception {

        try {
            return fileDAO.update(file);
        } catch (HibernateException e) {
            throw new Exception("Updating file, id: " + file.getId() + " was filed", e);
        }
    }

    @Transactional
    public void delete(long id) throws Exception {

        try {
            fileDAO.delete(id);
        } catch (HibernateException e) {
            throw new Exception("Deleting file, id: " + id + " was filed", e);
        }
    }

    @Transactional
    public File put(Storage storage, File file) throws Exception {

        List<File> files = new ArrayList<>();

        files.add(file);

        try {

            validatePut(storage, files);

            file.setStorage(storage);
            fileDAO.update(file);
            return file;

        } catch (Exception e) {
            throw new Exception("Put operation for file id: " + file.getId() + " into storage id: " +
                    storage.getId() + " was filed", e);
        }
    }

    @Transactional
    public void delete(Storage storage, File file) throws Exception {

        if (file.getStorage().getId() != storage.getId())
            return;

        try {

            file.setStorage(null);
            fileDAO.update(file);

        } catch (Exception e) {
            throw new Exception("Delete file id: " + file.getId() + " from storage id: " + storage.getId() +
                    " was filed", e);
        }
    }

    @Transactional
    public void transferAll(Storage storageFrom, Storage storageTo) throws Exception {

        try {

            List<File> filesFrom = fileDAO.getFilesByStorageId(storageFrom.getId());

            for (File file : filesFrom) {
                file.setStorage(storageTo);
            }

            validatePut(storageTo, filesFrom);

            fileDAO.update(filesFrom);

        } catch (Exception e) {
            throw new Exception("Transfer all files from storage id: " + storageFrom.getId() + " to storage id: " +
                    storageTo.getId() + " was filed", e);
        }
    }

    @Transactional
    public void transferFile(Storage storageFrom, Storage storageTo, long id) throws Exception {

        try {

            File file = fileDAO.findById(id);

            if (file.getStorage().getId() != storageFrom.getId())
                throw new BadRequestException("File id: " + id + " was not found in storage id: " + storageFrom.getId());

            file.setStorage(null);

            put(storageTo, file);
        } catch (Exception e) {
            throw new Exception("Transfer file id: " + id + " from storage id: " + storageFrom.getId()
                    + " to storage id: " + storageTo.getId() + " was filed", e);
        }
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
