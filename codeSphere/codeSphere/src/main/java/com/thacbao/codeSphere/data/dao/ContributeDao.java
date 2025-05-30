package com.thacbao.codeSphere.data.dao;

import com.thacbao.codeSphere.dto.request.exercise.ContributeReq;
import com.thacbao.codeSphere.dto.response.exercise.ContributeDTO;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.SQLDataException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public List<ContributeDTO> getAllContributeActive(Boolean status, String order, String by, Integer page) throws SQLDataException {
        try {
            String orderBy = order != null && by != null ? " order by " + by + " " + order : " ";
            String sql = """
                    Select id, title, paper, input, output, note, created_by, created_at from contributes
                    where is_active = :status 
                    """ + orderBy + " limit 20 offset :page";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("status", status);
            query.setParameter("page", (page - 1) * 20);
            List<Object[]> result = query.getResultList();
            return result.stream().map(rs -> new ContributeDTO(Integer.parseInt(rs[0].toString()),
                    rs[1].toString(), rs[2].toString(), rs[3].toString(),
                    rs[4].toString(), rs[5].toString(), rs[6].toString(),
                    rs[7].toString())).collect(Collectors.toList());
        }
        catch (Exception ex) {
            throw new SQLDataException("Error saving Contribute: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public BigInteger getAllRecord(Boolean status) throws SQLDataException {
        try {
            String sql = "Select count(*) from contributes where is_active = :status";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("status", status);
            return (BigInteger) query.getSingleResult();
        }
        catch (Exception ex) {
            throw new SQLDataException("Error saving Contribute: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public List<ContributeDTO> getMyContribute(Boolean status, String dateOrder, String username) throws SQLDataException {
        try {
            String sql = "Select c.id, c.title, c.paper, c.input, c.output, c.note, c.created_by, c.created_at from contributes as c " +
                    "join users as u on c.user_id = u.id " +
                    "where c.is_active = :status and u.user_name = :username " +
                    (dateOrder != null ? "order by c.created_at " + dateOrder : "");
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("status", status);
            query.setParameter("username", username);
            List<Object[]> result = query.getResultList();
            return result.stream().map(rs -> new ContributeDTO(Integer.parseInt(rs[0].toString()),
                    rs[1].toString(), rs[2].toString(), rs[3].toString(),
                    rs[4].toString(), rs[5].toString(), rs[6].toString(),
                    rs[7].toString())).collect(Collectors.toList());
        }
        catch (Exception ex) {
            throw new SQLDataException("Error saving Contribute: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public ContributeDTO getContributeDetails(Integer id) throws SQLDataException {
        try {
            String sql = "Select c.id, c.title, c.paper, c.input, c.output, c.note, c.created_by, c.created_at from contributes as c " +
                    "where id = :id ";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("id", id);
            Object[] rs = (Object[]) query.getSingleResult();
            return new ContributeDTO(Integer.parseInt(rs[0].toString()),
                    rs[1].toString(), rs[2].toString(), rs[3].toString(),
                    rs[4].toString(), rs[5].toString(), rs[6].toString(), rs[7].toString());
        }
        catch (Exception ex) {
            throw new SQLDataException("Error saving Contribute: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public void updateContribute(String title, String paper, String input, String output, String note, Integer id) throws SQLDataException {
        try {
            String sql = "Update contributes set title = :title, paper = :paper, input = :input, " +
                    "output = :output, note = :note where id = :id";
            entityManager.createNativeQuery(sql)
                    .setParameter("title", title)
                    .setParameter("paper", paper)
                    .setParameter("input", input)
                    .setParameter("output", output)
                    .setParameter("note", note)
                    .setParameter("id", id)
                    .executeUpdate();
            ;
        }
        catch (Exception ex) {
            throw new SQLDataException("Error saving Contribute: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public void activateContribute(String id, Boolean status) throws SQLDataException {
        try {
            String sql = "UPDATE contributes SET is_active = :status WHERE id = :id";
            entityManager.createNativeQuery(sql)
                    .setParameter("status", status)
                    .setParameter("id", id)
                    .executeUpdate();
        }
        catch (Exception ex) {
            throw new SQLDataException("Error saving Contribute: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public void deleteContribute(Integer id) throws SQLDataException {
        try {
            String sql = "Delete from contributes where id = :id";
            entityManager.createNativeQuery(sql)
                    .setParameter("id", id)
                    .executeUpdate();
        }
        catch (Exception ex) {
            throw new SQLDataException("Error saving Contribute: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public long countContribute() throws SQLDataException {
        try {
            String sql = "Select count(*) from contributes";
            Query query = entityManager.createNativeQuery(sql);
            BigInteger count = (BigInteger) query.getSingleResult();
            return count.longValue();
        }
        catch (Exception ex) {
            throw new SQLDataException("Error saving Contribute: " + ex.getMessage(), ex);
        }
    }
}
