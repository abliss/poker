import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;


class Deck {
	List<Card> cards;

	Deck() {
		cards = Lists.newArrayListWithExpectedSize(52);
		for (Card.Rank r : Card.Rank.values()) {
			for (Card.Suit s : Card.Suit.values()) {
				cards.add(new Card(r, s));
			}
		}
	}

	Deck(List<Card> cards) {
		this.cards = Lists.newArrayListWithExpectedSize(cards.size());
		for (Card c : cards) {
			this.cards.add(c);
		}
	}

	public Card remove(Card c) {
		if (cards.contains(c)) {
			cards.remove(c);
			return c;
		} else {
			throw new IllegalArgumentException("Deck doesn't contain " + c);
		}
	}
	
	public Card getCard(Card.Rank rank, Card.Suit suit) {
		return this.remove(new Card(rank, suit));
	}

	/**
	 * Return a copy of this deck, without the specified card.
	 */
	public Deck without(Card c) {
		Deck d = new Deck(cards);
		d.remove(c);
		return d;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Card c : cards) {
			s.append(c.toString() + "\n");
		}
		return s.toString();
	}

	public int size() {
		return cards.size();
	}

	public List<Card> peek(int numCards) {
		List<Card> peeked = Lists.newArrayListWithExpectedSize(numCards);
		for (int i = 0; i < numCards; i++) {
			peeked.add(cards.get(i));
		}
		return peeked;
	}
	
	public Card draw() {
		Card c = cards.get(0);
		this.remove(c);
		return c;
	}

	public List<Card> draw(int numCards) {
		List<Card> drawn = Lists.newArrayListWithExpectedSize(numCards);
		for (int i = 0; i < numCards; ++i) {
			Card c = cards.get(0);
			this.remove(c);
			drawn.add(c);
		}
		return drawn;
	}

	public List<Card> cardsAt(int i, int j, int k, int l) {
		List<Card> list = Lists.newArrayListWithExpectedSize(4);
		list.add(cards.get(i));
		list.add(cards.get(j));
		list.add(cards.get(k));
		list.add(cards.get(l));
		return list;
	}

	public List<Card> deal(int numCards) {
		List<Card> dealt = cards.subList(0, numCards);
		cards = cards.subList(numCards, cards.size());
		return dealt;
	}
	public List<Card> asList() {
		return ImmutableList.copyOf(cards);
	}

	public Deck shuffle() {
		Collections.shuffle(cards);
		return this;
	}

}
