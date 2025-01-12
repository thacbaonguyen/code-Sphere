package com.thacbao.codeSphere.data.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class AuthorizationDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public boolean insertIntoAuthorization(Integer userId, Integer roleId) {
        try {
            entityManager.createNativeQuery("INSERT INTO authorization (user_id, role_id) VALUES (?, ?)")
                    .setParameter(1, userId)
                    .setParameter(2, roleId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
