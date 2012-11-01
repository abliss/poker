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
        //System.out.println("XXXX bestDraw of hand " + hand);
        List<Card> bestKeepers = null;
        float bestScore = -2.0f;
        for (int i = 0; i < 16; i++) {  // all possible draws
            List<Card> keepers = Lists.newArrayListWithExpectedSize(4);
            for (int j = 0; j < 4; j++) {
                if ((i & (1 << j)) > 0) {
                    keepers.add(hand.cardAt(j));
                }
            }
            //System.out.println("XXXX Considering keeping: " + keepers);
            float score = scoreDraw(keepers, deck, other, info, bestScore);
            //System.out.println("XXXX Considering keeping: " + keepers + " score= " + score);
            if (score > bestScore) {
                //System.out.println("XXXX New best!: " + keepers + " score= " + score);
                bestScore = score;
                bestKeepers = keepers;
            }
        }
        //System.out.println("XXXX Returning best " + bestKeepers + " score= " + bestScore);
        return bestKeepers;
    }

    /**
     * Expected number of wins.  If EV will be less than threshhold, just return threshhold.
     */
    private static <T> float scoreDraw(List<Card> kept, Deck deck, Strategy<T> other, T info,
                                       float threshhold) {
        float totalScore = 0;
        int count;
        //System.out.println("Scoring Draw to " + kept + " at " + new Date());
        if (kept.size() == 4) {
            totalScore = scoreHand(kept, deck, other, info, threshhold);
            count = 1;
        } else if (kept.size() == 3) {
            count = deck.size();
            for (Card c : deck.asList()) {
                kept.add(c);
                totalScore += scoreHand(kept, deck.without(c), other, info, -2.0f);
                // TODO: thresh check
                kept.remove(3);
            }
        } else if (kept.size() == 2) {
            totalScore = 0;
            int size = deck.size();
            count = size * (size - 1) / 2;
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
                    kept.remove(3);
                }
                kept.remove(2);
            }
        } else if (kept.size() == 1) {
            totalScore = 0;
            int size = deck.size();
            count = size * (size - 1) * (size - 2) / 6;
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
                        kept.remove(3);
                    }
                    kept.remove(2);
                }
                kept.remove(1);
            }
        } else if (kept.size() == 0) {
            totalScore = 0;
            int size = deck.size();
            count = size * (size - 1) * (size - 2) * (size - 3)/ 24;
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
                        tempDeck3 = tempDeck2.without(draw3);
                        for (int l = k + 1; l < size; l++) {
                            Card draw4 = deck.cardAt(l);
                            kept.add(draw4);
                            tempDeck4 = tempDeck3.without(draw4);
                            totalScore += scoreHand(kept, tempDeck4, other, info, -2.0f);
                            // TODO: thresh check
                            kept.remove(3);
                        }
                        kept.remove(2);
                    }
                    kept.remove(1);
                }
                kept.remove(0);
            }
        } else {
            throw new RuntimeException("Bad size for kept: " + kept.size());
        }
        float score = totalScore / (float) count;
        //System.out.println("Scoring Draw to " + kept + " as " + totalScore + "/" + count + "=  " + score + "  at " + new Date());
        return score;

    }

    /**
     * Expected number of wins-losses.  If EV will be less than threshhold, just return threshhold.
     */
    private static <T> float scoreHand(List<Card> handCards, Deck deck, Strategy<T> other, T info,
                                       float threshhold) {
        //System.out.println("Scoring " + handCards + " against " + deck.asList() + " at " + new Date());
        Hand myHand = new Hand(handCards);
        int wins = 0;
        int losses = 0;
        int total = 0;
        // Eir hands are in increasing order; once beaten, we stay beaten.
        boolean check = true;
        for (Multiset.Entry<Hand> eirHands : deck.allSuitifiedHands().entrySet()) {
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
        float score = ((float) (wins - losses)) / total;
        //System.out.println("Scoring " + myHand + " as " + score + " at " + new Date());
        return score;
    }

}