import java.util.List;
import java.util.Iterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

class Card implements Comparable {
	public enum Suit {
		CLUB {public String toString() { return "c";}},
		DIAMOND {public String toString() {return "d";}},
		HEART {public String toString() {return "h";}},
		SPADE {public String toString() {return "s";}};
	}

	public enum Rank {
		ACE {public String toString() { return "A";}},
		TWO {public String toString() { return "2";}},
		THREE {public String toString() {return "3";}},
		FOUR {public String toString() { return "4";}},
		FIVE {public String toString() { return "5";}},
		SIX {public String toString() {return "6";}},
		SEVEN {public String toString() { return "7";}},
		EIGHT {public String toString() { return "8";}},
		NINE {public String toString() {return "9";}},
		TEN {public String toString() { return "T";}},
		JACK {public String toString() { return "J";}},
		QUEEN {public String toString() {return "Q";}},
		KING {public String toString() { return "K";}};
	}

	private final Rank rank;
	private final Suit suit;

	Card(Rank r, Suit s) {
		this.rank = r;
		this.suit = s;
	}

	public Rank getRank() {
		return rank;
	}

	public Suit getSuit() {
		return suit;
	}

	@Override
	public String toString() {
		return rank.toString() + suit.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Card)) {
			return false;
		}
		Card card = (Card) other;
		return this.rank == card.getRank() && this.suit == card.getSuit();
	}

	@Override
	public int hashCode() {
		return 31 * rank.hashCode() + suit.hashCode();
	}

	public int compareTo(Object otherCard) {
		if (!(otherCard instanceof Card)) {
			throw new RuntimeException("Can't compare Card to " + otherCard);
		}
		Card other = (Card) otherCard;
		if (this.rank.ordinal() != other.getRank().ordinal()) {
			return this.rank.ordinal() - other.getRank().ordinal();
		}
		return this.suit.ordinal() - other.getSuit().ordinal();
	}

    /**
     * Returns the given list of cards, but with suit abstracted out. i.e.:
     * <ol>
     * <li>for all i and j, out[i].suit == out[j].suit iff in[i].suit == in[j].suit, and</li>
     * <li>if you scan forward in the list, noting each suit the first time it appears, the suits 
     *     you note will be in ascending order.</li>
     * </ol>
     *
     * this guarantees that if two cardlists are isomorphic modulo suit
     * permutations, their suitified versions will be identical.
     */
    public static List<Card> suitify(List<Card> in) {
        List<Card> out = Lists.newArrayListWithExpectedSize(in.size());
        int unusedSuit = 0;
        for (Card inCard : in) {
            Suit outSuit = null;
            for (int i = 0; i < out.size(); i++) {
                if (inCard.getSuit() == in.get(i).getSuit()) {
                    outSuit = out.get(i).getSuit();
                    break;
                }
            }
            if (outSuit == null) {
                outSuit = Suit.values()[unusedSuit++];
            }
            out.add(new Card(inCard.getRank(), outSuit));
        }
        return out;
    }

    /**
     * Suitify multiple lists of cards, using the same transformation on each.
     */
    public static List<List<Card>> multiSuitify(List<List<Card>> inputLists) {
	List<Card> suitified = suitify(Lists.newArrayList(Iterables.concat(inputLists)));
	List<List<Card>> allOutput = Lists.newArrayListWithExpectedSize(inputLists.size());
	int index = 0;
	for (List<Card> inputList : inputLists) {
	    List<Card> outputList = Lists.newArrayListWithExpectedSize(inputList.size());
	    outputList.addAll(suitified.subList(index, index + inputList.size()));
	    allOutput.add(outputList);
	    index = index + inputList.size();
	}
	return allOutput;
    }
    /*
    public static void testMultiSuitify() {
	List<Card> hand = Lists.newArrayList(new Card(Rank.ACE, Suit.SPADE), new Card(Rank.TWO, Suit.HEART),
					     new Card(Rank.THREE, Suit.DIAMOND), new Card(Rank.FOUR, Suit.CLUB));
	List<Card> hand2 = Lists.newArrayList(new Card(Rank.FIVE, Suit.CLUB), new Card(Rank.SIX, Suit.SPADE),
					     new Card(Rank.SEVEN, Suit.HEART), new Card(Rank.EIGHT, Suit.DIAMOND));
	System.out.println(hand);
	System.out.println(hand2);
	
	System.out.println(multiSuitify(Lists.newArrayList(hand, hand2)));	
    }
    */
}
