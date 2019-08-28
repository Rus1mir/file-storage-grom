package dao;

import model.File;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

        //return sessionFactory.getCurrentSession().get(File.class, id);
        Session session = sessionFactory.getCurrentSession();
        Query<File> query = session.createQuery("from File f join fetch f.storage s where f.id = :id", File.class);
        query.setParameter("id", id);
        return query.getSingleResult();
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
