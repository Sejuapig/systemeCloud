package com.zenika.zencontact.resource;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.zenika.zencontact.domain.User;
import com.zenika.zencontact.fetch.PartnerBirthdayService;
import com.zenika.zencontact.persistence.objectify.UserDaoObjectify;

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
      String birthdate = PartnerBirthdayService.getInstance().findBirthdate(user.firstName, user.lastName);
      if (birthdate != null) {
          try {
              user.birthdate(new SimpleDateFormat("yyyy-MM-dd").parse(birthdate));
          } catch (ParseException e) {
              System.out.println(e);
          }
      }
    user.id(UserDaoObjectify.getInstance().save(user));
    cache.delete(CONTACTS_CACHE_KEY);
    response.setContentType("application/json; charset=utf-8");
    response.setStatus(201);
    response.getWriter().println(new Gson().toJson(user));
  }
}
