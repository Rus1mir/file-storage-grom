package com.dao;

import com.model.Storage;
import javassist.NotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class StorageDAO {

    private SessionFactory sessionFactory;

    @Autowired
    public StorageDAO(SessionFactory sessionFactory) {

        this.sessionFactory = sessionFactory;
    }

    public Storage save(Storage storage) {

        Session session = sessionFactory.getCurrentSession();
        session.persist(storage);
        return storage;
    }

    public Storage findById(long id) throws Exception {

        Session session = sessionFactory.getCurrentSession();
        Storage storage = session.get(Storage.class, id);

        if (storage == null)
            throw new NotFoundException("Storage id: " + id + " was not found");
        return storage;
    }

    public Storage update(Storage storage) {

        Session session = sessionFactory.getCurrentSession();
        session.update(storage);
        return storage;
    }

    public void delete(long id) {

        Session session = sessionFactory.getCurrentSession();
        session.delete(session.load(Storage.class, id));
    }
}