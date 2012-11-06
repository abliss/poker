import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

class Hand implements Comparable<Hand> {
    
  private static Map<Hand, Integer> handValues = null;
  private int hashCode = 0;
  private static Map<Integer, Hand> hashes = null;
  public static Map<Hand, Integer> getHandValues() {
        if (handValues == null) {
            handValues = Maps.newHashMapWithExpectedSize(270725);
            hashes = Maps.newHashMapWithExpectedSize(270725);
            // Get playable hand for every hand
            Deck deck = new Deck();
            long startTime = System.currentTimeMillis();
            for (List<Card> set : deck.subsetsOfSize4()) {
                Hand h = Hand.from(set);
                Integer playableCode = h.playableHandCodeImpl();
                handValues.put(h, playableCode);
                hashes.put(h.hashCode(), h);
            }

            System.out.println(handValues.size());
            System.out.println(handValues.entrySet().size());
            System.out.println(handValues.keySet().size());
            System.out.println(System.currentTimeMillis() - startTime);	
		
            //System.out.println("all hands:\n" + new Distribution(handValues.keySet()).toString());
        }
        return handValues;
  }

  
	/** Cards in the hand in sorted order */
	private final Card[] cards = new Card[4];

	public Hand(Card c0, Card c1, Card c2, Card c3) {
        cards[0] = c0;
        cards[1] = c1;
        cards[2] = c2;
        cards[3] = c3;
        int[] ords = new int[4];
        ords[0] = c0.ordinal();
        ords[1] = c1.ordinal();
        ords[2] = c2.ordinal();
        ords[3] = c3.ordinal();
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                if (ords[j] < ords[i]) {
                    int temp = ords[i];
                    ords[i] = ords[j];
                    ords[j] = temp;
                    Card tempC = cards[i];
                    cards[i] = cards[j];
                    cards[j] = tempC;
                }
            }
        }
	}

    /** Deal a new hand from the top of the given deck. */
    public static Hand from(Deck d) {
        return new Hand(d.draw(), d.draw(), d.draw(), d.draw());
    }
	public static Hand from(Card[] cardList) {
        return new Hand(cardList[0], cardList[1], cardList[2], cardList[3]);
    }
	public static Hand from(List<Card> cardList) {
        return new Hand(cardList.get(0), cardList.get(1), cardList.get(2), cardList.get(3));
	}
    public static Hand from(Collection<Card> cardList) {
        return Hand.from(Lists.newArrayList(cardList));
    }
    public static Hand fromHashCode(Integer hashCode) {
        getHandValues();
        return hashes.get(hashCode);
    }

	public Card cardAt(int index) {
		return cards[index];
	}

	public List<Card> without(Card... remove) {
		List<Card> partialHand = Lists.newArrayListWithExpectedSize(4 - remove.length);
		for (Card c : cards) {
			boolean shouldRemove = false;
			for (Card cardToRemove : remove) {
				if (c.equals(cardToRemove)) {	
					shouldRemove = true;
				}
			}
			if (!shouldRemove) {
				partialHand.add(c);
			}
		}	
		return partialHand;
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Card card : cards) {
			s.append(card);
		}
		
		s.append(" (");
        s.append(decodePlayableHand(playableHandCode()));
		s.append(")");
		return s.toString();
	}

	/**
	 * +1 if this hand is better than other hand
	 * -1 if it's worse
	 * 0 if they're the same
	 */
	@Override public int compareTo(Hand otherHand) {
        return this.playableHandCode().compareTo(otherHand.playableHandCode());
	}

    public int compareTo(Integer otherHandPlayableHandCode) {
        return this.playableHandCode().compareTo(otherHandPlayableHandCode);
	}

	/**
	 * Grossly inefficient check to see if a list of cards is a playable hand.
	 */
	private static boolean canPlay(List<Card> cards) {
		boolean[] suits = new boolean[4];
		boolean[] ranks = new boolean[13];
		for (Card card : cards) {
			if (suits[card.getSuit().ordinal()] || ranks[card.getRank().ordinal()]) {
				return false;
			}
			suits[card.getSuit().ordinal()] = true;
			ranks[card.getRank().ordinal()] = true;
		}
		return true;
	}

    
	// test cases - 4c4s9cQc -> 4s9c
    
    public static String decodePlayableHand(int playableHand) {
        StringBuilder s = new StringBuilder();
        int code = playableHand;
        for (int i = 0; i < 5; i++) {
            s.append(Card.Rank.values()[15 - (code & 0xf)]);
            code >>= 4;
            if (code == 0) return s.toString();
        }
        throw new IllegalArgumentException("Bad playable code: " + playableHand);
    }

	/**
	 * Return the playable cards in a hand.  Assumes that the hand is in sorted order.
	 */
	public Integer playableHandCode() {
        return getHandValues().get(this);
    }
    private Integer playableHandCodeImpl() {
        int code = 0;
        int bucket = 0;
        for (Card c : playableHandImpl()) {
            code |= ((15 - c.getRank().ordinal()) << bucket);
            bucket += 4;
        }
        return code;
    }
	private List<Card> playableHandImpl() {
        long now = System.currentTimeMillis();
		// 4 cards
		if (canPlay(Lists.newArrayList(cards))) {
			return ImmutableList.of(cards);
		}

		// 3 cards
		List<Card> current = Lists.newArrayListWithExpectedSize(3);

		current = ImmutableList.of(cards[0], cards[1], cards[2]);
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards[0], cards[1], cards[3]);
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards[0], cards[2], cards[3]);
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards[1], cards[2], cards[3]);
		if (canPlay(current)) { return current; }
		
		// 2 cards
		current = ImmutableList.of(cards[0], cards[1]);
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards[0], cards[2]);
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards[1], cards[2]);
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards[0], cards[3]);
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards[1], cards[3]);
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards[2], cards[3]);
		if (canPlay(current)) { return current; }

		// 1 card
        List<Card> ret = ImmutableList.of(cards[0]);
		return ret;
	}

	public List<Card> getCards() {
		return ImmutableList.of(cards);
	}
    /*
	public double rank(Deck deck) {
//		long startTime = System.currentTimeMillis();
//		int count = 0;
		int wins = 0;
		int ties = 0;
		int losses = 0;
		for (int i = 0; i < deck.size(); ++i) {
v			for (int j = i + 1; j < deck.size(); ++j) {
				for (int k = j + 1; k < deck.size(); ++k) {
					for (int l = k + 1; l < deck.size(); ++l) {
						Hand compHand = new Hand(deck.cardsAt(i, j, k, l));
						int result = compareTo(compHand);
						if (result > 0) {
							wins++;
						} else if (result < 0) {
							losses++;
						} else {
							ties++;
						}
					}
				}
			}
		}
        	  System.out.println(toString());
        	  System.out.println("wins: " + wins);
        	  System.out.println("ties: " + ties);
        	  System.out.println("losses: " + losses);
        	  System.out.println("total: " + (wins + ties + losses));

		//	    System.out.println("time: " + (System.currentTimeMillis() - startTime));
		//	    return wins - losses;
		return (double) wins / (wins + ties + losses);
	}
		 */
	public boolean equals(Object other) {
		if (!(other instanceof Hand)) {
			return false;
		}

		Hand h = (Hand) other;
		return hashCode == other.hashCode();
	}

    /**
     * Given four cards in increasing order, what would the hash code be of that hand?
     */
	public static int hashCode(Card c0, Card c1, Card c2, Card c3) {
        return c0.ordinal() |
            (c1.ordinal() << 6) |
            (c2.ordinal() << 12) |
            (c3.ordinal() << 18);
    }

	public int hashCode() {
        if (hashCode == 0) {
            hashCode = hashCode(cards[0], cards[1], cards[2], cards[3]);
        }
		return hashCode;
	}

    public int playableHandSize() {
        int code = playableHandCode();
        if (code > (1 << 12)) return 4;
        if (code > (1 << 8)) return 3;
        if (code > (1 << 4)) return 2;
        return 1;
    }
    /**
     * Create a new hand by keeping only the cards of the given indices, and
     * drawing the rest from the given deck.
     */
    public Hand draw(Deck d, List<Integer> keepers) {
		List<Card> newCards = Lists.newArrayListWithExpectedSize(4);
        for (int keepIndex : keepers) {
            newCards.add(cards[keepIndex]);
        }
        while (newCards.size() < 4) {
            newCards.add(d.draw());
        }
        return from(newCards);
    }
}
