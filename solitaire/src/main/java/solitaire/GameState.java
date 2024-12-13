package solitaire;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameState {
    private Stack<Card> deck; // Full deck of cards
    private Stack<Card>[] gamePiles; // Seven piles on the tableau
    private Stack<Card> visibleCards; // Stack for visible cards
    private Stack<Card> discardedCards; // Discard pile
    private Stack<Card>[] foundationPiles; // Four foundation piles

    @SuppressWarnings("unchecked")
    public GameState() {
        // Initialize the game state
        deck = new Stack<>();
        gamePiles = new Stack[7]; // Array of 7 stacks
        visibleCards = new Stack<>();
        discardedCards = new Stack<>();

        // Initialize each game pile
        for (int i = 0; i < gamePiles.length; i++) {
            gamePiles[i] = new Stack<>();
        }
        foundationPiles = new Stack[4];
        for (int i = 0; i < foundationPiles.length; i++) {
            foundationPiles[i] = new Stack<>();
        }

        initializeDeck();
        shuffleDeck();
        dealInitialCards();
    }

    //REPLACE THE FOLLOWING 4 functions with your code from part 2

    // Creates a full deck of cards with all combinations of suits and ranks
    private void initializeDeck() {
      //USE IMPLEMENTATION FROM PART 2
      for (Suit suit : Suit.values()) {
        for (Rank rank : Rank.values()) {
            deck.push(new Card(suit, rank));
        }
    }
    }

    // Shuffles the deck
    private void shuffleDeck() {
        java.util.Collections.shuffle(deck);
    }

    // Deals cards to the 7 game piles
    private void dealInitialCards() {
        //USE IMPLEMENTATION FROM PART 2
        for (int i = 0; i < gamePiles.length; i++) {
            // Deal i + 1 cards to each pile (i = 0 -> 1 card, i = 1 -> 2 cards, etc.)
            for (int j = 0; j <= i; j++) {
                Card card = deck.pop();
                gamePiles[i].push(card);
            }
            // The top card of each pile should be face up
            gamePiles[i].peek().flip();
        }
    }

    // Draws up to three cards from the deck into visibleCards
    public void drawFromDeck() {
        //USE IMPLEMENTATION FROM PART 2
        // Step 1: Discard any remaining visible cards before drawing new cards
        discardCards();

        // Step 2: If the deck is empty, recycle the discarded cards
        if (deck.isEmpty()) {
            // Move all cards from the discarded pile back to the deck
            while (!discardedCards.isEmpty()) {
                deck.push(discardedCards.pop());
            }

            // Shuffle the deck after moving all discarded cards back
            shuffleDeck();
        }

        // Step 3: Draw up to 3 cards from the deck into visibleCards
        for (int i = 0; i < 3; i++) {
            if (!deck.isEmpty()) {
                Card card = deck.pop(); // Draw a card from the deck
                card.flip(); // Make sure the card is face up
                visibleCards.push(card); // Add the card to the visible cards stack
            }
        }
        }

        public void discardCards() {
            //takes whatever cards are remaining in the visibleCards pile and moves them to the discardPiles
            while (!visibleCards.isEmpty()) {
                discardedCards.push(visibleCards.pop());
            }
    }

    // new methods from part 3

    public boolean canCardMove(Card card, int toPile){
        /*a card can be moved from the visible cards to a pile if 
            A) The card is the opposite color and its rank is ONE smaller than the card it will be placed on
            B) The pile is empty and the card is a King
        */
        if (gamePiles[toPile].isEmpty() && card.getRank() == Rank.KING) {
            return true; // Rule B: The pile is empty and the card is a King.
        }

        if (!gamePiles[toPile].isEmpty()) {
            Card topCard = gamePiles[toPile].peek();
            // Rule A: Check if the card is one smaller and opposite color
            if ((card.getRank().ordinal() == topCard.getRank().ordinal() - 1) &&
                (card.getColor() != topCard.getColor())) {
                return true;
            }
        }

        return false;
    }

    // attempts to move top card from visible card stack to the toPileIndex
    // returns true if successful and false if unsuccessful
    public boolean moveCardFromVisibleCardsToPile(int toPileIndex) {
        /* 
            If a card can be moved, it should be popped from the visible cards pile and pushed to the pile it is added to
            hints: use peek() and ordinal() to determine whether or not a card can be moved. 
            USE the method you just made, canCardMove
        */
        if (visibleCards.isEmpty()) {
            return false; 
        }

        Card cardToMove = visibleCards.peek();
        if (canCardMove(cardToMove, toPileIndex)) {
            gamePiles[toPileIndex].push(visibleCards.pop());
            return true;
        }

        return false;
    }

    // Move a card from one pile to another
    public boolean moveCards(int fromPileIndex, int cardIndex, int toPileIndex) {
        Stack<Card> fromPile = gamePiles[fromPileIndex];

        // Create a sub-stack of cards to move
        ArrayList<Card> cardsToMove = new ArrayList<>(fromPile.subList(cardIndex, fromPile.size()));

        Card bottomCard = cardsToMove.get(0); // the bottom card to be moved

        // Check if bottomCard can be moved to the toPile
        // if we can move the cards, add cardsToMove to the toPile and remove them from the fromPile
        // Then, flip the next card in the fromPile stack

        //return true if successful, false if unsuccessful
        if (!gamePiles[toPileIndex].isEmpty()) {
            Card topCard = gamePiles[toPileIndex].peek();
            if ((bottomCard.getRank().ordinal() == topCard.getRank().ordinal() - 1) &&
                (bottomCard.getColor() != topCard.getColor())) {
                for (Card card : cardsToMove) {
                    gamePiles[toPileIndex].push(card);
                }
                for (int i = 0; i < cardsToMove.size(); i++) {
                    fromPile.pop();
                }
                if (!fromPile.isEmpty()) {
                    fromPile.peek().flip();
                }
                return true;
            }
        } else if (bottomCard.getRank() == Rank.KING) {
            for (Card card : cardsToMove) {
                gamePiles[toPileIndex].push(card);
            }
            for (int i = 0; i < cardsToMove.size(); i++) {
                fromPile.pop();
            }
            if (!fromPile.isEmpty()) {
                fromPile.peek().flip();
            }
            return true;
        }

        return false;
    }
    private boolean canMoveToFoundation(Card card, int foundationIndex){
        //The foundation piles are the 4 piles that you have to build to win the game. 
        //In order for a card to be added to the pile, it needs to be one larger than the 
        //current top card of the foundation pile. It needs to be the same suit. 
        //If the foundation pile is empty, the new card must be an ace

        //This method should return true if a card can be moved to the foundation, and false otherwise. 
        
        //hint: another good time to use peek() and ordinal()
        Stack<Card> foundationPile = foundationPiles[foundationIndex];

        if (foundationPile.isEmpty()) {
            // If foundation pile is empty, only an Ace can be added
            return card.getRank() == Rank.ACE;
        } else {
            // Otherwise, check if the card is one rank higher and the same suit as the top card
            Card topCard = foundationPile.peek();
            return (card.getRank().ordinal() == topCard.getRank().ordinal() + 1) && (card.getSuit() == topCard.getSuit());
        }
   }

    public boolean moveToFoundation(int fromPileIndex, int foundationIndex) {
        //check if we can move the top card of the fromPile to the foundation at foundationIndex
        
        //remember to flip the new top card if it is face down

        //return true if successful, false otherwise
        Stack<Card> fromPile = gamePiles[fromPileIndex];
        if (fromPile.isEmpty()) {
            return false;
        }

        Card topCard = fromPile.peek();
        if (canMoveToFoundation(topCard, foundationIndex)) {
            // Move the card to the foundation pile
            foundationPiles[foundationIndex].push(fromPile.pop());
            // Flip the next card in the source pile if it exists
            if (!fromPile.isEmpty()) {
                fromPile.peek().flip();
            }
            return true;
        }

        return false;
    }

    public boolean moveToFoundationFromVisibleCards(int foundationIndex) {
        //similar to the above method, 
        //move the top card from the visible cards to the foundation pile with index foundationIndex if possible
    
        //return true if successful, false otherwise. 
        if (visibleCards.isEmpty()) {
            return false;
        }

        Card topCard = visibleCards.peek();
        if (canMoveToFoundation(topCard, foundationIndex)) {
            // Move the card to the foundation pile
            foundationPiles[foundationIndex].push(visibleCards.pop());
            return true;
        }
        
        return false;
    }

    

    // Don't change this, used for testing
    public void printState() {
        System.out.println("Deck size: " + deck.size());

        System.out.print("Visible cards: ");
        if (visibleCards.isEmpty()) {
            System.out.println("None");
        } else {
            for (Card card : visibleCards) {
                System.out.print(card + " ");
            }
            System.out.println();
        }

        System.out.println("Discarded cards: " + discardedCards.size());

        System.out.println("Game piles:");
        for (int i = 0; i < gamePiles.length; i++) {
            System.out.print("Pile " + (i + 1) + ": ");
            if (gamePiles[i].isEmpty()) {
                System.out.println("Empty");
            } else {
                for (Card card : gamePiles[i]) {
                    System.out.print(card + " ");
                }
                System.out.println();
            }
        }
    }

    // getters
    public Stack<Card> getGamePile(int index) {
        return gamePiles[index];
    }

    public Stack<Card> getFoundationPile(int index) {
        return foundationPiles[index];
    }

    public Stack<Card> getDeck() {
        return deck;
    }

    public Stack<Card> getVisibleCards() {
        return visibleCards;
    }
}
