package com.dao;

import com.exception.BadRequestException;
import com.model.Storage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

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

        if (storage == null)
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

    public long getMaxSize(long id) throws Exception {

        Session session = sessionFactory.getCurrentSession();
        try {
            Query<Long> query = session.createQuery(
                    "select storage.storageSize " +
                            "from Storage storage " +
                            "where storage.id = :id",
                    Long.class);

            query.setParameter("id", id);
            return query.getSingleResult();

        } catch (NoResultException e) {
            throw new BadRequestException("Storage id: " + id + " was not found");
        }
    }

    public long getOccupiedSize(long id) {

        Session session = sessionFactory.getCurrentSession();

        Query<Long> query = session.createQuery("" +
                        "select sum(f.size) " +
                        "from File f " +
                        "where f.storage.id = :id",
                Long.class);

        query.setParameter("id", id);
        Long size = query.getSingleResult();
        return size == null? 0L : size;
    }
}