import java.util.List;
import java.util.Iterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public enum Card {
        CAC(Rank.ACE,Suit.CLUB), CAD(Rank.ACE,Suit.DIAMOND), CAH(Rank.ACE,Suit.HEART), CAS(Rank.ACE,Suit.SPADE),
        C2C(Rank.TWO,Suit.CLUB), C2D(Rank.TWO,Suit.DIAMOND), C2H(Rank.TWO,Suit.HEART), C2S(Rank.TWO,Suit.SPADE),
        C3C(Rank.THREE,Suit.CLUB), C3D(Rank.THREE,Suit.DIAMOND), C3H(Rank.THREE,Suit.HEART), C3S(Rank.THREE,Suit.SPADE),
        C4C(Rank.FOUR,Suit.CLUB), C4D(Rank.FOUR,Suit.DIAMOND), C4H(Rank.FOUR,Suit.HEART), C4S(Rank.FOUR,Suit.SPADE),
        C5C(Rank.FIVE,Suit.CLUB), C5D(Rank.FIVE,Suit.DIAMOND), C5H(Rank.FIVE,Suit.HEART), C5S(Rank.FIVE,Suit.SPADE),
        C6C(Rank.SIX,Suit.CLUB), C6D(Rank.SIX,Suit.DIAMOND), C6H(Rank.SIX,Suit.HEART), C6S(Rank.SIX,Suit.SPADE),
        C7C(Rank.SEVEN,Suit.CLUB), C7D(Rank.SEVEN,Suit.DIAMOND), C7H(Rank.SEVEN,Suit.HEART), C7S(Rank.SEVEN,Suit.SPADE),
        C8C(Rank.EIGHT,Suit.CLUB), C8D(Rank.EIGHT,Suit.DIAMOND), C8H(Rank.EIGHT,Suit.HEART), C8S(Rank.EIGHT,Suit.SPADE),
        C9C(Rank.NINE,Suit.CLUB), C9D(Rank.NINE,Suit.DIAMOND), C9H(Rank.NINE,Suit.HEART), C9S(Rank.NINE,Suit.SPADE),
        CTC(Rank.TEN,Suit.CLUB), CTD(Rank.TEN,Suit.DIAMOND), CTH(Rank.TEN,Suit.HEART), CTS(Rank.TEN,Suit.SPADE),
        CJC(Rank.JACK,Suit.CLUB), CJD(Rank.JACK,Suit.DIAMOND), CJH(Rank.JACK,Suit.HEART), CJS(Rank.JACK,Suit.SPADE),
        CQC(Rank.QUEEN,Suit.CLUB), CQD(Rank.QUEEN,Suit.DIAMOND), CQH(Rank.QUEEN,Suit.HEART), CQS(Rank.QUEEN,Suit.SPADE),
        CKC(Rank.KING,Suit.CLUB), CKD(Rank.KING,Suit.DIAMOND), CKH(Rank.KING,Suit.HEART), CKS(Rank.KING,Suit.SPADE);
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
        private static final Card rankSuit[][] = new Card[13][4];
        static {
            for (Card c : Card.values()) {
                rankSuit[c.rank.ordinal()][c.suit.ordinal()] = c;
            }
        }
        Card(Rank rank, Suit suit) {
            this.rank = rank;
            this.suit = suit;
        }
        public String toString() {return "" + rank + suit;}
        public Rank getRank() { return rank; }
        public Suit getSuit() { return suit; }
        public static Card from(Rank rank, Suit suit) {
            return rankSuit[rank.ordinal()][suit.ordinal()];
        }
        
        private final static Card[] CARD_VALUES = values();
        private final static Suit[] SUIT_VALUES = Suit.values();
        private final static Rank[] RANK_VALUES = Rank.values();
    /**
     * Returns the given list of cards, but with suit abstracted out. i.e.:
     * <ol>
     * <li>for all i and j, out[i].suit == out[j].suit iff in[i].suit == in[j].suit, and</li>
     * <li>if any suit-swapping isomorphism maps in1 to in2, then suitify(in1) will equal suitify(in2).</li>
     * </ol>
     *
     * In other words, this guarantees that if two cardlists are isomorphic
     * modulo suit permutations, their suitified versions will be identical.
     */
    public static Card[] suitify(Card[] in) {
        int len = in.length;
        Card[] out = new Card[len];
        int unusedSuit = 0;
        for (int i = 0; i < len; i++) {
            Card inCard = in[i];
            Suit outSuit = null;
            for (int j = 0; j < i; j++) {
                if (in[j].getSuit() == in[i].getSuit()) {
                    outSuit = out[j].getSuit();
                    break;
                }
            }
            if (outSuit == null) {
                outSuit = SUIT_VALUES[unusedSuit++];
            }
            out[i] = Card.from(inCard.getRank(), outSuit);
        }
        return out;
    }

    /**
     * Suitify multiple lists of cards, using the same transformation on each.
     */
    /*
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
    */
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
