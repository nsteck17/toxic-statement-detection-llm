package spring.ai.example.spring_ai_demo;

public class AnalysisSummary {
    int totalTruePositives;
    int totalTrueNegatives;
    int totalFalsePositives;
    int totalFalseNegatives;

    int totalRecords;

    int totalOriginalToxic;
    int totalOriginalNotToxic;
    int totalReplyToxic;
    int totalReplyNotToxic;
    int totalReplyUnsure;
    int totalErrorReply;

    public int getTotalErrorReply() {
        return totalErrorReply;
    }

    public void setTotalErrorReply(int totalErrorReply) {
        this.totalErrorReply = totalErrorReply;
    }

    public double getPercision() {
        return (totalTruePositives * 1.0) / ((totalTruePositives * 1.0) + (totalFalsePositives * 1.0));
    }
    public double getRecall() {
        return (totalTruePositives * 01.0)  / ((totalTruePositives * 1.0) + (totalFalseNegatives * 1.0));
    }
    public double getF1Score() {
        System.out.println("getPercision() = " + getPercision());
        System.out.println("getRecall() = " + getRecall());
        return 2 * (getPercision() * getRecall()) / (getPercision() + getRecall());
    }

    public int getTotalTruePositives() {
        return totalTruePositives;
    }

    public void setTotalTruePositives(int totalTruePositives) {
        this.totalTruePositives = totalTruePositives;
    }

    public int getTotalTrueNegatives() {
        return totalTrueNegatives;
    }

    public void setTotalTrueNegatives(int totalTrueNegatives) {
        this.totalTrueNegatives = totalTrueNegatives;
    }

    public int getTotalFalsePositives() {
        return totalFalsePositives;
    }

    public void setTotalFalsePositives(int totalFalsePositives) {
        this.totalFalsePositives = totalFalsePositives;
    }

    public int getTotalFalseNegatives() {
        return totalFalseNegatives;
    }

    public void setTotalFalseNegatives(int totalFalseNegatives) {
        this.totalFalseNegatives = totalFalseNegatives;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getTotalOriginalToxic() {
        return totalOriginalToxic;
    }

    public void setTotalOriginalToxic(int totalOriginalToxic) {
        this.totalOriginalToxic = totalOriginalToxic;
    }

    public int getTotalOriginalNotToxic() {
        return totalOriginalNotToxic;
    }

    public void setTotalOriginalNotToxic(int totalOriginalNotToxic) {
        this.totalOriginalNotToxic = totalOriginalNotToxic;
    }

    public int getTotalReplyToxic() {
        return totalReplyToxic;
    }

    public void setTotalReplyToxic(int totalReplyToxic) {
        this.totalReplyToxic = totalReplyToxic;
    }

    public int getTotalReplyNotToxic() {
        return totalReplyNotToxic;
    }

    public void setTotalReplyNotToxic(int totalReplyNotToxic) {
        this.totalReplyNotToxic = totalReplyNotToxic;
    }

    public int getTotalReplyUnsure() {
        return totalReplyUnsure;
    }

    public void setTotalReplyUnsure(int totalReplyUnsure) {
        this.totalReplyUnsure = totalReplyUnsure;
    }

    @Override
    public String toString() {
        return "AnalysisSummary{" +
                "totalTruePositives=" + totalTruePositives + System.lineSeparator() +
                ", totalTrueNegatives=" + totalTrueNegatives + System.lineSeparator() +
                ", totalFalsePositives=" + totalFalsePositives + System.lineSeparator() +
                ", totalFalseNegatives=" + totalFalseNegatives + System.lineSeparator() +
                ", totalRecords=" + totalRecords + System.lineSeparator() +
                ", totalOriginalToxic=" + totalOriginalToxic +  System.lineSeparator() +
                ", totalOriginalNotToxic=" + totalOriginalNotToxic +    System.lineSeparator() +
                ", totalReplyToxic=" + totalReplyToxic +        System.lineSeparator() +
                ", totalReplyNotToxic=" + totalReplyNotToxic +          System.lineSeparator() +
                ", totalReplyUnsure=" + totalReplyUnsure +      System.lineSeparator() +
                ", totalErrorReply=" + totalErrorReply +    System.lineSeparator() +
                ", percision=" + getPercision() + System.lineSeparator() +
                ", recall=" + getRecall() + System.lineSeparator() +
                ", f1Score=" + getF1Score() + System.lineSeparator() +
                '}';
    }
}
