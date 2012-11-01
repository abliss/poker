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
        Deck d = new Deck();
        d.shuffle(new Random(0));
        Hand h = new Hand(d);
        Strategies.bestDraw(h, d, null, null);

            /*
        //System.out.println("____ DECK ____" + deck.asList());
        System.out.println("____ FAST DISTRO ____" + new Date());
        System.out.println(Distribution.generatePat());
        System.out.println("____ FLAT DISTRO ____" + new Date());
        System.out.println(Distribution.generate(list, deck));
        System.out.println("____  PAT DISTRO ____" + new Date());
        System.out.println(Strategies.getDistribution(Strategies.PatStrategy, null));
        System.out.println("____ " + new Date());
            */
    }
}
