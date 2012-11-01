import java.util.List;
interface Strategy<T> {
    List<Card> discard(Hand hand, T info);
}