import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Lists;

class Test {
    public static void main(String[] argv) {
        Deck deck = new Deck();
        deck.shuffle();
        Hand hand = new Hand(deck);
        System.out.println("First hand: " + hand);
        Hand hand2 = hand.draw(deck, Lists.newArrayList(0));
        System.out.println("After draw: " + hand2);
        System.out.println("Improvement? " + hand2.compareTo(hand));
        
        for (int i = 0; i < 40; i++) {
        	deck.draw();
        }
        
        System.out.println(hand);
        System.out.println(deck);
        // TODO(ejwu): Figure out why 5 choose 4 is apparently 2880
        System.out.println(Distribution.generate(Collections.EMPTY_LIST, deck));
        if (true) return;
        Map<Collection<Card>, Distribution> distributions = Distribution.generateAllDistributions(hand, deck);
        for (Collection<Card> partialHand : distributions.keySet()) {
        	System.out.println(partialHand);
        	System.out.println(distributions.get(partialHand));
        	System.out.println("--------------");
        	
        }
    }
}
