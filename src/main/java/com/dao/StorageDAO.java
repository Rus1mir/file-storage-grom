package com.dao;

import com.model.Storage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;

@Repository
public class StorageDAO {
    private SessionFactory sessionFactory;

    @Autowired
    public StorageDAO(SessionFactory sessionFactory) {

        this.sessionFactory = sessionFactory;
    }

    public Storage save(Storage storage) {

        sessionFactory.getCurrentSession().persist(storage);
        return storage;
    }

    public Storage findById(long id) {

        Storage storage = sessionFactory.getCurrentSession().get(Storage.class, id);

        if(storage == null)
            throw new EntityNotFoundException("Storage id: " + id + " was not found");
        return storage;
    }

    public Storage update(Storage storage) {

        sessionFactory.getCurrentSession().update(storage);
        return storage;
    }

    public void delete(long id) {

        Session session = sessionFactory.getCurrentSession();
        session.delete(session.load(Storage.class, id));
    }
}
