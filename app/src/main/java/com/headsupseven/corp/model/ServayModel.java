package com.headsupseven.corp.model;

/**
 * Created by tmmac on 3/12/17.
 */

public class ServayModel {

    private int ID;
    private String CreatedAt="";
    private String UpdatedAt="";
    private int SurveyID;
    private String Question="";
    private String Answer1="";
    private String Answer2="";
    private String Answer3="";
    private String Answer4="";
    private String Answer5="";
    private boolean CustomAnswer=false;


    public String getAnswer5() {
        return Answer5;
    }

    public void setAnswer5(String answer5) {
        Answer5 = answer5;
    }

    public boolean isCustomAnswer() {
        return CustomAnswer;
    }

    public void setCustomAnswer(boolean customAnswer) {
        CustomAnswer = customAnswer;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSurveyID() {
        return SurveyID;
    }

    public void setSurveyID(int surveyID) {
        SurveyID = surveyID;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer1() {
        return Answer1;
    }

    public void setAnswer1(String answer1) {
        Answer1 = answer1;
    }

    public String getAnswer2() {
        return Answer2;
    }

    public void setAnswer2(String answer2) {
        Answer2 = answer2;
    }

    public String getAnswer3() {
        return Answer3;
    }

    public void setAnswer3(String answer3) {
        Answer3 = answer3;
    }

    public String getAnswer4() {
        return Answer4;
    }

    public void setAnswer4(String answer4) {
        Answer4 = answer4;
    }
}
