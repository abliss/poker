import com.google.common.collect.*;
import java.util.*;


class Strategies {
    /**
     * Never draws.
     */
    public static final Strategy<Void> PatStrategy = new Strategy<Void>() {
        public List<Card> discard(Hand hand, Void info) {
            return hand.getCards();
        }
    };

    /**
     * Chooses cards to discard based only on the hand; constructed from an
     * exhaustive list of possibilities.  Every sutified hand must be a key in
     * the map.
     */ 
     public static final class MapStrategy implements Strategy<Void> {
        private final Map<List<Card>, List<Card>> drawMap;
        public MapStrategy(Map<List<Card>, List<Card>> drawMap) {
            this.drawMap = drawMap;
            if (drawMap.size() != 18096) { // 270725 before suit abstraction
                throw new IllegalArgumentException("bad drawMap size;want 18096: "
                                                   + drawMap.size());
            }
        }
        public List<Card> discard(Hand hand, Void info) {
            List<Card> result = drawMap.get(Card.suitify(hand.getCards()));
            if (result == null) {
                throw new IllegalStateException("incomplete drawMap: " + hand.getCards());
            }
            return result;
        }
     }

    /**
     * What distribution does this strategy lead to after one deal and one draw
     * from the given deck?
     */
    public static <T> Distribution getDistribution(Strategy<T> strat, T info) {
        Distribution distro = new Distribution();
        for (Hand hand : Hand.getHandValues().keySet()) {
            Deck deck = new Deck();
            for (Card card : hand.getCards()) {
                deck = deck.without(card);
            }
            List<Card> partial = strat.discard(hand, info);
            distro.merge(Distribution.generate(partial, deck));
        }
        return distro;
    }
    
    /**
     * Finds the winningest strategy against a given strategy on a given hand.  
     */
    public static <T> List<Card> bestDraw(Hand hand, Deck deck, Strategy<T> other, T info) {
        System.out.println("XXXX bestDraw of hand " + hand);
        List<Card> bestKeepers = null;
        float bestScore = -2.0f;
        for (int i = 0; i < 16; i++) {  // all possible draws
            List<Card> keepers = Lists.newArrayListWithExpectedSize(4);
            for (int j = 0; j < 4; j++) {
                if ((i & (1 << j)) > 0) {
                    keepers.add(hand.cardAt(j));
                }
            }
            System.out.println("XXXX Considering keeping: " + keepers);
            scoreDraw(keepers, deck, other, info, bestScore);
        }
        return null;
    }

    /**
     * Expected number of wins.  If EV will be less than threshhold, just return threshhold.
     */
    private static <T> float scoreDraw(List<Card> kept, Deck deck, Strategy<T> other, T info,
                                       float threshhold) {

        if (kept.size() == 4) {
            return scoreHand(kept, deck, other, info, threshhold);
        } else if (kept.size() == 3) {
            float totalScore = 0;
            int count = deck.size();
            for (Card c : deck) {
                kept.add(c);
                totalScore += scoreHand(kept, deck.without(c), other, info, -2.0f);
                // TODO: thresh check
                kept.remove(3);
                count++;
            }
            return totalScore / (float) count;
        } else if (kept.size() == 2) {
            float totalScore = 0;
            int size = deck.size();
            int count = size * (size - 1) / 2;
            Deck tempDeck1;
            Deck tempDeck2;
            for (int i = 0; i < size; i++) {
                Card draw1 = deck.cardAt(i);
                kept.add(draw1);
                tempDeck1 = deck.without(draw1);
                for (int j = i + 1; j < size; j++) {
                    Card draw2 = deck.cardAt(j);
                    kept.add(draw2);
                    tempDeck2 = tempDeck1.without(draw2);
                    totalScore += scoreHand(kept, tempDeck2, other, info, -2.0f);
                    // TODO: thresh check
                    count++;
                    kept.remove(3);
                }
                kept.remove(2);
            }
            return totalScore / (float) count;
        } else if (kept.size() == 1) {
            float totalScore = 0;
            int size = deck.size();
            int count = size * (size - 1) * (size - 2) / 6;
            Deck tempDeck1;
            Deck tempDeck2;
            Deck tempDeck3;
            for (int i = 0; i < size; i++) {
                Card draw1 = deck.cardAt(i);
                kept.add(draw1);
                tempDeck1 = deck.without(draw1);
                for (int j = i + 1; j < size; j++) {
                    Card draw2 = deck.cardAt(j);
                    kept.add(draw2);
                    tempDeck2 = tempDeck1.without(draw2);
                    for (int k = j + 1; k < size; k++) {
                        Card draw3 = deck.cardAt(k);
                        kept.add(draw3);
                        tempDeck3 = tempDeck2.without(draw3);
                        totalScore += scoreHand(kept, tempDeck3, other, info, -2.0f);
                        // TODO: thresh check
                        count++;
                        kept.remove(3);
                    }
                    kept.remove(2);
                }
                kept.remove(1);
            }
            return totalScore / (float) count;
        } else if (kept.size() == 0) {
            float totalScore = 0;
            int size = deck.size();
            int count = size * (size - 1) * (size - 2) * (size - 3)/ 24;
            Deck tempDeck1;
            Deck tempDeck2;
            Deck tempDeck3;
            Deck tempDeck4;
            for (int i = 0; i < size; i++) {
                Card draw1 = deck.cardAt(i);
                kept.add(draw1);
                tempDeck1 = deck.without(draw1);
                for (int j = i + 1; j < size; j++) {
                    Card draw2 = deck.cardAt(j);
                    kept.add(draw2);
                    tempDeck2 = tempDeck1.without(draw2);
                    for (int k = j + 1; k < size; k++) {
                        Card draw3 = deck.cardAt(k);
                        kept.add(draw3);
                        tempDeck3 = tempDeck3.without(draw3);
                        for (int l = k + 1; l < size; l++) {
                            Card draw4 = deck.cardAt(l);
                            kept.add(draw4);
                            tempDeck4 = tempDeck3.without(draw4);
                            totalScore += scoreHand(kept, tempDeck4, other, info, -2.0f);
                            // TODO: thresh check
                            count++;
                            kept.remove(3);
                        }
                        kept.remove(2);
                    }
                    kept.remove(1);
                }
                kept.remove(0);
            }
            return totalScore / (float) count;
        } else {
            throw new RuntimeException("Bad size for kept: " + kept.size());
        }
    }

    /**
     * Expected number of wins-losses.  If EV will be less than threshhold, just return threshhold.
     */
    private static <T> float scoreHand(List<Card> handCards, Deck deck, Strategy<T> other, T info,
                                       float threshhold) {
        Hand myHand = new Hand(handCards);
        int wins = 0;
        int losses = 0;
        int total = 0;
        // Eir hands are in increasing order; once beaten, we stay beaten.
        boolean check = true;
        for (Multiset.Entry<Hand> eirHands : deck.allSuitifiedHands()) {
            // TODO: binary search
            int winner = -1;
            if (check) {
                winner = myHand.compareTo(eirHands.getElement());
            }
            int eirCount = eirHands.getCount();
            if (winner > 0) {
                wins += eirCount;
            } else if (winner < 0) {
                losses += eirCount;
                check = false;
            }
            total += eirCount;
            // TODO: thresh check
        }
        return ((float) wins) / total;
    }

}