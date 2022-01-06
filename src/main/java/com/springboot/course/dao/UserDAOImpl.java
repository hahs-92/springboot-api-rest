package com.springboot.course.dao;

import com.springboot.course.model.UserModel;
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
        userUpdate.setPassword(user.getPassword());

        return entityManager.merge(userUpdate);
    }
}
