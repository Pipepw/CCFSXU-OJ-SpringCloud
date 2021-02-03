package org.verwandlung.voj.web.model;


import java.io.Serializable;

/**
 * 试题难度的module
 * 对应数据库中的problem_difficulty数据表
 */
public class ProblemDifficulty  implements Serializable {

    /**
     * 难度的唯一标识符
     */
    private long problemDifficultyId;

    /**
     * 试题难度的别名
     */
    private String problemDifficultySlug;

    /**
     * 试题难度的名称
     */
    private String problemDifficultyName;

    /**
     * ProblemDifficulty的默认构造函数
     */
    public ProblemDifficulty(){ }

    /**
     * ProblemDifficulty的构造函数
     * @param problemDifficultySlug - 试题难度的别名
     * @param problemDifficultyName - 试题难度的名称
     */
    public ProblemDifficulty(String problemDifficultySlug,String problemDifficultyName){
        this.problemDifficultySlug = problemDifficultySlug;
        this.problemDifficultyName = problemDifficultyName;
    }

    /**
     * ProblemDifficulty的构造函数
     * @param problemDifficultyId - 试题难度的唯一标识符
     * @param problemDifficultySlug - 试题难度的别名
     * @param problemDifficultyName - 试题难度的名称
     */
    public ProblemDifficulty(long problemDifficultyId, String problemDifficultySlug,String problemDifficultyName){
        this(problemDifficultySlug,problemDifficultyName);
        this.problemDifficultyId = problemDifficultyId;
    }

    public long getProblemDifficultyId() {
        return problemDifficultyId;
    }

    public void setProblemDifficultyId(long problemDifficultyId) {
        this.problemDifficultyId = problemDifficultyId;
    }

    public String getProblemDifficultySlug() {
        return problemDifficultySlug;
    }

    public void setProblemDifficultySlug(String problemDifficultySlug) {
        this.problemDifficultySlug = problemDifficultySlug;
    }

    public String getProblemDifficultyName() {
        return problemDifficultyName;
    }

    public void setProblemDifficultyName(String problemDifficultyName) {
        this.problemDifficultyName = problemDifficultyName;
    }
}
