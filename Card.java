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

	Rank rank;
	Suit suit;

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
}
