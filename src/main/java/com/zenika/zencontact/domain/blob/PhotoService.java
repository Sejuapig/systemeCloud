package com.zenika.zencontact.domain.blob;

import com.google.appengine.api.blobstore.*;
import com.zenika.zencontact.domain.User;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.zenika.zencontact.persistence.objectify.UserDaoObjectify;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class PhotoService {

    private static PhotoService INSTANCE = new PhotoService();

    public static PhotoService getInstance() {
        return INSTANCE;
    }

    private BlobstoreService blobstoreService =
        BlobstoreServiceFactory.getBlobstoreService();

    public void prepareUploadURL(User contact) {
        String uploadURL = blobstoreService
            .createUploadUrl("/api/v0/photo/" + contact.id);
            contact.uploadURL(uploadURL);
    }

    public void prepareDownloadURL(User contact) {
        BlobKey photoKey = contact.photoKey;
        if (photoKey != null) {
            String url = "/api/v0/photo/" + contact.id + "/"
                    + photoKey.getKeyString();
            contact.downloadURL(url);
        }
    }

    public void updatePhoto(Long id, HttpServletRequest req) {
        Map<String, List<BlobKey>> uploads = blobstoreService.getUploads(req);
        if (!uploads.keySet().isEmpty()) {
            // delete old photo from BlobStore to save disk space
            // update photo BlobKey in Contact entity
            Iterator<String> names = uploads.keySet().iterator();
            String name = names.next();
            List<BlobKey> keys = uploads.get(name);
            User contact = UserDaoObjectify.getInstance().get(id)
                    .photoKey(keys.get(0));
            UserDaoObjectify.getInstance().save(contact);
        }
    }

    public void serve(BlobKey blobKey, HttpServletResponse resp)
            throws IOException {
        BlobInfoFactory blobInfoFactory = new BlobInfoFactory(
                DatastoreServiceFactory.getDatastoreService());
        BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
        resp.setHeader("Content-Disposition", "attachment; filename="
                + blobInfo.getFilename());
        blobstoreService.serve(blobKey, resp);
    }
}
