package com.zenika.zencontact.resource;

import com.zenika.zencontact.domain.User;
import com.zenika.zencontact.persistence.UserRepository;
import com.zenika.zencontact.persistence.objectify.UserDaoObjectify;
import com.google.gson.Gson;
import com.google.appengine.api.memcache.*;
import java.io.IOException;
import java.lang.*;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(name = "UserResource", value = "/api/v0/users")
public class UserResource extends HttpServlet {

  private MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
  private List<User> contacts = new ArrayList<User>();
  public static final String CONTACTS_CACHE_KEY = "CONTACTS_CACHE_KEY";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    contacts = (List<User>)cache.get(CONTACTS_CACHE_KEY);
    if(contacts == null){
      contacts = UserDaoObjectify.getInstance().getAll();
      cache.put(CONTACTS_CACHE_KEY, contacts, Expiration.byDeltaSeconds(240), MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
    }
    response.setContentType("application/json; charset=utf-8");
    response.getWriter().println(new Gson()
      .toJsonTree(contacts).getAsJsonArray());
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    User user = new Gson().fromJson(request.getReader(), User.class);
    user.id(UserDaoObjectify.getInstance().save(user));
    cache.delete(CONTACTS_CACHE_KEY);
    response.setContentType("application/json; charset=utf-8");
    response.setStatus(201);
    response.getWriter().println(new Gson().toJson(user));
  }
}
