package com.thacbao.codeSphere.dao;

import com.thacbao.codeSphere.dto.response.UserDTO;
import com.thacbao.codeSphere.entity.User;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.SQLDataException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    public User getUser(Integer id){
        User user = entityManager.find(User.class, id);
        return user;
    }

    @Transactional
    public List<UserDTO> getUserDetails(Integer id){
        try{
            String sql = "SELECT u.user_name AS userName, u.full_name AS fullName, " +
                    "r.name AS roleName, r.code AS roleCode " +
                    "FROM users AS u " +
                    "JOIN authorization AS a ON a.user_id = u.id " +
                    "JOIN roles AS r ON r.id = a.role_id " +
                    "WHERE u.id = :userId";
            List<Object[]> results = entityManager.createNativeQuery(sql)
                    .setParameter("userId", id)
                    .getResultList();

            // Ánh xạ kết quả từ Object[] sang UserDTO
            return results.stream()
                    .map(result -> new UserDTO(
                            (String) result[0], // userName
                            (String) result[1], // fullName
                            (String) result[2], // roleName
                            (String) result[3]  // roleCode
                    ))
                    .collect(Collectors.toList());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Transactional
    public UserDTO getProfile(String username) throws SQLDataException {
        try {
            String sql = "SELECT u.user_name, u.full_name, u.email, u.phone_number, u.dob, u.created_at, u.updated_at, " +
                    "GROUP_CONCAT(r.name) as roles_name from users as u " +
                    "JOIN authorization as a on a.user_id = u.id " +
                    "JOIN roles as r on a.role_id = r.id " +
                    "where u.user_name = :username " +
                    "group by u.id";
            Object[] results = (Object[]) entityManager.createNativeQuery(sql)
                    .setParameter("username", username)
                    .getSingleResult();
            return new UserDTO((String) results[0], (String) results[1], (String) results[2],
                    (String) results[3], results[4].toString(),results[5].toString(), results[6].toString(),
                    Arrays.asList(results[7].toString().split(",")));
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }

    @Transactional
    public void deleteUser(Integer id){
        try{
            String sql = "DELETE FROM users WHERE id = :userId";
            entityManager.createNativeQuery(sql)
                    .setParameter("userId", id)
                    .executeUpdate();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Transactional
    public Integer existUsername(String username){
        try{
            String sql = "SELECT u.id FROM users AS u WHERE u.username = :username";
            return (Integer) entityManager.createNativeQuery(sql)
                    .setParameter("username", username)
                    .getSingleResult();
        }
        catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Transactional
    public List<String> getRolesUser(Integer id){
        try{
            String sql = "SELECT r.name " +
                    "FROM roles AS r " +
                    "JOIN authorization AS a ON r.id = a.role_id " +
                    "JOIN users AS u ON u.id = a.user_id " +
                    "WHERE u.id = :userId";
            return entityManager.createNativeQuery(sql)
                    .setParameter("userId", id)
                    .getResultList();
        }
        catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
