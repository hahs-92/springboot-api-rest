package com.springboot.course.dao;

import com.springboot.course.model.UserModel;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserDAOImpl implements IUserDAO{
    @PersistenceContext
    EntityManager entityManager; //nos ayuda con la conexion con la DB

    @Override
    public List<UserModel> getAll() {
        String query = "FROM UserModel"; //nombre de la clase no de la TABLA DB
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Optional<UserModel> getUser(Long userId) {
        UserModel user = entityManager.find(UserModel.class, userId);
        return Optional.of(user);
    }

    @Override
    public void delete(Long userId) {
        UserModel user = entityManager.find(UserModel.class,userId);
        entityManager.remove(user);
    }

    @Override
    public UserModel save(UserModel user) {
        return entityManager.merge(user);
    }

    @Override
    public UserModel update(Long userId, UserModel user) {
        UserModel userUpdate = entityManager.find(UserModel.class,userId);
        userUpdate.setFirstName(user.getFirstName());
        userUpdate.setLastName(user.getLastName());
        userUpdate.setEmail(user.getEmail());
        userUpdate.setTelephone(user.getTelephone());

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String hash = argon2.hash(3,1024, 1  ,user.getPassword());

        userUpdate.setPassword(hash);

        return entityManager.merge(userUpdate);
    }

    @Override
    public UserModel getUserByCredentials(UserModel user) {
        String query = "FROM UserModel WHERE email = :email";
        List<UserModel> userFound =  entityManager.createQuery(query)
                .setParameter("email", user.getEmail())
                .getResultList();

        if(userFound.isEmpty())
            return null;

        String passwordHashed = userFound.get(0).getPassword();

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

        if(argon2.verify(passwordHashed, user.getPassword())) {
            return  userFound.get(0);
        }

        return  null;
    }
}
