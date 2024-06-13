package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
                new Student("Student1", Map.of("Math", 90, "Physics", 85)),
                new Student("Student2", Map.of("Math", 95, "Physics", 88)),
                new Student("Student3", Map.of("Math", 88, "Chemistry", 92)),
                new Student("Student4", Map.of("Physics", 78, "Chemistry", 85))
        );

        Map<String, Double> averageGradesBySubject = getAverageGradesBySubject(students);

        // Выводим результат
        System.out.println("Средние оценки по предметам:");
        averageGradesBySubject.forEach((subject, averageGrade) -> {
            System.out.printf("%s: %.2f%n", subject, averageGrade);
        });
    }

    public static Map<String, Double> getAverageGradesBySubject(List<Student> students) {
        Function<Map.Entry<String, Integer>, Double> averagingFunction = entry -> {
            List<Integer> grades = students.stream()
                    .flatMap(student -> student.getGrades().entrySet().stream())
                    .filter(e -> e.getKey().equals(entry.getKey()))
                    .mapToInt(Map.Entry::getValue)
                    .boxed()
                    .collect(Collectors.toList());

            return grades.stream()
                    .mapToDouble(Integer::doubleValue)
                    .average()
                    .orElse(0.0);
        };

        // Используем Parallel Stream для обработки данных
        return students.parallelStream()
                .flatMap(student -> student.getGrades().entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        averagingFunction,
                        (oldValue, newValue) -> (oldValue + newValue) / 2.0 // Объединяем значения для дубликатов
                ));
    }
}