import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String castle = "\uD83C\uDFF0";

        int sizeBoard = 5;
        int step = 0;
        int score = 0;

        Person person = new Person(sizeBoard);

        Random random = new Random();
        int castleX = 1 + random.nextInt(sizeBoard);
        int castleY = 1;

        String[][] board = new String[sizeBoard][sizeBoard];
        for (int y = 1; y <= sizeBoard; y++) {
            for (int x = 1; x <= sizeBoard; x++) {
                board[y - 1][x - 1] = "  ";
            }
        }

        int countMonster = sizeBoard * sizeBoard - sizeBoard - 3;
        Monster[] monsters = new Monster[countMonster];
        int count = 0;

        while (count < countMonster) {
            Monster monster;

            if (random.nextBoolean()) {
                monster = new Monster(sizeBoard);
            } else {
                monster = new BigMonster(sizeBoard);
            }

            int row = monster.getY() - 1;
            int column = monster.getX() - 1;

            if (board[row][column].equals("  ")) {
                board[row][column] = monster.getImage();
                monsters[count] = monster;
                count++;
            }
        }

        board[castleY - 1][castleX - 1] = castle;

        String potion = "\uD83E\uDDEA";
        int potions = 2;
        while (potions > 0) {
            int px = 1 + random.nextInt(sizeBoard);
            int py = 1 + random.nextInt(sizeBoard);
            boolean isPersonCell = px == person.getX() && py == person.getY();
            if (board[py - 1][px - 1].equals("  ") && !isPersonCell) {
                board[py - 1][px - 1] = potion;
                potions--;
            }
        }

        System.out.println("Привет! Ты готов начать играть в игру? (Напиши: ДА или НЕТ)");

        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();

        System.out.println("Ваш ответ:\t" + answer);

        switch (answer) {
            case "да":
            case "Да":
            case "дА":
            case "ДА":
                System.out.println("Выбери сложность игры (от 1 до 5):");
                int difficultGame = scanner.nextInt();
                System.out.println("Выбранная сложность:\t" + difficultGame);

                while (true) {
                    board[person.getY() - 1][person.getX() - 1] = person.getImage();
                    outputBoard(board, person.getLive(), person.getX(), person.getY());

                    System.out.println("Введите, куда будет ходить персонаж "
                            + "(ход возможен только по вертикали или горизонтали на одну клетку)"
                            + "\nКоординаты персонажа — (x: " + person.getX()
                            + ", y: " + person.getY() + ")");

                    int x = scanner.nextInt();
                    int y = scanner.nextInt();
                    System.out.println(x + ", " + y);

                    if (person.isMoveCorrect(x, y)) {
                        if (board[y - 1][x - 1].equals("  ")) {
                            board[person.getY() - 1][person.getX() - 1] = "  ";
                            person.move(x, y);
                            step++;

                            System.out.println("Ход корректный; новые координаты: "
                                    + person.getX() + ", " + person.getY()
                                    + "\nХод номер: " + step);
                        } else if (board[y - 1][x - 1].equals(potion)) {
                            board[person.getY() - 1][person.getX() - 1] = "  ";
                            person.move(x, y);
                            step++;
                            score += 10 * difficultGame;
                            person.addLive();
                            System.out.println("Вы нашли зелье! +1 жизнь");
                        } else if (board[y - 1][x - 1].equals(castle)) {
                            System.out.println("Вы прошли игру!\nКоличество ходов: "+step+". Количество очков: "+score);
                            break;
                        } else {
                            for (Monster monster : monsters) {
                                if (monster.conflictPerson(x, y)) {
                                    if (monster.taskMonster(difficultGame)) {
                                        board[person.getY() - 1][person.getX() - 1] = "  ";
                                        person.move(x, y);
                                        step++;
                                        score += 10 * difficultGame;
                                    } else {
                                        person.downLive();
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        System.out.println("Некорректный ход");
                    }

                    if (person.getLive() <= 0) {
                        break;
                    }
                }

                if (person.getLive() <= 0) {
                    System.out.println("Закончились жизни. Вы проиграли.\nКоличество ходов: "+step+". Количество очков: "+score);
                }
                break;

            case "НЕТ":
            case "нет":
            case "Нет":
                System.out.println("Жаль, приходи ещё!");
                break;

            default:
                System.out.println("Данные введены некорректно");
                break;
        }
    }

    static void outputBoard(String[][] board, int live, int px, int py) {
        String wall = "+ —— + —— + —— + —— + —— +";

        for (int y = 1; y <= board.length; y++) {
            System.out.println(wall);
            for (int x = 1; x <= board.length; x++) {
                boolean near = Math.abs(x - px) + Math.abs(y - py) <= 1;
                if (near) {
                    System.out.print("| " + board[y - 1][x - 1] + " ");
                } else {
                    System.out.print("| ░░ ");
                }
            }
            System.out.println("|");
        }
        System.out.println(wall);

        System.out.println("Количество жизней:\t" + live);

    }
}