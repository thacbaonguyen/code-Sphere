package com.thacbao.codeSphere.dao;

import com.thacbao.codeSphere.dto.request.ExerciseRequest;
import com.thacbao.codeSphere.dto.request.ExerciseUpdateRequest;
import com.thacbao.codeSphere.dto.response.ExerciseDTO;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.SQLDataException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExerciseDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<ExerciseDTO> filterExerciseBySubject(String subject) throws SQLDataException {
        try {
            String sql = "SELECT e.code, e.title, e.description, e.level, s.name, e.topic, e.time_limit, e.memory_limit" +
                    " FROM exercises as e " +
                    "join subjects as s on e.subject_id = s.id " +
                    "where s.name = :subject";
            List<Object[]> result = entityManager.createNativeQuery(sql)
                    .setParameter("subject", subject)
                    .getResultList();
            return result.stream().map(rs -> new ExerciseDTO(rs[0].toString(), rs[1].toString(),
                    rs[2].toString(), Integer.parseInt(rs[3].toString()),
                    rs[4].toString(), rs[5].toString(),
                    Integer.parseInt(rs[6].toString()), Integer.parseInt(rs[7].toString())))
                    .collect(Collectors.toList());
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }

    @Transactional
    public ExerciseDTO viewExerciseDetails(String code) throws SQLDataException {
        try {
            String sql = "SELECT e.code, e.title, e.paper, e.input, e.output, e.note, e.created_by, e.created_at, s.name, " +
                    "e.description, e.level, e.time_limit, e.memory_limit, e.topic FROM exercises as e " +
                    "join subjects as s on e.subject_id = s.id " +
                    "where e.code = :code";
            Object[] result = (Object[]) entityManager.createNativeQuery(sql).setParameter("code", code).getSingleResult();
            return new ExerciseDTO(result[0].toString(), result[1].toString(), result[2].toString(), result[3].toString(),
                    result[4].toString(), result[5].toString(), result[6].toString(), result[7].toString(), result[8].toString(),
                    result[9].toString(), Integer.parseInt(result[10].toString()),
                    Integer.parseInt(result[11].toString()), Integer.parseInt(result[12].toString()), result[13].toString());
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }

    @Transactional
    public List<ExerciseDTO> filterExerciseBySubjectAndOrder(String subject, String order, String by) throws SQLDataException {
        try {
            String sql = "SELECT e.code, e.title, e.description, e.level, s.name, e.topic, e.time_limit, e.memory_limit " +
                    "FROM exercises as e " +
                    "join subjects as s on e.subject_id = s.id " +
                    "where s.name = :subject ";
            if (by != null && order != null) {
                sql += "ORDER BY " + by + " " + order;
            }
            List<Object[]> result = entityManager.createNativeQuery(sql)
                    .setParameter("subject", subject)
                    .getResultList();
            return result.stream().map(rs -> new ExerciseDTO(rs[0].toString(), rs[1].toString(),
                    rs[2].toString(), Integer.parseInt(rs[3].toString()),
                    rs[4].toString(), rs[5].toString(),
                    Integer.parseInt(rs[6].toString()), Integer.parseInt(rs[7].toString())))
                    .collect(Collectors.toList());
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }

    @Transactional
    public void activateExercise(String code, Boolean isActive) throws SQLDataException {
        try {
            String sql = "UPDATE exercises SET is_active = :isActive WHERE code = :code";
            entityManager.createNativeQuery(sql)
                    .setParameter("isActive", isActive)
                    .setParameter("code", code)
                    .executeUpdate();
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }

    @Transactional
    public void updateExercise(ExerciseUpdateRequest request) throws SQLDataException {
        try {
            String sql = "UPDATE exercises SET code = :code, title = :title, paper = :paper, input = :input, output = :output, " +
                    "note = :note, description = :description, level = :level, time_limit = :timeLimit, memory_limit = :memoryLimit " +
                    "where code = :code";
            entityManager.createNativeQuery(sql)
                    .setParameter("code", request.getCode())
                    .setParameter("title", request.getTitle())
                    .setParameter("paper", request.getPaper())
                    .setParameter("input", request.getInput())
                    .setParameter("output", request.getOutput())
                    .setParameter("note", request.getNote())
                    .setParameter("description", request.getDescription())
                    .setParameter("level", request.getLevel())
                    .setParameter("timeLimit", request.getTimeLimit())
                    .setParameter("memoryLimit", request.getMemoryLimit())
                    .executeUpdate();
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }

    @Transactional
    public void deleteExercise(String code) throws SQLDataException {
        try {
            String sql = "DELETE FROM exercises WHERE code = :code";
            entityManager.createNativeQuery(sql).setParameter("code", code).executeUpdate();
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }
}
