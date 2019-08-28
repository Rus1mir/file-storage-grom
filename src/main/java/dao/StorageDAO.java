package dao;

import model.Storage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

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

        return (Storage) sessionFactory.getCurrentSession().get(Storage.class, id);
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
