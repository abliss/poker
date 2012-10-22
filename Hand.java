import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

class Hand implements Comparable<Hand> {

	private static Map<Hand, List<Card>> handValues = null;

	private static Map<Hand, List<Card>> getHandValues() {
        if (handValues == null) {
            handValues = Maps.newHashMapWithExpectedSize(270725);
            // Get playable hand for every hand
            Deck deck = new Deck();
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < deck.size(); ++i) {
                for (int j = i + 1; j < deck.size(); ++j) {
                    for (int k = j + 1; k < deck.size(); ++k) {
                        for (int l = k + 1; l < deck.size(); ++l) {
                            Hand h = new Hand(deck.cardsAt(i, j, k, l));
                            handValues.put(h, ImmutableList.copyOf(h.playableHand()));
                        }
                    }
                }
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
	private final List<Card> cards;

	Hand(Card c1, Card c2, Card c3, Card c4) {
		cards = Lists.newArrayList(c1, c2, c3, c4);
		Collections.sort(cards);
	}

    /** Deal a new hand from the top of the given deck. */
    Hand(Deck d) {
        this(d.draw(), d.draw(), d.draw(), d.draw());
    }
    
	Hand(Collection<Card> cardList) {
		if (cardList.size() != 4) {
			throw new IllegalArgumentException();
		}
		// TODO: is this dodgy?
		cards = Lists.newArrayList(cardList);
		Collections.sort(cards);
	}

	public Card cardAt(int index) {
		return cards.get(index);
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
		for (Card card : playableHand()) {
			s.append(card.getRank());
		}
		s.append(")");
		return s.toString();
	}

	/**
	 * +1 if this hand is better than other hand
	 * -1 if it's worse
	 * 0 if they're the same
	 */
	@Override public int compareTo(Hand otherHand) {
		List<Card> thisHand = getHandValues().get(this);
		List<Card> thatHand = getHandValues().get(otherHand);
		if (thisHand == null || thatHand == null) {
		    
		    return 0;
		}
		if (thisHand.size() > thatHand.size()) {
			return 1;
		} else if (thisHand.size() < thatHand.size()) {
			return -1;
		}
		for (int i = thisHand.size() - 1; i >= 0; --i) {
			if (thisHand.get(i).getRank().ordinal() < thatHand.get(i).getRank().ordinal()) {
				return 1;
			} else if (thisHand.get(i).getRank().ordinal() > thatHand.get(i).getRank().ordinal()) {
				return -1;
			}
		}
		return 0;
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
	/**
	 * Return the playable cards in a hand.  Assumes that the hand is in sorted order.
	 */
	public List<Card> playableHand() {
		// 4 cards
		if (canPlay(cards)) {
			return cards;
		}

		// 3 cards
		List<Card> current = Lists.newArrayListWithExpectedSize(3);

		current = ImmutableList.of(cards.get(0), cards.get(1), cards.get(2));
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards.get(0), cards.get(1), cards.get(3));
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards.get(0), cards.get(2), cards.get(3));
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards.get(1), cards.get(2), cards.get(3));
		if (canPlay(current)) { return current; }
		
		// 2 cards
		current = ImmutableList.of(cards.get(0), cards.get(1));
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards.get(0), cards.get(2));
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards.get(1), cards.get(2));
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards.get(0), cards.get(3));
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards.get(1), cards.get(3));
		if (canPlay(current)) { return current; }
		current = ImmutableList.of(cards.get(2), cards.get(3));
		if (canPlay(current)) { return current; }

		// 1 card
		return ImmutableList.of(cards.get(0));
	}

	public List<Card> getCards() {
		return cards;
	}

	public double rank(Deck deck) {
//		long startTime = System.currentTimeMillis();
//		int count = 0;
		int wins = 0;
		int ties = 0;
		int losses = 0;
		for (int i = 0; i < deck.size(); ++i) {
			for (int j = i + 1; j < deck.size(); ++j) {
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
		/*
        	  System.out.println(toString());
        	  System.out.println("wins: " + wins);
        	  System.out.println("ties: " + ties);
        	  System.out.println("losses: " + losses);
        	  System.out.println("total: " + (wins + ties + losses));
		 */
		//	    System.out.println("time: " + (System.currentTimeMillis() - startTime));
		//	    return wins - losses;
		return (double) wins / (wins + ties + losses);
	}

	public boolean equals(Object other) {
		if (!(other instanceof Hand)) {
			return false;
		}

		Hand h = (Hand) other;
		return getCards().equals(h.getCards());
	}

	public int hashCode() {
		return getCards().hashCode();
	}

    /**
     * Create a new hand by keeping only the cards of the given indices, and
     * drawing the rest from the given deck.
     */
    public Hand draw(Deck d, List<Integer> keepers) {
		List<Card> newCards = Lists.newArrayListWithExpectedSize(4);
        for (int keepIndex : keepers) {
            newCards.add(cards.get(keepIndex));
        }
        while (newCards.size() < 4) {
            newCards.add(d.draw());
        }
        return new Hand(newCards);
    }
}
