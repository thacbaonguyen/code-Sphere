package com.thacbao.codeSphere.dao;

import com.thacbao.codeSphere.dto.response.UserDTO;
import com.thacbao.codeSphere.entity.User;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
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
}
