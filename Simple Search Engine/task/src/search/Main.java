package search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    private static ArrayList<String> people = new ArrayList<>();
    private static final Map<String, List<Integer>> index = new HashMap<>();
    private static final String NO_ONE_FOUND = "No one found";

    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            String fileName = args[1];
            Path path = Paths.get(fileName);
            people = (ArrayList<String>) Files.readAllLines(path);
            createIndex();
        }

        Scanner scanner = new Scanner(System.in);
        boolean isExit = false;
        while (!isExit) {
            printMenu();
            int menuAction = Integer.parseInt(scanner.nextLine());
            isExit = handleMenuAction(menuAction);
        }
        System.out.println("Bye!");
    }

    private static void createIndex() {
        for (int i = 0; i < people.size(); i++) {
            for (String word : people.get(i).split(" ")) {
                String lowWord = word.toLowerCase();
                if (index.get(lowWord) == null) {
                    var numberOfLines = new ArrayList<Integer>();
                    numberOfLines.add(i);
                    index.put(lowWord, numberOfLines);
                } else {
                    index.get(lowWord).add(i);
                }
            }
        }
    }

    private static boolean handleMenuAction(int menuAction) {
        boolean isExit = false;
        switch (menuAction) {
            case 1:
                Strategies strategy = chooseStrategy();
                findPeople(strategy);
                break;
            case 2:
                printAllPeople();
                break;
            case 0:
                isExit = true;
                break;
            default:
                System.out.println("Incorrect option! Try again.");
        }

        return isExit;
    }

    private static Strategies chooseStrategy() {
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        switch (input) {
            case "ANY":
                return Strategies.ANY;
            case "ALL":
                return Strategies.ALL;
            case "NONE":
                return Strategies.NONE;
            default:
                System.out.println("Wrong strategy. Choosing ALL");
                return Strategies.ALL;
        }
    }

    private static void printAllPeople() {
        System.out.println("=== List of people ===");
        people.forEach(System.out::println);
    }

    private static void printMenu() {
        System.out.println("=== Menu ===");
        System.out.println("1. Find a person");
        System.out.println("2. Print all people");
        System.out.println("0. Exit");
    }

    private static void findPeople(Strategies strategy) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a name or email to search all suitable people:");
        String searchData = scanner.nextLine().toLowerCase();
        switch (strategy) {
            case ALL:
                findAllSuitablePeople(searchData);
                break;
            case ANY:
                findAny(searchData);
                break;
            case NONE:
                findNone(searchData);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static void findNone(String searchData) {
        Set<Integer> searchIndexResult = new HashSet<>();
        Arrays.stream(searchData.split("\\s+"))
                .filter(index::containsKey)
                .map(index::get)
                .forEach(searchIndexResult::addAll);

        Set<Integer> allIndexes = IntStream.range(0, people.size())
                .boxed().collect(Collectors.toSet());
        allIndexes.removeAll(searchIndexResult);
        if (allIndexes.isEmpty()) {
            System.out.println(NO_ONE_FOUND);
        } else {
            allIndexes.stream()
                    .map(index -> people.get(index))
                    .forEach(System.out::println);
        }
    }

    private static void findAllSuitablePeople(String searchData) {
        Set<Integer> searchIndexResult = new HashSet<>();
        for (String s : searchData.split("\\s+")) {
            if (index.containsKey(s)) {
                List<Integer> integers = index.get(s);
                searchIndexResult.addAll(integers);
            } else {
                System.out.println(NO_ONE_FOUND);
                return;
            }
        }

        List<String> result = new ArrayList<>();
        for (Integer integer : searchIndexResult) {
            boolean containsAll = Arrays.asList(people.get(integer).split("\\s+"))
                    .containsAll(Arrays.asList(searchData.split("\\s+")));
            if (containsAll) {
                result.add(people.get(integer));
            }
        }

        if (result.isEmpty()) {
            System.out.println(NO_ONE_FOUND);
        } else {
            System.out.printf("%d persons found:", result.size());
            result.forEach(System.out::println);
        }

    }

    private static void findAny(String searchData) {
        Set<Integer> searchIndexResult = new HashSet<>();
        Arrays.stream(searchData.split("\\s+"))
                .filter(index::containsKey)
                .map(index::get)
                .forEach(searchIndexResult::addAll);
        if (!searchIndexResult.isEmpty()) {
            System.out.printf("%d persons found:%n", searchIndexResult.size());
            searchIndexResult.stream()
                    .map(element -> people.get(element))
                    .forEach(System.out::println);
        } else {
            System.out.println(NO_ONE_FOUND);
        }
    }
}

enum Strategies {
    ANY, ALL, NONE
}