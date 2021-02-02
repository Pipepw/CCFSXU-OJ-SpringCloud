package org.verwandlung.voj.web.mapper;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.verwandlung.voj.web.model.ProblemDifficulty;
import org.verwandlung.voj.web.model.ProblemDifficultyRelationship;

import java.util.List;

/**
 * ProblemDifficulty Data Access Object.
 *
 * @author Pipepw
 */
@Mapper
@Repository //注明是Dao层操作
@CacheNamespace(implementation = org.mybatis.caches.ehcache.EhcacheCache.class)
public interface ProblemDifficultyMapper {

    /**
     * 获取所有的难度对应表
     * @return
     */
    String getProblemDifficultyRelationUsingProblemId(@Param("problemId") long problemId);

    /**
     * 根据问题的id，添加难度对应关系
     * @return
     */
    int createProblemDifficultyRelationship(@Param("problemId") long problemId, @Param("problemDifficultyId") long problemDifficultyId);

}
