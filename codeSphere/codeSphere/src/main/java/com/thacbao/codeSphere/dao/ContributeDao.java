package com.thacbao.codeSphere.dao;

import com.thacbao.codeSphere.dto.request.ContributeReq;
import com.thacbao.codeSphere.entity.Contribute;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.SQLDataException;
import java.time.LocalDate;

@Service
@Transactional
public class ContributeDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(ContributeReq request, String username, Integer userId) throws SQLDataException {
        try {
            String sql = "INSERT INTO contributes (title, paper, input, output, note, is_active, created_by, created_at, user_id) " +
                    "VALUES (:title, :paper, :input, :output, :note, :isActive, :createdBy, :createdAt, :userId)";
            entityManager.createNativeQuery(sql)
                    .setParameter("title", request.getTitle())
                    .setParameter("paper", request.getPaper())
                    .setParameter("input", request.getInput())
                    .setParameter("output", request.getOutput())
                    .setParameter("note", request.getNote())
                    .setParameter("isActive", false)
                    .setParameter("createdBy", username)
                    .setParameter("createdAt", LocalDate.now())
                    .setParameter("userId", userId)
                    .executeUpdate();
        }
        catch (Exception ex) {
            throw new SQLDataException("Error saving Contribute: " + ex.getMessage(), ex);
        }
    }
}
