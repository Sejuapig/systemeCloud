package com.zenika.zencontact.persistence.objectify;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.*;
import com.zenika.zencontact.domain.User;
import com.zenika.zencontact.persistence.UserDao;
import java.util.List;

public class UserDaoObjectify implements UserDao {

    private static UserDaoObjectify INSTANCE = new UserDaoObjectify();

    public static UserDaoObjectify getInstance() {
        ObjectifyService.factory().register(User.class);
        return INSTANCE;
    }

    public long save(User contact) {
        return ObjectifyService.ofy().save()
            .entity(contact).now().getId();
    }
    public void delete(Long id) {
        ObjectifyService.ofy().delete()
            .key(Key.create(User.class, id)).now();
    }
    public User get(Long id) {
        return ObjectifyService.ofy().load()
            .key(Key.create(User.class, id)).now();
    }
    public List<User> getAll() {
        return ObjectifyService.ofy().load()
            .type(User.class).list();
    }
}