import com.google.common.collect.Lists;

class Test {
    public static void main(String[] argv) {
        Deck deck = new Deck();
        Hand hand = new Hand(deck);
        System.out.println("First hand: " + hand);
        Hand hand2 = hand.draw(deck, Lists.newArrayList(0));
        System.out.println("After draw: " + hand2);
        System.out.println("Improvement? " + hand2.compareTo(hand));
    }
}
