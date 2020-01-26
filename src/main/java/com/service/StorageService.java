package com.service;

import com.dao.StorageDAO;
import com.model.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

    private StorageDAO storageDAO;

    @Autowired
    public StorageService(StorageDAO storageDAO) {

        this.storageDAO = storageDAO;
    }

    public Storage save(Storage storage) {

        return storageDAO.save(storage);
    }

    public Storage findById(long id) throws Exception{

        return storageDAO.findById(id);
    }

    public Storage update(Storage storage) {

        return storageDAO.update(storage);
    }

    public void delete(long id) {

        storageDAO.delete(id);
    }
}
