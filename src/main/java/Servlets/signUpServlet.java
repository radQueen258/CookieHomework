package Servlets;

import Models.User;
import Repositories.AccountRepository;
import Repositories.AccountRepositoryJdbclmpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/signUp")
public class signUpServlet extends HttpServlet {

    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/javaCookie";

    AccountRepository accountRepository;

    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }

        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            accountRepository = new AccountRepositoryJdbclmpl(connection);
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        UUID userUUID = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("id".equals(cookie.getName())) {
                    userUUID = UUID.fromString(cookie.getValue());
                    break;
                }
            }
        }
        if (userUUID != null) {
            try {
                if (accountRepository.findUUID(userUUID)) {
                    response.sendRedirect("/home");
                    return;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        request.getRequestDispatcher("/html/signUpPage.html").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountUsername = request.getParameter("username");
        String accountEmail = request.getParameter("email");
        String accountPassword = request.getParameter("password");

        User user = User.builder()
                .userName(accountUsername)
                .userEmail(accountEmail)
                .userPassword(accountPassword)
                .build();

        try {
            accountRepository.save(user);
            response.sendRedirect("/save");
        }catch (SQLException e){
            response.sendRedirect("/signUp");
            throw new RuntimeException(e);
        }
    }
}
