package spring.ai.example.spring_ai_demo;

public class CommentToxicDataSetEntry {
    private String comment;
    private boolean isToxic;

    public CommentToxicDataSetEntry(String comment, boolean isToxic) {
        this.comment = comment;
        this.isToxic = isToxic;
    }

    public String getComment() {
        return comment;
    }

    public boolean isToxic() {
        return isToxic;
    }

    @Override
    public String toString() {
        return "CommentToxicDataSetEntry{" +
                "comment='" + comment + '\'' +
                ", isToxic=" + isToxic +
                '}';
    }
}
