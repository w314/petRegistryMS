package com.revature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyApp {
    private class Player {
        private int ranking;
        private String name;
        private int age;

        public Player(int ranking, String name, int age) {
            this.ranking = ranking;
            this.name = name;
            this.age = age;
        }
// constructor, getters, setters
    }

    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
        MyApp myApp = new MyApp();
        List<Player> footballTeam = new ArrayList<>();
        Player player1 = myApp.new Player(59, "John", 20);
        Player player2 = myApp.new Player(67, "Roger", 22);
        Player player3 = myApp.new Player(45, "Steven", 24);
        footballTeam.add(player1);
        footballTeam.add(player2);
        footballTeam.add(player3);

        System.out.println("Before Sorting : " + footballTeam);
        Collections.sort(footballTeam);
        System.out.println("After Sorting : " + footballTeam);

    }
}