import com.google.common.collect.Lists;

class Test {
    public static void main(String[] argv) {
        for (int i = 0; i < 20; i++) {
            Deck deck = new Deck().shuffle();
            Hand hand1 = new Hand(deck);
            System.out.println("First hand:  " + hand1);
            Hand hand2 = new Hand(deck);
            System.out.println("Second hand: " + hand2);
            Distribution dist1 = Distribution.generate(hand1.without(hand1.cardAt(3)), deck);
            Distribution dist2 = Distribution.generate(hand2.without(hand2.cardAt(3)), deck);
            System.out.println("Dist compare: " + dist1.compareTo(dist2));
        }
    }
}
