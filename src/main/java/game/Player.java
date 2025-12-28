package game;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Player {
    public final String[] answers = new String[2];
    private String question2;
    private int score;
    private boolean isVoted;

    public Player(){
        score = 0;
        isVoted = false;
    }
    public void vote(){
        isVoted = true;
    }
    public boolean isVoted(){
        return isVoted;
    }
    public void increaseScore(){
        score++;
    }
    public int getScore(){
        return score;
    }

    public void addAnswer(int num, String answer){
        if (0 <= num && num <= 1){
            answers[num] = answer;
        }
        else {
            System.err.println("add answer " + num + " а как так вышло..");
        }
    }
    public String getShuffledAnswer(){
        List<String> words = new java.util.ArrayList<>(Arrays.stream(answers[0].split(" ")).toList());
        Collections.shuffle(words);
        return String.join(" ", words);
    }

    public String getQuestion2() {
        return question2;
    }

    public void setQuestion2(String question2) {
        this.question2 = question2;
    }
}
