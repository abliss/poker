import com.google.common.collect.*;
import java.util.*;
import java.util.concurrent.*;

class Strategies {

    static class ScoreAndKeepers {
	public float score;
	public Collection<Card> keepers;
	public ScoreAndKeepers(float score, Collection<Card> keepers) {
	    this.score = score;
	    this.keepers = keepers;
	}
    }

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

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
            List<Card> result = drawMap.get(Card.suitify(hand.getCardArray()));
            if (result == null) {
                throw new IllegalStateException("incomplete drawMap: " + hand.getCards());
            }
            return result;
        }
     }
     */ 
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
    public static <T> Collection<Card> bestDraw(final Hand hand, final Deck deck, final Strategy<T> other, final T info) {
        System.out.println("XXXX bestDraw of hand " + hand + ":" + (System.currentTimeMillis() - Test.START)) ;
        Collection<Card> bestKeepers = null;
        float bestScore = -2.0f;

        final List<Future<ScoreAndKeepers>> tasks = Lists.newArrayList();

        for (int i = 0; i < 16; i++) {  // all possible draws
	    final int i2 = i;
	    tasks.add(executor.submit(new Callable<ScoreAndKeepers>() {
		    public ScoreAndKeepers call() {
                final EnumSet<Card> keepers = EnumSet.noneOf(Card.class);

			for (int j = 0; j < 4; j++) {
			    if ((i2 & (1 << j)) > 0) {
				keepers.add(hand.cardAt(j));
			    }
			}
			//System.out.println("XXXX Considering keeping: " + keepers);
			// TODO: use bestScore as threshold
			float score = scoreDraw(keepers, deck, other, info, -2.0f);
			return new ScoreAndKeepers(score, keepers);
		    }}));

        }
	for (Future<ScoreAndKeepers> f : tasks) {
	    try {
		if (f.get().score > bestScore) {
		    bestScore = f.get().score;
		    bestKeepers = f.get().keepers;
		}
	    } catch (Exception e) {
		// catch random thread exceptions and blow up
		throw new RuntimeException(e);
	    }
	}

        System.out.println("XXXX Returning best " + bestKeepers + " score= " + bestScore + ":" + (System.currentTimeMillis() - Test.START));
        return bestKeepers;
    }

    /**
     * Expected number of wins.  If EV will be less than threshhold, just return threshhold.
     */
    private static <T> float scoreDraw(Collection<Card> kept, Deck deck, Strategy<T> other, T info,
                                       float threshhold) {
        float totalScore = 0;
        int count;
        System.out.println("Scoring Draw to " + kept  + ":" + (System.currentTimeMillis() - Test.START));
        if (kept.size() == 4) {
            totalScore = scoreHand(kept, deck, other, info, threshhold);
            count = 1;
        } else if (kept.size() == 3) {
            count = deck.size();
            
            for (Card c : deck) {
                kept.add(c);
                totalScore += scoreHand(kept, deck.without(c), other, info, -2.0f);
                // TODO: thresh check
                kept.remove(c);
            }
        } else if (kept.size() == 2) {
            totalScore = 0;
            int size = deck.size();
            count = size * (size - 1) / 2;
            for (Collection<Card> draw : deck.subsetsOfSize2()) {
                kept.addAll(draw);
                totalScore += scoreHand(kept, deck.without(draw), other, info, -2.0f);
                // TODO: thresh check
                kept.removeAll(draw);
            }
        } else if (kept.size() == 1) {
            totalScore = 0;
            int size = deck.size();
            count = size * (size - 1) * (size - 2) / 6;
            for (Collection<Card> draw : deck.subsetsOfSize3()) {
                kept.addAll(draw);
                totalScore += scoreHand(kept, deck.without(draw), other, info, -2.0f);
                // TODO: thresh check
                kept.removeAll(draw);
            }
        } else if (kept.size() == 0) {
            // XXX As long as we're drawing against the pat distro, we know this must = 0
            totalScore = 0.0f;
            count = 1;
            /*
            for (Collection<card> draw : deck.subsetsOfSize4()) {
                kept.add(draw);
                tempDeck2 = tempDeck1.without(draw);
                totalScore += scoreHand(kept, deck.without(draw), other, info, -2.0f);
                // TODO: thresh check
                kept.removeAll(draw);
            }
            */
        } else {
            throw new RuntimeException("Bad size for kept: " + kept.size());
        }
        float score = totalScore / (float) count;
        System.out.println("Scoring Draw to " + kept + " as " + totalScore + "/" + count + "=  " + score  + ":" + (System.currentTimeMillis() - Test.START));
        return score;

    }

    /**
     * Expected number of wins-losses.  If EV will be less than threshhold, just return threshhold.
     */
    private static <T> float scoreHand(Collection<Card> handCards, Deck deck, Strategy<T> other, T info,
                                       float threshhold) {
        //System.out.println("Scoring " + handCards + " against " + deck.asList()  + ":" + (System.currentTimeMillis() - Test.START));
        Hand myHand = Hand.from(handCards);
        int wins = 0;
        int losses = 0;
        Multiset<Hand> distro = deck.allSuitifiedHands();
        int total = distro.size();
        for (Multiset.Entry<Hand> eirHands : distro.entrySet()) {
            int winner = winner = myHand.compareTo(eirHands.getElement());
            int eirCount = eirHands.getCount();
            if (winner > 0) {
                wins += eirCount;
            } else if (winner < 0) {
                losses += eirCount;
            }
            // TODO: thresh check
        }
        float score = ((float) (wins - losses)) / total;
        System.out.println("Scoring " + myHand + " as " + score + "  :" + (System.currentTimeMillis() - Test.START));
        return score;
    }

}
