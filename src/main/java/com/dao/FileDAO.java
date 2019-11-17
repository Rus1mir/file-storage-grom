package com.dao;

import com.model.File;
import javassist.NotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Collections;

@Repository
@Transactional
public class FileDAO {

    private SessionFactory sessionFactory;
    private final String DELETE_FROM_STORAGE_REQ = "UPDATE File f set f.storage = NULL WHERE f.id = :ID " +
            "AND f.storage.id = :S_ID";

    private final String PUT_INTO_STORAGE_REQ = "UPDATE File f set f.storage = (FROM Storage s WHERE s.id = :S_ID) " +
            "WHERE f.id = :F_ID";

    private final String TRANSFER_FILE_REQ = "UPDATE File f set f.storage = (FROM Storage s WHERE s.id = :S_TO_ID) " +
            "WHERE f.id = :F_ID AND f.storage.id = :S_FROM_ID";

    private final String TRANSFER_ALL_REQ = "UPDATE File f SET f.storage = (FROM Storage s WHERE s.id = :S_TO_ID) " +
            "WHERE f.storage.id = :S_FROM_ID";

    private final String GET_SIZE_REQ = "SELECT f.size FROM File f WHERE f.id = :ID";

    private final String GET_FORMAT_REQ = "SELECT f.format FROM File f WHERE f.id = :ID";

    private final String GET_STORAGE_ID_REQ = "SELECT f.storage.id FROM File f WHERE f.id = :ID";

    @Autowired
    public FileDAO(SessionFactory sessionFactory) {

        this.sessionFactory = sessionFactory;
    }

    public File saveEntity(File file) {

        sessionFactory.getCurrentSession().persist(file);
        return file;
    }

    public File findById(long id) {

        Session session = sessionFactory.getCurrentSession();
        File file = session.find(File.class, id,
                Collections.singletonMap("javax.persistence.fetchgraph",
                        session.getEntityGraph("file.storage")));

        if (file == null)
            throw new EntityNotFoundException("File with id: " + id + " was not found");

        return file;
    }

    public File update(File file) {

        sessionFactory.getCurrentSession().update(file);
        return file;
    }

    public void delete(long id) {

        Session session = sessionFactory.getCurrentSession();
        session.delete(session.load(File.class, id));
    }

    public int deleteFileFromStorage(long storageId, long fileId) {

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(DELETE_FROM_STORAGE_REQ);

        query.setParameter("ID", fileId);
        query.setParameter("S_ID", storageId);
        return query.executeUpdate();
    }

    public void putIntoStorage(long storageId, long id) {

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(PUT_INTO_STORAGE_REQ);

        query.setParameter("S_ID", storageId);
        query.setParameter("F_ID", id);
        query.executeUpdate();
    }

    public void transferFile(Long storageFromId, long storageToId, long id) {

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(TRANSFER_FILE_REQ);

        query.setParameter("S_TO_ID", storageToId);
        query.setParameter("S_FROM_ID", storageFromId);
        query.setParameter("F_ID", id);
        query.executeUpdate();
    }

    public int transferAll(long storageFromId, long storageToId) {

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(TRANSFER_ALL_REQ);

        query.setParameter("S_TO_ID", storageToId);
        query.setParameter("S_FROM_ID", storageFromId);
        return query.executeUpdate();
    }

    public long getSize(long id) throws Exception {
        Session session = sessionFactory.getCurrentSession();

        try {
            Query<Long> query = session.createQuery(GET_SIZE_REQ, Long.class);

            query.setParameter("ID", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException("File id: " + id + " was not found");
        }
    }

    public String getFormat(long id) throws Exception {
        Session session = sessionFactory.getCurrentSession();

        try {
            Query<String> query = session.createQuery(GET_FORMAT_REQ, String.class);

            query.setParameter("ID", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException("File id: " + id + " was not found");
        }
    }

    public Long getStorageId(long id) throws Exception {
        Session session = sessionFactory.getCurrentSession();

        try {
            Query<Long> query = session.createQuery(GET_STORAGE_ID_REQ, Long.class);

            query.setParameter("ID", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException("File id: " + id + " was not found");
        }
    }
}
