import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ReversiModel {
    // Двумерный массив. Возможные значения элементов:
    // 0 - клетка пустая
    // 1 - клетка занята черной фишкой
    // 2 - клетка занята белой фишкой
    final byte[][] array = new byte[8][8];

    // Переменные для текщего счета игры
    private byte blackScoreByte;
    private byte whiteScoreByte;

    // Флаг, указывающий, кто ходит (true - черные, false - белые)
    boolean fl;

    // Флаги, указывающие на невозможность игрока сделать следующий ход
    // Партия заканчивается, когда оба флага = true
    private static boolean impossibleNextStepBlack;
    private static boolean impossibleNextStepWhite;

    // Массив с элементами Pair
    // Храненит координаты клеток, подходящих для следующего хода
    // (эти клетки отдельно подсвечиваются на поле желтым)
    final List<Pair<Integer, Integer>> possibleMoves = new ArrayList<>();

    // Массив с элементами Pair
    // Храненит координаты клеток, которые нужно перерисовать
    final List<Pair<Integer, Integer>> repaintSquare = new ArrayList<>();

    // Инициализация всех параметров начальными значениями
    void initialValues() {
        repaintSquare.clear();

        // Инициализация array начальными значениями
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (i == 3 && j == 3)
                    array[i][j] = 2;
                else if (i == 4 && j == 4)
                    array[i][j] = 2;
                else if (i == 3 && j == 4)
                    array[i][j] = 1;
                else if (i == 4 && j == 3)
                    array[i][j] = 1;
                else array[i][j] = 0;

                repaintSquare.add(new Pair<>(i, j));
            }

        // Первыми ходят черные
        fl = true;

        // Ходы возможны
        impossibleNextStepBlack = false;
        impossibleNextStepWhite = false;

        // Начальный счет
        whiteScoreByte = 2;
        blackScoreByte = 2;
    }

    byte getWhiteScoreByte() { return whiteScoreByte; }

    byte getBlackScoreByte() { return blackScoreByte; }

    // Формирование массива possibleMoves со всеми возможными вариантами ходов в текущей ситуации
    void setPossibleMoves() {
        // Проверка на конец партии
        if (impossibleNextStepBlack && impossibleNextStepWhite) return;

        possibleMoves.clear();
        // Номер того игрока, который ходил в предыдущий раз
        byte blackOrWhite = fl ? (byte) 2 : 1;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (array[i][j] == blackOrWhite) {
                    if (j < 7) {
                        if (array[i][j + 1] == 0)
                            if (analyzeAction(i, j + 1, false, false))
                                possibleMoves.add(new Pair<>(i, j + 1));
                        if (i < 7)
                            if (array[i + 1][j + 1] == 0)
                                if (analyzeAction(i + 1, j + 1, false, false))
                                    possibleMoves.add(new Pair<>(i + 1, j + 1));
                        if (i > 0)
                            if (array[i - 1][j + 1] == 0)
                                if (analyzeAction(i - 1, j + 1, false, false))
                                    possibleMoves.add(new Pair<>(i - 1, j + 1));
                    }

                    if (j > 0) {
                        if (array[i][j - 1] == 0)
                            if (analyzeAction(i, j - 1, false, false))
                                possibleMoves.add(new Pair<>(i, j - 1));
                        if (i < 7)
                            if (array[i + 1][j - 1] == 0)
                                if (analyzeAction(i + 1, j - 1, false, false))
                                    possibleMoves.add(new Pair<>(i + 1, j - 1));
                        if (i > 0)
                            if (array[i - 1][j - 1] == 0)
                                if (analyzeAction(i - 1, j - 1, false, false))
                                    possibleMoves.add(new Pair<>(i - 1, j - 1));
                    }

                    if (i < 7)
                        if (array[i + 1][j] == 0)
                            if (analyzeAction(i + 1, j, false, false))
                                possibleMoves.add(new Pair<>(i + 1, j));

                    if (i > 0)
                        if (array[i - 1][j] == 0)
                            if (analyzeAction(i - 1, j, false, false))
                                possibleMoves.add(new Pair<>(i - 1, j));
                }

        // Проверка на невозможность сделать дальнейший ход
        if (possibleMoves.size() == 0) {
            if (fl)
                impossibleNextStepBlack = true;
            else
                impossibleNextStepWhite = true;

            fl = !fl;
            setPossibleMoves();
        }

        impossibleNextStepWhite = false;
        impossibleNextStepBlack = false;
    }

    // implement = false : проверяет, найдется ли непрерывный ряд фишек соперника
    // хотя бы по одному из 8 возможных направлений, если сходить в (i, j)
    // implement = true : делает ход в (i, j)
    // showPotential = true, когда нужно отметить фишки, которыми завладеет игрок,
    // если сделает ход в (i, j)
    boolean analyzeAction (int i, int j, boolean implement, boolean showPotential) {
        repaintSquare.clear();

        if (showPotential) repaintSquare.add(new Pair<>(i, j));

        // Номер игрока, который будет делать сейчас ход
        byte blackOrWhite = fl ? (byte) 1 : 2;

        // Проверка справа от (i, j)
        if (j != 7) {
            int newJ = j;
            int count = 1;
            while (newJ < 7) {
                newJ++;
                if (array[i][newJ] == 0) break;
                if (array[i][newJ] == blackOrWhite) {
                    if (count != 1) {
                        if (!implement && !showPotential) return true;

                        if (implement)
                            for (int z = j; z < newJ; z++) {
                                array[i][z] = blackOrWhite;
                                repaintSquare.add(new Pair<>(i, z));
                            }

                        if (showPotential)
                            for (int z = j + 1; z < newJ; z++)
                                repaintSquare.add(new Pair<>(i, z));
                    }

                    break;
                }
                count++;
            }
        }

        // Проверка слева от (i, j)
        if (j != 0) {
            int newJ = j;
            int count = 1;
            while (newJ > 0) {
                newJ--;
                if (array[i][newJ] == 0) break;
                if (array[i][newJ] == blackOrWhite) {
                    if (count != 1) {
                        if (!implement && !showPotential) return true;

                        if (implement)
                            for (int z = j; z > newJ; z--) {
                                array[i][z] = blackOrWhite;
                                repaintSquare.add(new Pair<>(i, z));
                            }


                        if (showPotential)
                            for (int z = j - 1; z > newJ; z--)
                                repaintSquare.add(new Pair<>(i, z));
                    }

                    break;
                }
                count++;
            }
        }

        // Проверка снизу от (i, j)
        if (i != 7) {
            int newI = i;
            int count = 1;
            while (newI < 7) {
                newI++;
                if (array[newI][j] == 0) break;
                if (array[newI][j] == blackOrWhite) {
                    if (count != 1) {
                        if (!implement && !showPotential) return true;

                        if (implement)
                            for (int p = i; p < newI; p++) {
                                array[p][j] = blackOrWhite;
                                repaintSquare.add(new Pair<>(p, j));
                            }

                        if (showPotential)
                            for (int p = i + 1; p < newI; p++)
                                repaintSquare.add(new Pair<>(p, j));
                    }

                    break;
                }

                count++;
            }
        }

        // Проверка сверху от (i, j)
        if (i != 0) {
            int newI = i;
            int count = 1;
            while (newI > 0) {
                newI--;
                if (array[newI][j] == 0) break;
                if (array[newI][j] == blackOrWhite){
                    if (count != 1) {
                        if (!implement && !showPotential) return true;

                        if (implement)
                            for (int p = i; p > newI; p--) {
                                array[p][j] = blackOrWhite;
                                repaintSquare.add(new Pair<>(p, j));
                            }

                        if (showPotential)
                            for (int p = i - 1; p > newI; p--)
                                repaintSquare.add(new Pair<>(p, j));
                    }

                    break;
                }

                count++;
            }
        }

        // Проверка снизу справа от (i, j)
        if (i != 7 && j != 7) {
            int newI = i;
            int newJ = j;
            int count = 1;
            while (newI < 7 && newJ < 7) {
                newI++;
                newJ++;
                if (array[newI][newJ] == 0) break;
                if (array[newI][newJ] == blackOrWhite) {
                    if (count != 1) {
                        if (!implement && !showPotential) return true;

                        if (implement) {
                            int p = i;
                            int z = j;
                            while(p < newI && z < newJ) {
                                array[p][z] = blackOrWhite;
                                repaintSquare.add(new Pair<>(p, z));
                                p++;
                                z++;
                            }
                        }

                        if (showPotential) {
                            int p = i + 1;
                            int z = j + 1;
                            while(p < newI && z < newJ) {
                                repaintSquare.add(new Pair<>(p, z));
                                p++;
                                z++;
                            }
                        }
                    }

                    break;
                }

                count++;
            }
        }

        // Проверка снизу слева от (i, j)
        if (i != 7 && j != 0) {
            int newI = i;
            int newJ = j;
            int count = 1;
            while (newI < 7 && newJ > 0) {
                newI++;
                newJ--;
                if (array[newI][newJ] == 0) break;
                if (array[newI][newJ] == blackOrWhite) {
                    if (count != 1) {
                        if (!implement && !showPotential) return true;

                        if (implement) {
                            int p = i;
                            int z = j;
                            while(p < newI && z > newJ) {
                                array[p][z] = blackOrWhite;
                                repaintSquare.add(new Pair<>(p, z));
                                p++;
                                z--;
                            }
                        }

                        if (showPotential) {
                            int p = i + 1;
                            int z = j - 1;
                            while(p < newI && z > newJ) {
                                repaintSquare.add(new Pair<>(p, z));
                                p++;
                                z--;
                            }
                        }
                    }

                    break;
                }

                count++;
            }
        }

        // Проверка сверху слева от (i, j)
        if (i != 0 && j != 0) {
            int newI = i;
            int newJ = j;
            int count = 1;
            while (newI > 0 && newJ > 0) {
                newI--;
                newJ--;
                if (array[newI][newJ] == 0) break;
                if (array[newI][newJ] == blackOrWhite) {
                    if (count != 1) {
                        if (!implement && !showPotential) return true;

                        if (implement) {
                            int p = i;
                            int z = j;
                            while(p > newI && z > newJ) {
                                array[p][z] = blackOrWhite;
                                repaintSquare.add(new Pair<>(p, z));
                                p--;
                                z--;
                            }
                        }

                        if (showPotential) {
                            int p = i - 1;
                            int z = j - 1;
                            while(p > newI && z > newJ) {
                                repaintSquare.add(new Pair<>(p, z));
                                p--;
                                z--;
                            }
                        }
                    }

                    break;
                }

                count++;
            }
        }

        // Проверка сверху справа от (i, j)
        if (i != 0 && j != 7) {
            int newI = i;
            int newJ = j;
            int count = 1;
            while (newI > 0 && newJ < 7) {
                newI--;
                newJ++;
                if (array[newI][newJ] == 0) break;
                if (array[newI][newJ] == blackOrWhite) {
                    if (count != 1) {
                        if (!implement && !showPotential) return true;


                        if (implement) {
                            int p = i;
                            int z = j;
                            while(p > newI && z < newJ) {
                                array[p][z] = blackOrWhite;
                                repaintSquare.add(new Pair<>(p, z));
                                p--;
                                z++;
                            }
                        }

                        if (showPotential) {
                            int p = i - 1;
                            int z = j + 1;
                            while(p > newI && z < newJ) {
                                repaintSquare.add(new Pair<>(p, z));
                                p--;
                                z++;
                            }
                        }
                    }

                    break;
                }
                count++;
            }
        }

        if (implement) {
            fl = !fl;
            updateScore();
        }

        return false;
    }

    // Обновление текущего счета
    private void updateScore() {
        blackScoreByte = 0;
        whiteScoreByte = 0;
        for (byte i = 0; i < 8; i++)
            for (byte j = 0; j < 8; j++) {
                if (array[i][j] == 1) blackScoreByte++;
                if (array[i][j] == 2) whiteScoreByte++;
            }
    }

}
