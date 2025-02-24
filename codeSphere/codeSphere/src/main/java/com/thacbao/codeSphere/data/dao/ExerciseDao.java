package com.thacbao.codeSphere.data.dao;

import com.thacbao.codeSphere.dto.request.exercise.ExerciseUdReq;
import com.thacbao.codeSphere.dto.response.exercise.ExerciseDTO;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.SQLDataException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExerciseDao {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String FIND_ALL = "SELECT e.code, e.title, e.description, e.level, s.name, e.topic, e.time_limit, e.memory_limit FROM exercises e ";


    @Transactional
    public ExerciseDTO viewExerciseDetails(String code) throws SQLDataException {
        try {
            String sql = """
                    SELECT e.id, e.code, e.title, e.paper, e.input, e.output, e.note, e.created_by, e.created_at, s.name, 
                    e.description, e.level, e.time_limit, e.memory_limit, e.topic FROM exercises as e 
                    join subjects as s on e.subject_id = s.id 
                    where e.code = :code
                    """;
            Object[] result = (Object[]) entityManager.createNativeQuery(sql).setParameter("code", code).getSingleResult();
            return new ExerciseDTO(Integer.parseInt(result[0].toString()) ,result[1].toString(), result[2].toString(), result[3].toString(), result[4].toString(),
                    result[5].toString(), (String) result[6], result[7].toString(), result[8].toString(), result[9].toString(),
                    result[10].toString(), Integer.parseInt(result[11].toString()),
                    Integer.parseInt(result[12].toString()), Integer.parseInt(result[13].toString()), result[14].toString());
        }
        catch (Exception ex){
            throw new SQLDataException(ex.getMessage());
        }
    }

    @Transactional
    public List<ExerciseDTO> filterExerciseBySubjectAndParam(String subject, String order, String by, String search, Integer page)
            throws SQLDataException {
        try {
            String sql = FIND_ALL + "join subjects as s on e.subject_id = s.id " +
                    "where s.name = :subject and e.is_active = true " +
                    (search != null ? "and (lower(e.title) like concat('%', :search, '%') " +
                            "or lower(e.code) like concat('%', :search, '%')) " : "") +
                    (order != null && by != null ? " order by " + by + " " + order + " " : "") +
                    "limit 50 offset :start"; //tham so co dinh se la 50 ban ghi tren 1 trang
            Query query = entityManager.createNativeQuery(sql);
            if(search != null){
                query.setParameter("search", search);
            }
            if (order != null && by != null) {
                //duong dan bi thay doi khong hop le
                if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")){
                    throw new NotFoundException("Cannot found this url");
                }
//                if(!by.equalsIgnoreCase("code") && !by.equalsIgnoreCase("title")
//                        && !by.equalsIgnoreCase("level")){
//                    throw new NotFoundException("Cannot found this url");
//                }
            }
            query.setParameter("subject", subject);
            query.setParameter("start", (page - 1) * 50);
            List<Object[]> result = query.getResultList();
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
    public BigInteger getTotalRecord(String subject, String order, String by, String search)
            throws SQLDataException {
        try {
            String sql = "Select count(*) from exercises as e " + "join subjects as s on e.subject_id = s.id " +
                    "where s.name = :subject and e.is_active = true " +
                    (search != null ? "and (lower(e.title) like concat('%', :search, '%') " +
                            "or lower(e.code) like concat('%', :search, '%')) " : "") +
                    (order != null && by != null ? " order by " + by + " " + order + " " : "");
            Query query = entityManager.createNativeQuery(sql);
            if(search != null){
                query.setParameter("search", search);
            }
            if (order != null && by != null) {
                //duong dan bi thay doi khong hop le
                if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")){
                    throw new NotFoundException("Cannot found this url");
                }
            }
            query.setParameter("subject", subject);
            return (BigInteger) query.getSingleResult();
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
    public void updateExercise(ExerciseUdReq request) throws SQLDataException {
        try {
            String sql = """
                    UPDATE exercises SET code = :code, title = :title, paper = :paper, input = :input, output = :output, 
                    note = :note, description = :description, level = :level, time_limit = :timeLimit, memory_limit = :memoryLimit, topic =:topic 
                    where id = :id
                    """;
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
                    .setParameter("topic", request.getTopic())
                    .setParameter("id", request.getId())
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
