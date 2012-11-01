import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

class Test {
    
    public static void main(String[] argv) {
        Deck deck = new Deck().shuffle(new Random(0));
        for (int i = 0; i < 40; i++) {
            deck.draw();
        }
        List<Card> list = new ArrayList<Card>(3);
        System.out.println("____ DECK ____" + deck.asList());
        //list.add(deck.draw());
        //list.add(deck.draw());
        //list.add(deck.draw());
        //Hand.getHandValues();
        System.out.println("____ FLAT DISTRO ____" + new Date());
        System.out.println(Distribution.generate(list, deck));
        System.out.println("____  PAT DISTRO ____" + new Date());
        if (true) return;
        System.out.println(Strategies.getDistribution(Strategies.PatStrategy, null));
        System.out.println("____ " + new Date());

    }
}
