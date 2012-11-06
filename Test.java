import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

class Test {
    public static long START = System.currentTimeMillis();
    public static void main(String[] argv) {
        int seed = 0;
        try {
            seed = Integer.parseInt(argv[0]);
        } catch (RuntimeException e) {
            System.err.println("Usage: java Test random_seed");
            return;
        }
        System.out.println("Starting with seed " + seed + " at " + new Date());
        for (int j = 0; j < 1; j++) {
            Deck deck = new Deck();
            //deck.shuffle(new Random(seed + j));
            System.out.println("____ DECK ____" + deck);
            Hand h = new Hand(Card.CAC, Card.C2D, Card.CKH, Card.CQC);
            System.out.println("____ HAND ____" + h);
            Collection<Card> keep = Strategies.bestDraw(h, deck.without(h.getCards()), null, null);
            System.out.print("____ KEEP ____");
            System.out.println(keep);
            h = new Hand(Card.CAC, Card.C2D, Card.CKH, Card.C3C);
            System.out.println("____ HAND ____" + h);
            keep = Strategies.bestDraw(h, deck.without(h.getCards()), null, null);
            System.out.print("____ KEEP ____");
            System.out.println(keep);
        }
            /*
        //System.out.println("____ DECK ____" + deck.asList());
        System.out.println("____ FAST DISTRO ____" + (System.currentTimeMillis() - START)));
        System.out.println(Distribution.generatePat());
        System.out.println("____ FLAT DISTRO ____" + System.currentTimeMillis());
        System.out.println(Distribution.generate(list, deck));
        System.out.println("____  PAT DISTRO ____" + System.currentTimeMillis());
        System.out.println(Strategies.getDistribution(Strategies.PatStrategy, null));
        System.out.println("____ " + System.currentTimeMillis());
            */
    }
}
