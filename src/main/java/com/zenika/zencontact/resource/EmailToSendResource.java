package com.zenika.zencontact.resource;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.zenika.zencontact.domain.Email;
import com.zenika.zencontact.domain.email.EmailService;

@WebServlet(name="EmailToSendResource", value="/api/v0/email")
public class EmailToSendResource {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Email email = new Gson().fromJson(request.getReader(), Email.class);
        EmailService.getInstance().sendEmail(email);
    }
}
