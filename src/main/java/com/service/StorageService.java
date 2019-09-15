package com.service;

import com.dao.StorageDAO;
import com.model.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class StorageService {

    private StorageDAO storageDAO;

    @Autowired
    public StorageService(StorageDAO storageDAO) {

        this.storageDAO = storageDAO;
    }

    @Transactional
    public Storage save(Storage storage) {

        return storageDAO.save(storage);
    }

    @Transactional
    public Storage findById(long id) {

        return storageDAO.findById(id);
    }

    @Transactional
    public Storage update(Storage storage) {

        return storageDAO.update(storage);
    }

    @Transactional
    public void delete(long id) {

        storageDAO.delete(id);
    }
}
