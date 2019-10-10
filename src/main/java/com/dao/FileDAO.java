package com.dao;

import com.exception.BadRequestException;
import com.model.File;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.Collections;

@Repository
public class FileDAO {
    private SessionFactory sessionFactory;

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

    public File update(File storage) {

        sessionFactory.getCurrentSession().update(storage);
        return storage;
    }

    public void delete(long id) {

        Session session = sessionFactory.getCurrentSession();
        session.delete(session.load(File.class, id));
    }

    public int deleteFileFromStorage(long storageId, long fileId) {

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("update File f set f.storage = null " +
                "where f.id = :id " +
                "and f.storage.id = :sId");

        query.setParameter("id", fileId);
        query.setParameter("sId", storageId);
        return query.executeUpdate();
    }

    public int transferFile(long storageFromId, long storageToId, long id) {

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("update File f set f.storage = " +
                "(from Storage s where s.id = :sToId) " +
                "where f.id = :fId " +
                "and f.storage.id = :sFromId ");

        query.setParameter("sToId", storageToId);
        query.setParameter("sFromId", storageFromId);
        query.setParameter("fId", id);
        return query.executeUpdate();
    }

    public int transferAll(long storageFromId, long storageToId) {

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("update File f set f.storage = " +
                "(from Storage s where s.id = :sToId) where " +
                "f.storage.id = :sFromId");

        query.setParameter("sToId", storageToId);
        query.setParameter("sFromId", storageFromId);
        return query.executeUpdate();
    }

    public long getSize(long id) throws Exception {
        Session session = sessionFactory.getCurrentSession();

        try {
            Query<Long> query = session.createQuery(
                    "select f.size " +
                            "from File f " +
                            "where f.id = :id",
                    Long.class);

            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new BadRequestException("File id: " + id + " was not found");
        }
    }
}
