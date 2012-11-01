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
        for (int j = 0; j < 10; j++) {
            Deck deck = new Deck();
            deck.shuffle(new Random(j));
            for (int i = 0; i < 30; i++) {
                deck.draw();
            }
            System.out.println("____ DECK ____" + deck.asList());
            Hand h = new Hand(deck);
            System.out.println("____ HAND ____" + h);
            System.out.print("____ KEEP ____");
            System.out.println(Strategies.bestDraw(h, deck, null, null));
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
