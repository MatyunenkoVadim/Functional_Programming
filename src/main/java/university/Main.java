package university;

import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        Registrar reg = new Registrar();
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("""
                    1) Добавить студента
                    2) Добавить преподавателя
                    3) Создать курс
                    4) Назначить преподавателя на курс
                    5) Записать студента на курс
                    6) Отчислить студента с курса
                    7) Поставить оценку
                    8) Список курсов студента + GPA
                    9) Состав группы курса
                    10) Курсы преподавателя
                    11) Найти студента/курс по части имени
                    12) Удалить курс
                    13) Сохранить в JSON
                    14) Загрузить из JSON
                     0) Выход
                     >\s
                    """);

            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print("Имя студента: ");
                        var s = reg.addStudent(in.nextLine().trim());
                        System.out.println("Студент создан: " + s);
                    }

                    case "2" -> {
                        System.out.print("Имя преподавателя: ");
                        var p = reg.addProfessor(in.nextLine().trim());
                        System.out.println("Преподаватель создан: " + p);
                    }

                    case "3" -> {
                        System.out.print("Название курса: ");
                        String name = in.nextLine().trim();
                        System.out.print("Кредиты (целое): ");
                        int credits = Integer.parseInt(in.nextLine().trim());
                        var c = reg.addCourse(name, credits);
                        System.out.println("Курс создан: " + c);
                    }

                    case "4" -> {
                        System.out.print("ID курса: ");
                        UUID courseId = UUID.fromString(in.nextLine().trim());
                        System.out.print("ID преподавателя: ");
                        UUID profId = UUID.fromString(in.nextLine().trim());
                        reg.assignProfessor(courseId, profId);
                        System.out.println("Назначение выполнено.");
                    }

                    case "5" -> {
                        System.out.print("ID студента: ");
                        UUID studentId = UUID.fromString(in.nextLine().trim());
                        System.out.print("ID курса: ");
                        UUID courseId = UUID.fromString(in.nextLine().trim());
                        var enr = reg.enroll(studentId, courseId);
                        System.out.println("Запись выполнена: " + enr);
                    }

                    case "6" -> {
                        System.out.print("ID студента: ");
                        UUID studentId = UUID.fromString(in.nextLine().trim());
                        System.out.print("ID курса: ");
                        UUID courseId = UUID.fromString(in.nextLine().trim());
                        reg.drop(studentId, courseId);
                        System.out.println("Студент отчислен с курса.");
                    }

                    case "7" -> {
                        System.out.print("ID студента: ");
                        UUID studentId = UUID.fromString(in.nextLine().trim());
                        System.out.print("ID курса: ");
                        UUID courseId = UUID.fromString(in.nextLine().trim());
                        System.out.print("Оценка [A,B,C,D,F]: ");
                        Grade g = Grade.valueOf(in.nextLine().trim().toUpperCase());
                        reg.grade(studentId, courseId, g);
                        System.out.println("Оценка выставлена.");
                    }

                    case "8" -> {
                        System.out.print("ID студента: ");
                        UUID studentId = UUID.fromString(in.nextLine().trim());
                        reg.printTranscript(studentId);
                    }

                    case "9" -> {
                        System.out.print("ID курса: ");
                        UUID courseId = UUID.fromString(in.nextLine().trim());
                        reg.printRoster(courseId);
                    }

                    case "10" -> {
                        System.out.print("ID преподавателя: ");
                        UUID profId = UUID.fromString(in.nextLine().trim());
                        reg.printProfessorCourses(profId);
                    }

                    case "11" -> {
                        System.out.print("Часть имени: ");
                        String q = in.nextLine().trim();
                        reg.search(q);
                    }

                    case "12" -> {
                        System.out.print("ID курса: ");
                        UUID courseId = UUID.fromString(in.nextLine().trim());
                        reg.removeCourse(courseId);
                        System.out.println("Курс удалён.");
                    }

                    case "13" -> {
                        System.out.print("Путь к файлу (например, data.json): ");
                        var p = java.nio.file.Path.of(in.nextLine().trim());
                        reg.saveToJson(p);
                        System.out.println("Сохранено: " + p.toAbsolutePath());
                    }

                    case "14" -> {
                        System.out.print("Путь к файлу: ");
                        var p = java.nio.file.Path.of(in.nextLine().trim());
                        reg.loadFromJson(p);
                        System.out.println("Загружено из: " + p.toAbsolutePath());
                    }

                    case "0" -> {
                        System.out.println("Чао-какао!");
                        return;
                    }

                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }
}
