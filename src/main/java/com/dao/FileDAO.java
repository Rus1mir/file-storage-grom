package com.dao;

import com.model.File;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;

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

    public List<File> getFilesByStorageId(long id) {

        Session session = sessionFactory.getCurrentSession();
        Query<File> query = session.createQuery("from File f where f.storage.id = :id", File.class);
        query.setParameter("id", id);
        return query.getResultList();
    }

    public List<File> update(List<File> files) {

        Session session = sessionFactory.getCurrentSession();
        session.setJdbcBatchSize(12);
        for (File f : files) {
            session.update(f);
        }
        return files;
    }
}
