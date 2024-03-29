package it.polimi.db69.telco.telcoejb.services;

import it.polimi.db69.telco.telcoejb.entities.Alert;
import it.polimi.db69.telco.telcoejb.entities.LoginLog;
import it.polimi.db69.telco.telcoejb.entities.User;


import javax.ejb.Stateless;
import javax.persistence.*;
import javax.security.auth.login.CredentialException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Stateless
public class UserService {
    @PersistenceContext(unitName = "TELCOEJB")
    private EntityManager em;

    public UserService(){}

    public User checkCredentials(String username, String password){
        List<User> uList = null;
        uList = em.createNamedQuery("User.checkCredentials", User.class)
                .setParameter(1, username).setParameter(2, password).getResultList();
        if(uList.isEmpty()) return null;
        else return uList.get(0);
    }

    public User findUserById(int userId) {
        em.clear();
        return em.find(User.class, userId);
    }

    public User findUserByUsername(String username){
        return em.createNamedQuery("User.getFromUsername", User.class).setParameter("username", username).getSingleResult();
    }

    public User createUser(String email, String username, String password) throws CredentialException {
        if (em.createNamedQuery("User.getFromEmail", User.class)
                .setParameter("email", email).setMaxResults(1).getResultStream().findFirst().orElse(null) != null)
            throw new CredentialException("Email already in use by another account");

        else if (em.createNamedQuery("User.getFromUsername", User.class)
                .setParameter("username", username).setMaxResults(1).getResultStream().findFirst().orElse(null) != null)
            throw new CredentialException("Email already in use by another account");

        else {
            User user = new User(username, password, email);
            em.persist(user);

            return user;
        }
    }

    public void addLog(int userid){
        User user = findUserById(userid);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        LoginLog loginLog = new LoginLog(timestamp);
        user.addUserLogin(loginLog);

        em.persist(user);
    }

    public Collection<User> getAllUsers(){
        return em.createNamedQuery("User.getAllUsers", User.class).getResultList();
    }

    public Collection<User> getInsolventUsers(){
        return em.createNamedQuery("User.getInsolventUsers", User.class).getResultList();
    }
}

