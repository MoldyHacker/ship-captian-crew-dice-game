package edu.wctc;

import java.util.*;
import java.util.stream.Collectors;

public class DiceGame {
    private final List<Player> players;
    private final List<Die> dice;
    private final int maxRolls;
    private Player currentPlayer;

    public DiceGame(int countPlayers, int countDice, int maxRolls){
        //Constructor that initializes all final instance fields.
        //Creates the required number of Player objects and Die objects and adds them to the appropriate lists.
        //If the number of players is less than 2, throws an IllegalArgumentException.
        players = new ArrayList<>();
        dice = new ArrayList<>();
        if (countPlayers < 2) {
            throw new IllegalArgumentException();
        }
        else {
            for (int i = 0; i < countPlayers; i++)
                players.add(new Player());
        }
        for (int i = 0; i < countDice; i++)
            dice.add(new Die(6));

        this.maxRolls = maxRolls;

        currentPlayer = players.get(0);
    }

    private boolean allDiceHeld(){
        //Returns true if all dice are held, false otherwise.
        //Hint: allMatch
        return dice.stream().allMatch(Die::isBeingHeld);
    }

    public boolean autoHold(int faceValue){
        //If there already is a die with the given face value that is held, just return true.
        //If there is a die with the given face value that is unheld, hold it and return true. (If there are multiple matches, only hold one of them.)
        //If there is no die with the given face value, return false.
        //Hints: filter, findFirst, isPresent
        if (isHoldingDie(faceValue)){
            return true;
        } else if (dice.stream().anyMatch(die -> die.getFaceValue() == faceValue)){
            dice.stream().filter(die -> die.getFaceValue() == faceValue).findFirst().ifPresent(Die::holdDie);
            return true;
        }
        return false;
    }

    public boolean currentPlayerCanRoll(){
        //Returns true if the current player has any rolls remaining and if not all dice are held.
        return currentPlayer.getRollsUsed() < maxRolls;
    }

    public int getCurrentPlayerNumber(){
        //	Returns the player number of the current player.
        return currentPlayer.getPlayerNumber();
    }

    public int getCurrentPlayerScore(){
        //	Returns the score of the current player.
        return currentPlayer.getScore();
    }

    public String getDiceResults(){
        //Resets a string composed by concatenating each Die's toString.
        //Hints: map, Collectors.joining
        return dice.stream().map(Die::toString).collect(Collectors.joining(", "));
    }

    public String getFinalWinner(){
        // Finds the player with the most wins and returns its toString.
        //Hints: Collections.max, Comparator.comparingInt
        return players.stream().max(Comparator.comparingInt(Player::getWins)).map(Player::toString).orElse("");
    }

    public String getGameResults(){
        //Sorts the player list field by score, highest to lowest.
        //Awards each player that earned the highest score a win and all others a loss.
        //Returns a string composed by concatenating each Player's toString.
        //Hints: Comparator.comparingInt, reversed
        //More hints: forEach
        //Final hints: map, Collectors.joining
        int highScore = players.stream().collect(Collectors.summarizingInt(Player::getScore)).getMax();
        players.stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .forEach(player -> {
                    if (player.getScore() == highScore && player.getScore() != 0)
                        player.addWin();
                    else
                        player.addLoss();
                });
        return players.stream().map(Player::toString).collect(Collectors.joining(", "));
    }

    private boolean isHoldingDie(int faceValue){
        //Returns true if there is any held die with a matching face value, false otherwise.
        //Hints: filter, findFirst, isPresent
        return dice.stream().anyMatch(die -> die.isBeingHeld() && die.getFaceValue() == faceValue);
    }

    public boolean nextPlayer(){
        //	If there are more players in the list after the current player, updates currentPlayer to be the next player and returns true. Otherwise, returns false.
        if (getCurrentPlayerNumber() < players.size()){
            currentPlayer = players.get(getCurrentPlayerNumber());
            return true;
        }
        return false;
    }

    public void playerHold(char dieNum){
        //Finds the die with the given die number (NOT the face value) and holds it.
        //Hints: filter, findFirst, isPresent
        dice.stream().filter(die -> die.getDieNum() == dieNum).findFirst().ifPresent(Die::holdDie);
    }

    public void resetDice(){
        //Resets each die.
        //Hint: forEach
        dice.forEach(Die::resetDie);
    }

    public void resetPlayers(){
        //Resets each player.
        //Hint: forEach
        players.forEach(Player::resetPlayer);
    }

    public void rollDice(){
        //Logs the roll for the current player, then rolls each die.
        //Hint: forEach
        currentPlayer.roll();
        dice.forEach(Die::rollDie);
    }

    public void scoreCurrentPlayer(){
        //If there is currently a ship (6), captain (5), and crew (4) die held, adds the points for the remaining two dice (the cargo) to the current player's score.
        //If there is not a 6, 5, and 4 held, assigns no points.
        //Note: The 6, 5, and 4 held as the ship, captain, and crew are not worth points.
        if (isHoldingDie(6) && isHoldingDie(5) && isHoldingDie(4))
            currentPlayer.setScore(dice.stream().mapToInt(Die::getFaceValue).sum() - 15);
    }

    public void startNewGame(){
        //Assigns the first player in the list as the current player. (The list will still be sorted by score from the previous round, so winner will end up going first.
        //Resets all players.
        currentPlayer = players.get(0);
        resetPlayers();
    }
}
