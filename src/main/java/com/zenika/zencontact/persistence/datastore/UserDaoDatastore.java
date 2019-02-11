package com.zenika.zencontact.persistence.datastore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.*;
import com.zenika.zencontact.domain.User;
import com.zenika.zencontact.persistence.UserDao;

public class UserDaoDatastore implements UserDao {

    private static UserDaoDatastore INSTANCE =
        new UserDaoDatastore();

    public static UserDaoDatastore getInstance() {
        return INSTANCE;
    }

    public DatastoreService datastore = 
        DatastoreServiceFactory.getDatastoreService();

    public long save(User contact) {
        // je créé l'entité
        Entity e = new Entity("User");
        if (contact.id != null) {
            // en cas d'édition
            Key k = KeyFactory.createKey("User", contact.id);
            try {
                e = datastore.get(k);
            } catch(EntityNotFoundException e1) {}
        }
        // on met les valeurs
        e.setProperty("firstName", contact.firstName);
        e.setProperty("lastName", contact.lastName);
        e.setProperty("email", contact.email);
        e.setProperty("notes", contact.notes);

        //sauvegarder
        Key key = datastore.put(e);
        return key.getId();
    }

	public void delete(Long id) {
        Key k = KeyFactory.createKey("User", id);
        datastore.delete(k);
    }

	public User get(Long id) {
        Entity e = null;
        try {
            e = datastore.get(KeyFactory.createKey("User", id));
        }
        catch(EntityNotFoundException exc) {}
        return User.create()
            .id(e.getKey().getId())
            .firstName((String) e.getProperty("firstName"))
            .lastName((String) e.getProperty("lastName"))
            .email((String) e.getProperty("email"))
            .notes((String) e.getProperty("notes"));
    }
	public List<User> getAll() {
        List<User> contacts = new ArrayList<>();
        Query q = new Query("User")
            .addProjection(new PropertyProjection("firstName", String.class))
            .addProjection(new PropertyProjection("lastName", String.class))
            .addProjection(new PropertyProjection("email", String.class))
            .addProjection(new PropertyProjection("notes", String.class));

        PreparedQuery pq = datastore.prepare(q);
        for (Entity e : pq.asIterable()) {
            contacts.add(User.create()
            .id(e.getKey().getId())
            .firstName((String) e.getProperty("firstName"))
            .lastName((String) e.getProperty("lastName"))
            .email((String) e.getProperty("email"))
            .notes((String) e.getProperty("notes")));
        }
        return contacts;
    }

}