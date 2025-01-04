package com.thacbao.codeSphere.dao;

import com.thacbao.codeSphere.dto.response.ExerciseDTO;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.SQLDataException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExerciseDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<ExerciseDTO> getAllExercise() throws SQLDataException {
        try {
            String sql = "SELECT e.code, e.title, e.paper, e.input, e.output, e.note, e.created_by, e.created_at, exc.name FROM exercises e " +
                    "JOIN exercise_categories as exc on e.category_id = exc.id";
            List<Object[]> result = entityManager.createNativeQuery(sql).getResultList();
            return result.stream().map(rs -> new ExerciseDTO((String) rs[0], (String) rs[1], (String) rs[2], (String) rs[3],
                    (String) rs[4], (String) rs[5], rs[6].toString(),
                    rs[7].toString(), rs[8].toString())).collect(Collectors.toList());
        }
        catch (Exception ex) {
            throw new SQLDataException(ex.getMessage());
        }
    }
}
