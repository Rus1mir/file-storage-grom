package com.dao;

import com.model.Storage;
import javassist.NotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Repository
@Transactional()
public class StorageDAO {

    private SessionFactory sessionFactory;
    //Вынести тексты запросов в константы и перевести в uppercase
    private final String REPO_SIZE_REQ = "SELECT s.storageSize FROM Storage s WHERE s.id = :ID";

    private final String OCCUPIED_SIZE_REQ = "SELECT SUM(f.size) FROM File f WHERE f.storage.id = :ID";

    private final String FORMAT_SUPPORT_REQ = "SELECT s.formatsSupported FROM Storage s WHERE s.id = :ID";

    private final String CONTAINS_FILES_REQ = "SELECT f.id FROM File f WHERE f.storage.id = :S_ID";

    private final String CONTAINING_FORMATS_REQ = "SELECT f.format FROM File f WHERE f.storage.id = :S_ID";

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

    public long getMaxSize(long id) throws Exception {
        Session session = sessionFactory.getCurrentSession();

        try {
            Query<Long> query = session.createQuery(REPO_SIZE_REQ, Long.class);
            query.setParameter("ID", id);
            return query.getSingleResult();

        } catch (NoResultException e) {
            throw new NotFoundException("Storage id: " + id + " was not found");
        }
    }

    public long getOccupiedSize(long id) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        try {
            Query<Long> query = session.createQuery(OCCUPIED_SIZE_REQ, Long.class);

            query.setParameter("ID", id);
            Long size = query.getSingleResult();
            return size == null ? 0L : size;

        } catch (NoResultException e) {
            throw new NotFoundException("Storage id: " + id + " was not found");
        }
    }

    public List<String> getFormatSupported(long id) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        try {
            Query<String[]> query = session.createQuery(FORMAT_SUPPORT_REQ, String[].class);

            query.setParameter("ID", id);
            //List<String> res = Arrays.asList(query.getSingleResult());
            return Arrays.asList(query.getSingleResult());

        } catch (NoResultException e) {
            throw new NotFoundException("Storage id: " + id + " was not found");
        }
    }

    public List<Long> getContainsFileIds(long id) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        try {
            Query<Long> query = session.createQuery(CONTAINS_FILES_REQ, Long.class);

            query.setParameter("S_ID", id);
            //List<Long> res = query.getResultList();
            return query.getResultList();

        } catch (NoResultException e) {
            throw new NotFoundException("Storage id: " + id + " was not found");
        }
    }

    public HashSet<String> getContainingFormats(long id) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        try {
            Query<String> query = session.createQuery(CONTAINING_FORMATS_REQ, String.class);

            query.setParameter("S_ID", id);
            //HashSet<String> res = new HashSet<>(query.getResultList());
            return new HashSet<>(query.getResultList());

        } catch (NoResultException e) {
            throw new NotFoundException("Storage id: " + id + " was not found");
        }
    }
}