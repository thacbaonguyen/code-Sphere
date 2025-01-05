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

    @Transactional
    public List<ExerciseDTO> filterExerciseBySubject(String subject) throws SQLDataException {
        try {
            String sql = "SELECT e.code, e.title, e.description, e.level, exc.name FROM exercises as e " +
                    "join exercise_categories as exc on e.category_id = exc.id " +
                    "where exc.name = :subject";
            List<Object[]> result = entityManager.createNativeQuery(sql)
                    .setParameter("subject", subject)
                    .getResultList();
            return result.stream().map(rs -> new ExerciseDTO(rs[0].toString(), rs[1].toString(),
                    rs[2].toString(), Integer.parseInt(rs[3].toString()),
                    rs[4].toString())).collect(Collectors.toList());
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }

    @Transactional
    public ExerciseDTO viewExerciseDetails(String code) throws SQLDataException {
        try {
            String sql = "SELECT e.code, e.title, e.paper, e.input, e.output, e.note, e.created_by, e.created_at, ex.name, " +
                    "e.description, e.level, e.time_limit, e.memory_limit FROM exercises as e " +
                    "join exercise_categories as ex on e.category_id = ex.id " +
                    "where e.code = :code";
            Object[] result = (Object[]) entityManager.createNativeQuery(sql).setParameter("code", code).getSingleResult();
            return new ExerciseDTO(result[0].toString(), result[1].toString(), result[2].toString(), result[3].toString(),
                    result[4].toString(), result[5].toString(), result[6].toString(), result[7].toString(), result[8].toString(),
                    result[9].toString(), Integer.parseInt(result[10].toString()),
                    Integer.parseInt(result[11].toString()), Integer.parseInt(result[12].toString()));
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }
}
