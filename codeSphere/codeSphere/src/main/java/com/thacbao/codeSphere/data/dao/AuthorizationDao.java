package com.thacbao.codeSphere.data.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.SQLDataException;

@Service
@Transactional
@Slf4j
public class AuthorizationDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public boolean insertIntoAuthorization(Integer userId, Integer roleId) throws SQLDataException {
        try {
            entityManager.createNativeQuery("INSERT INTO authorization (user_id, role_id) VALUES (?, ?)")
                    .setParameter(1, userId)
                    .setParameter(2, roleId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
            throw new SQLDataException(e.getMessage());
        }
    }

    @Transactional
    public void deleteFromAuthorization(Integer userId, Integer roleId) throws SQLDataException {
        try {
            String sql = "DELETE FROM authorization WHERE user_id = ? AND role_id = ?";
            entityManager.createNativeQuery(sql).setParameter(1, userId).setParameter(2, roleId).executeUpdate();
        }
        catch (Exception e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
            throw new SQLDataException(e.getMessage());
        }
    }
}
