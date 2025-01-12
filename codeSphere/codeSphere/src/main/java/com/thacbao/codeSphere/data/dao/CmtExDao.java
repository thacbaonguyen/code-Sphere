package com.thacbao.codeSphere.data.dao;

import com.thacbao.codeSphere.dto.response.exercise.CmExHistoryDTO;
import com.thacbao.codeSphere.dto.response.exercise.CommentExDTO;
import com.thacbao.codeSphere.entity.reference.CommentExercise;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.SQLDataException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CmtExDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(CommentExercise commentExercise) {
        entityManager.persist(commentExercise);
    }

    @Transactional
    public List<CommentExDTO> getCommentEx(String code) throws SQLDataException {
        try{
            String sql = "Select c.id, c.content, c.author_name, c.created_at, c.updated_at from commentexercise as c " +
                    "join exercises as e on e.id = c.exercise_id " +
                    "where e.code = :code";
            List<Object[]> result = entityManager.createNativeQuery(sql)
                    .setParameter("code", code)
                    .getResultList();
            return result.stream().map(rs ->
                    new CommentExDTO(Integer.parseInt( rs[0].toString()), rs[1].toString(),
                            rs[2].toString(), rs[3].toString(), rs[4].toString())
                    ).collect(Collectors.toList());
        }
        catch (Exception e){
            throw new SQLDataException(e.getMessage());
        }
    }

    @Transactional
    public void updateCommentEx(String content, Integer id) throws SQLDataException {
        try{
            String sql = "Update commentexercise set content = :content where id = :id";
            entityManager.createNativeQuery(sql)
                    .setParameter("content", content)
                    .setParameter("id", id)
                    .executeUpdate();
        }
        catch (Exception e){
            throw new SQLDataException(e.getMessage());
        }
    }

    @Transactional
    public List<CmExHistoryDTO> getCmExHistory(Integer commentExerciseId) throws SQLDataException {
        try {
            String sql = "Select ch.content, ch.updated_at from cmthistory as ch " +
                    "join commentexercise as c on c.id = ch.comment_exercise_id " +
                    "where ch.comment_exercise_id = :commentExerciseId";
            List<Object[]> result = entityManager.createNativeQuery(sql)
                    .setParameter("commentExerciseId", commentExerciseId)
                    .getResultList();
            return result.stream().map(rs ->
                    new CmExHistoryDTO(rs[0].toString(), rs[1].toString())
                    ).collect(Collectors.toList());
        }
        catch (Exception e){
            throw new SQLDataException(e.getMessage());
        }
    }
}
