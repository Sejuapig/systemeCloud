package com.zenika.zencontact.resource.auth;

import java.io.IOException;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zenika.zencontact.domain.email.EmailService;

@WebServlet(name="EmailReceivedResource", value="/_ah/mail/*")
@ServletSecurity(@HttpConstraint(rolesAllowed = {"admin"}))
public class EmailReceivedResource extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        EmailService.getInstance().logEmail(request);
    }
}
