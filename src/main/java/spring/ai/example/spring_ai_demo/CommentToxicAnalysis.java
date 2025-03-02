package spring.ai.example.spring_ai_demo;

import groovy.util.Eval;

public class CommentToxicAnalysis {
    private String originalComment;
    private boolean isOriginalToxic;
    private String replyFromChatGpt;
    private boolean isReplyToxic;
    private boolean isToxicUnsure;
    private Evaluation evaluationValue;
    private ErrorReason errorReason;

    public ErrorReason getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(ErrorReason errorReason) {
        this.errorReason = errorReason;
    }

    public Evaluation getEvaluationValue() {
        return evaluationValue;
    }

    public void setEvaluationValue(Evaluation evaluationValue) {
        this.evaluationValue = evaluationValue;
    }

    public String getOriginalComment() {
        return originalComment;
    }

    public void setOriginalComment(String originalComment) {
        this.originalComment = originalComment;
    }

    public boolean isOriginalToxic() {
        return isOriginalToxic;
    }

    public void setOriginalToxic(boolean originalToxic) {
        isOriginalToxic = originalToxic;
    }

    public String getReplyFromChatGpt() {
        return replyFromChatGpt;
    }

    public void setReplyFromChatGpt(String replyFromChatGpt) {
        this.replyFromChatGpt = replyFromChatGpt;
    }

    public boolean isReplyToxic() {
        return isReplyToxic;
    }

    public void setReplyToxic(boolean replyToxic) {
        isReplyToxic = replyToxic;
    }

    public boolean isToxicUnsure() {
        return isToxicUnsure;
    }

    public void setToxicUnsure(boolean toxicUnsure) {
        isToxicUnsure = toxicUnsure;
    }

    @Override
    public String toString() {
        return "CommentToxicAnalysis{" +
                "originalComment='" + originalComment + '\'' +
                ", isOriginalToxic=" + isOriginalToxic +
                ", replyFromChatGpt='" + replyFromChatGpt + '\'' +
                ", isReplyToxic=" + isReplyToxic +
                ", isToxicUnsure=" + isToxicUnsure +
                ", evaluationValue=" + evaluationValue +
                ", errorReason=" + errorReason +
                '}';
    }
}
