package com.dao;

import com.model.File;
import javassist.NotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;


@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class FileDAO {

    private SessionFactory sessionFactory;

    private final String TRANSFER_ALL_REQ = "UPDATE File f SET f.storage.id = :S_TO_ID WHERE f.storage.id = :S_FROM_ID";

    private final String GET_FILES_BY_STORAGE = "SELECT f FROM File f WHERE f.storage.id = : S_ID";

    @Autowired
    public FileDAO(SessionFactory sessionFactory) {

        this.sessionFactory = sessionFactory;
    }

    public File save(File file) {

        Session session = sessionFactory.getCurrentSession();
        session.persist(file);
        return file;
    }

    public File findById(long id) throws NotFoundException {

        Session session = sessionFactory.getCurrentSession();

        File file = session.find(File.class, id,
                Collections.singletonMap("javax.persistence.fetchgraph",
                        session.getEntityGraph("file.storage")));

        if (file == null)
            throw new NotFoundException("File with id: " + id + " was not found");

        return file;
    }

    public File update(File file) {

        Session session = sessionFactory.getCurrentSession();
        session.update(file);
        return file;
    }

    public void delete(long id) {

        Session session = sessionFactory.getCurrentSession();
        session.delete(session.load(File.class, id));
    }

    public int transferAll(long storageFromId, long storageToId) {

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(TRANSFER_ALL_REQ);

        query.setParameter("S_TO_ID", storageToId);
        query.setParameter("S_FROM_ID", storageFromId);

        return query.executeUpdate();
    }

    public List<File> getFilesByStorage(Long storageId) {

        Session session = sessionFactory.getCurrentSession();
        Query<File> query = session.createQuery(GET_FILES_BY_STORAGE, File.class);

        query.setParameter("S_ID", storageId);
        return query.getResultList();
    }
}
