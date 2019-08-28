package service;

import dao.StorageDAO;
import model.Storage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

public class StorageService {

    private StorageDAO storageDAO;

    @Autowired
    public StorageService(StorageDAO storageDAO) {

        this.storageDAO = storageDAO;
    }

    @Transactional
    public Storage save(Storage storage) throws Exception {

        try {
            return storageDAO.save(storage);
        } catch (Exception e) {
            throw new Exception("Save storage, id: " + storage.getId() + " was filed", e);
        }
    }

    @Transactional
    public Storage findById(long id) throws Exception {

        try {
            return storageDAO.findById(id);
        } catch (Exception e) {
            throw new Exception("Update storage, id: " + id + " was filed", e);
        }
    }

    @Transactional
    public Storage update(Storage storage) throws Exception {

        try {
            return storageDAO.update(storage);
        } catch (Exception e) {
            throw new Exception("Update storage, id: " + storage.getId() + " was filed", e);
        }

    }

    @Transactional
    public void delete(long id) throws Exception {

        try {
            storageDAO.delete(id);
        } catch (Exception e) {
            throw new Exception("Delete storage, id: " + id + " was filed", e);
        }
    }
}
