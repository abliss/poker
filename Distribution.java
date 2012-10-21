import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultiset;

class Distribution {
	
	private Multiset<Hand> hands = TreeMultiset.create();
	
	public Distribution(Collection<Hand> hands) {
		this.hands.addAll(hands);
	};
	
	public void merge(Distribution d) {
		this.hands.addAll(d.getHands());
	}
	
	public Collection<Hand> getHands() {
		return Multisets.unmodifiableMultiset(hands);
	}
	
	/**
	 * Generate all possible hands that can be made by drawing to a partial hand from the given deck.
	 * Does not modify the deck or the hand.
	 */
	public static Distribution generate(Collection<Card> partialHand, Deck deck) {	    
	    if (partialHand.size() == 4) {
	    	return new Distribution(Lists.newArrayList(new Hand(partialHand)));
	    }
	    Multiset<Hand> possibleHands = HashMultiset.create();
	    for (int i = 0; i < 4 - partialHand.size(); i++) {
	    	for (Card c : deck.asList()) {
	    		Set<Card> partialPlusDraw = Sets.newHashSet(partialHand);
	    		partialPlusDraw.add(c);
	    		possibleHands.addAll(Distribution.generate(partialPlusDraw, deck.without(c)).getHands());
	    	}
	    }
	    return new Distribution(possibleHands);
	}
	
	public static Map<Collection<Card>, Distribution> generateAllDistributions(Hand hand, Deck deck) {
	    Map<Collection<Card>, Distribution> possibleDistributions = Maps.newHashMap();
	    // Discard 0
	    possibleDistributions.put(hand.getCards(), Distribution.generate(hand.getCards(), deck));
	    
		Collection<Card> tempHand;
		
	    // Discard 1
	    for (int i = 0; i < 4; i++) {
	    	tempHand = hand.without(hand.cardAt(i));
	    	possibleDistributions.put(tempHand, Distribution.generate(tempHand, deck));
	    }
	    
	    // Discard 2
	    for (int i = 0; i < 4; i++) {
	    	for (int j = 0; j < 4; j++) {
	    		tempHand = hand.without(hand.cardAt(i), hand.cardAt(j));
	    		possibleDistributions.put(tempHand, Distribution.generate(tempHand, deck));
	    	}
	    }
	    
	    // Discard 3
	    for (int i = 0; i < 4; i++) {
	    	tempHand = Lists.newArrayList(hand.cardAt(i));
	    	possibleDistributions.put(tempHand, Distribution.generate(tempHand, deck));
	    }
	    
	    // Discard 4
	    possibleDistributions.put(Collections.EMPTY_LIST,
	    	Distribution.generate(Collections.EMPTY_LIST, deck));
	    return possibleDistributions;
	}
	
	public String display(Collection<Hand> hands, int numBuckets) {
	    int partitionSize = Math.max(1, Math.round((float) hands.size() / numBuckets) - 1);
	    Iterable<List<Hand>> buckets = Iterables.partition(hands, partitionSize);
	    int bucketIndex = 0;
	    int elementIndex = 0;
	    StringBuilder sb = new StringBuilder();
	    sb.append(hands.size() + " elements");
	    for (Iterable<Hand> bucket : buckets) {
		sb.append("\n" + bucketIndex + "(" + elementIndex + ") : " + bucket.iterator().next());
            bucketIndex++;
            elementIndex += Iterables.size(bucket);
            if (bucketIndex == numBuckets + 1 ||
                numBuckets * partitionSize > hands.size() && bucketIndex == numBuckets) {
            	Hand hand = Iterables.getLast(bucket);
            	sb.append("\n" + bucketIndex + "(" + hands.size() + ") : " + hand);
            }
		}
		return sb.toString();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("All hands\n");
		sb.append(display(hands, 10));

		Map<Integer, Multiset<Hand>> detailedDistribution = Maps.newHashMap();
		for (int i = 1; i <= 4; i++) {
			detailedDistribution.put(i, TreeMultiset.<Hand>create());
		}
		for (Hand h : hands) {
			detailedDistribution.get(h.playableHand().size()).add(h);
		}
		
		for (int i = 2; i <= 4; i++) {
			sb.append("\nAll hands of size " + i);
			sb.append("\n" + display(detailedDistribution.get(i), 10));
		}
		
		return sb.toString();
	}
}
