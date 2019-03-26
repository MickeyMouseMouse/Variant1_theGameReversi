import javafx.util.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class Tests {
    private ReversiModel model = new ReversiModel();

    @Test
    public void test1() {
        model.initialValues();
        model.setPossibleMoves();

        Set<Pair<Integer, Integer>> rightAnswer = new HashSet<>();
        rightAnswer.add(new Pair(2, 3));
        rightAnswer.add(new Pair(3, 2));
        rightAnswer.add(new Pair(4, 5));
        rightAnswer.add(new Pair(5, 4));

        assertEquals(rightAnswer, model.getPossibleMoves());
    }

    @Test
    public void test2() {
        model.initialValues();
        assertEquals(false,
                model.analyzeAction(0, 0, false, false));
    }

    @Test
    public void test3() {
        model.initialValues();
        assertEquals(true,
                model.analyzeAction(3, 2, false, false));
    }

    @Test
    public void test4() {
        model.initialValues();
        model.analyzeAction(2, 3, false, true);

        List<Pair<Integer, Integer>> rightAnswer = new ArrayList<>();
        rightAnswer.add(new Pair(2, 3));
        rightAnswer.add(new Pair(3, 3));

        assertEquals(rightAnswer, model.getRepaintSquare());
    }

    @Test
    public void test5() {
        model.initialValues();
        model.analyzeAction(2, 3, true, false);

        List<Pair<Integer, Integer>> rightAnswer = new ArrayList<>();
        rightAnswer.add(new Pair(2, 3));
        rightAnswer.add(new Pair(3, 3));

        assertEquals(rightAnswer, model.getRepaintSquare());
        assertEquals(4, model.getBlackScoreByte());
        assertEquals(1, model.getWhiteScoreByte());
        assertEquals(false, model.getFl());
    }
}
