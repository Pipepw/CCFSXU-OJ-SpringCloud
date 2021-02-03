package org.verwandlung.voj.web.model;

import java.io.Serializable;

/**
 * 试题难度对应关系的module
 * 对应数据库中的problem_difficulty_relationships数据表
 */
public class ProblemDifficultyRelationship  implements Serializable {
    private long problemId;
    private long problemDifficultyId;
    /**
     * ProblemDifficultyRelationship的默认构造函数
     */
    ProblemDifficultyRelationship(){}

    /**
     * ProblemDifficultyRelationship的构造函数
     * @param problemId - 试题的唯一标识符
     * @param problemDifficultyId - 试题难度的唯一标识符
     */
    ProblemDifficultyRelationship(long problemId, long problemDifficultyId){
        this.problemId = problemId;
        this.problemDifficultyId = problemDifficultyId;
    }
    public long getProblemId() {
        return problemId;
    }

    public void setProblemId(long problemId) {
        this.problemId = problemId;
    }

    public long getProblemDifficultyId() {
        return problemDifficultyId;
    }

    public void setProblemDifficultyId(long problemDifficultyId) {
        this.problemDifficultyId = problemDifficultyId;
    }
}
