import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.*;


class Deck implements Iterable<Card> {

	EnumSet<Card> cards;

	Deck() {
		cards = EnumSet.allOf(Card.class);
	}

	Deck(EnumSet<Card> cards) {
		this.cards = cards.clone();
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
		return this.remove(Card.from(rank, suit));
	}

	/**
	 * Return a copy of this deck, without the specified card.
	 */
	public Deck without(Card c) {
		Deck d = new Deck(cards);
		d.remove(c);
		return d;
	}

	public Deck without(Collection<Card> c) {
		Deck d = new Deck(cards);
		d.cards.removeAll(c);
		return d;
	}

	public String toString() {
        return cards.toString();
	}

	public int size() {
		return cards.size();
	}

    /*
	public List<Card> peek(int numCards) {
		List<Card> peeked = Lists.newArrayListWithExpectedSize(numCards);
		for (int i = 0; i < numCards; i++) {
			peeked.add(cardAt(i));
		}
		return peeked;
	}
    */

	public Card draw() {
		Card c = cardAt(0);
		this.remove(c);
		return c;
	}

	public List<Card> draw(int numCards) {
		List<Card> drawn = Lists.newArrayListWithExpectedSize(numCards);
		for (int i = 0; i < numCards; ++i) {
			Card c = cardAt(0);
			this.remove(c);
			drawn.add(c);
		}
		return drawn;
	}
    /*
	public List<Card> cardsAt(int i, int j, int k, int l) {
		List<Card> list = Lists.newArrayListWithExpectedSize(4);
		list.add(cardAt(i));
		list.add(cardAt(j));
		list.add(cardAt(k));
		list.add(cardAt(l));
		return list;
	}
    */
    /*
	public List<Card> deal(int numCards) {
		List<Card> dealt = cards.subList(0, numCards);
		cards = cards.subList(numCards, cards.size());
		return dealt;
	}
    */
	public Deck shuffle() {
        //TODO: XXX
		//Collections.shuffle(cards);
		return this;
	}
	public Deck shuffle(Random rand) {
        // TODO: XXX
		//Collections.shuffle(cards, rand);
		return this;
	}

    /**
     * All possible playable 4-card hands that this deck can deal, with
     * frequency counts.  (Each Hand is an unspecified representative of all the
     * equally-playable hands.)
     */
    public int[] allPlayableRanks() {
        int[] counts = new int[1092];
        for (Integer hash : handHashCodes()) {
            Hand h = Hand.fromHashCode(hash);
            counts[h.playableRank()]++;
        }
        return counts;
    }

    public Card cardAt(int i) {
        int index = 0;
        for (Card c : cards) {
            if (index++ == i) {
                return c;
            }
        }
        throw new RuntimeException("Deck index out of bounds: " + i + " >= " + cards.size());
    }

    public Iterator<Card> iterator() {
        return cards.iterator();
    }

    public Collection<List<Card>> subsetsOfSize2() {
        List<List<Card>> sets = Lists.newArrayListWithExpectedSize(1326);
        Card[] cardArr = cards.toArray(new Card[0]);
        int len = cardArr.length;
        for (int i = 0; i < len - 1; i++) {
            for (int j = i + 1; j < len; j++) {
                sets.add(Lists.newArrayList(cardArr[i], cardArr[j]));
            }
        }
        return sets;
    }
    public Collection<List<Card>> subsetsOfSize3() {
        List<List<Card>> sets = Lists.newArrayListWithExpectedSize(22100);
        Card[] cardArr = cards.toArray(new Card[0]);
        int len = cardArr.length;
        for (int i = 0; i < len - 2; i++) {
            for (int j = i + 1; j < len - 1; j++) {
                for (int k = j + 1; k < len; k++) {
                    sets.add(Lists.newArrayList(cardArr[i], cardArr[j], cardArr[k]));
                }
            }
        }
        return sets;
    }
    
    /**
     * Guarantees each List will be sorted.
     */
    public Collection<List<Card>> subsetsOfSize4() {
        List<List<Card>> sets = Lists.newArrayListWithExpectedSize(270725);
        Card[] cardArr = cards.toArray(new Card[0]);
        int len = cardArr.length;
        for (int i = 0; i < len - 3; i++) {
            for (int j = i + 1; j < len - 2; j++) {
                for (int k = j + 1; k < len - 1; k++) {
                    for (int l = k + 1; l < len; l++) {
                        sets.add(Lists.newArrayList(cardArr[i], cardArr[j], cardArr[k], cardArr[l]));
                    }
                }
            }
        }
        return sets;
    }

    /**
     * Doesn't actually construct all four-card hands, but computes their hashcodes.
     */
    public Collection<Integer> handHashCodes() {
        List<Integer> hashes = Lists.newArrayListWithExpectedSize(270725);
        Card[] cardArr = cards.toArray(new Card[0]);
        int len = cardArr.length;
        for (int i = 0; i < len - 3; i++) {
            for (int j = i + 1; j < len - 2; j++) {
                for (int k = j + 1; k < len - 1; k++) {
                    for (int l = k + 1; l < len; l++) {
                        hashes.add(Hand.hashCode(cardArr[i], cardArr[j], cardArr[k], cardArr[l]));
                    }
                }
            }
        }
        return hashes;
    }

}
