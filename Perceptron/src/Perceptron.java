import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Perceptron {
    private ArrayList<ObjectClass> trainList, testList;
    private double[] weights;
    private int vectorSize;
    private int epoch;
    private double alpha;
    private Set<String> classLabels;
    private String machingTo;
    private String other;

    public Perceptron() {
        System.out.println("Training file name");
        trainList = processFile(getFileName());
        System.out.println("Test file name");
        testList = processFile(getFileName());
        alpha = setAlpha();
        epoch = setEpoch();
        train();
        openLoop();
        System.out.println("Asta la vista");
    }

    private void openLoop() {
        while (true) {
            System.out.println("Wybierz jedna z opcji");
            System.out.println("A -> Testuj");
            System.out.println("B -> Wprowadz nowe obserwacje");
            System.out.println("C -> Zmien sciezke do pliku treningowego");
            System.out.println("D -> Zmien sciezke do pliku testowego");
            System.out.println("E -> Zmien stala uczenia");
            System.out.println("F -> Zmien liczbe epok uczenia");
            System.out.println("Q -> Wyjdź z programu");

            Scanner sc = new Scanner(System.in);
            String option;
            do {
                System.out.print(": ");
                option = sc.nextLine().toLowerCase().trim();
                System.out.println();
            }
            while (!option.equals("a") && !option.equals("b") && !option.equals("c") && !option.equals("d") && !option.equals("e") && !option.equals("f") && !option.equals("q"));

            switch (option) {
                case "a" -> train();
                case "b" -> insertObservation();
                case "c" -> {
                    System.out.println("Training file name");
                    changeTrainingFile();
                }
                case "d" -> {
                    System.out.println("Test file name");
                    changeTestFile();
                }
                case "e" -> alpha = setAlpha();
                case "f" -> epoch = setEpoch();
                case "q" -> System.out.println("Quitting...");
            }
            if (option.equalsIgnoreCase("q"))
                break;
        }
    }

    private void train() {

        weights = new double[vectorSize];
        Arrays.fill(weights, 1); //pseudoinicjalizuje

        for (int it = 0; it < epoch; it++) {
            int correct2 = 0;

            for (ObjectClass flower : trainList) {

                double sum = 0;
                for (int i = 0; i < weights.length; i++) {
                    sum += weights[i] * flower.getVector()[i]; //funkcja aktywacji suma w*x
                }
                double y = (sum >= 0 ? 1 : 0); //prog 0      y otrzymana
                double d = flower.getCat().equals(machingTo) ? 1 : 0; //d oczekiwana
                double delta = d - y; // 1-1 or 0-0

                if (delta != 0) {
                    for (int i = 0; i < weights.length; i++) {
                        weights[i] += alpha * delta * flower.getVector()[i];
                    }
                }
            }
            if (correct2 == testList.size()) {
                System.out.println("100% dokladnosci uzyskane po " + it + " iteracjach");
                break;
            }

            for (ObjectClass flower : testList) {
                if ((flower.getCat().equals(machingTo) && findKind(flower).equals(machingTo)) || (!flower.getCat().equals(machingTo) && findKind(flower).equals(other)))
                    correct2++;
            }
            System.out.println("Liczba poprawnie odgadniętych to " + correct2 + " na " + testList.size() + " co daje " + ((double) correct2 / testList.size()) * 100 + "% skuteczności");
        }
    }
    private String findKind(ObjectClass flower) {
        double sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * flower.getVector()[i]; // SUMA (w * x)
        }

        if (sum >= 0) {
            return machingTo;
        } // próg to zero
        else {
            return other;
        }
    }

    private void insertObservation() {
        System.out.println("Wprowadz wektor [par1 <enter> par2 <enter> parX ... number like '3.14' , '6.66'");
        Scanner sc = new Scanner(System.in);

        while(true){
            boolean isNums = true;
            String[] parts = sc.nextLine().split("\\s+");
            double[] parameters2 = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                if (!parts[i].matches(("-?\\d+([.,]?\\d+)?"))) {
                    System.out.println("ERROR: not numerical values !");
                    break;
                } //nie numeryczne

                if (!isNums) {
                    continue;
                }
                parameters2[i] = Double.parseDouble(parts[i]);
            }

            if (parameters2.length != vectorSize) {
                System.out.println("ERROR: input count mismatch");
                continue;
            } // zla liczba par

            ObjectClass inputIris = new ObjectClass(parameters2, "");
            String result = findKind(inputIris);
            if(result.equals(other))
                System.out.println("Wynik klasyfikacji: " + other + '\n');
            if(result.equals(machingTo))
                System.out.println("Wynik klasyfikacji: " + machingTo + '\n');
            break;
        }

    }
    private ArrayList<ObjectClass> processFile(String fname) {
        ArrayList<ObjectClass> group = new ArrayList<>();

        if(classLabels == null){
            classLabels = new HashSet<>();
            try{
                BufferedReader br = new BufferedReader(new FileReader(fname));
                String[] arr;

                while(classLabels.size() != 2){
                    arr = br.readLine().trim().split(",");
                    classLabels.add(arr[arr.length-1]);
                }
                Iterator<String> iter = classLabels.iterator();
                machingTo = iter.next();
                other = iter.next();
            }catch (IOException e){

            }
            System.out.println("Class labels: " + classLabels.toString());
        }

        try {
            BufferedReader bf = new BufferedReader(new FileReader(fname));
            String line;
            while ((line = bf.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.trim().split(","); // ","
                vectorSize = parts.length - 1;
                double[] parameters = new double[vectorSize];
                for (int i = 0; i < vectorSize; i++) {
                    parameters[i] = Double.parseDouble(parts[i].replace(",", "."));
                }
                group.add(new ObjectClass(parameters, String.valueOf(parts[vectorSize])));
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return group;
    }

    private void changeTrainingFile() {
        trainList = processFile(getFileName());
    }

    private void changeTestFile() {
        testList = processFile(getFileName());
    }

    private double setAlpha() {
        String strK;
        double k = -1;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.print("Podaj parametr alpha: ");
            strK = sc.nextLine();
            if (strK != null && !strK.isEmpty()) {
                try {
                    k = Double.parseDouble(strK);
                } catch (NumberFormatException n) {
                    k = -1;
                }
            }

            if (k > 0 && k < 1)
                return k;
        } while (true);
    }

    private int setEpoch() {
        String strK;
        double k = -1;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.print("Podaj liczbe epok: ");
            strK = sc.nextLine();
            if (strK != null && !strK.isEmpty()) {
                try {
                    k = Double.parseDouble(strK);
                } catch (NumberFormatException n) {
                    k = -1;
                }
            }

            if ((int) k > 0)
                return (int) k;
        } while (true);
    }

    private String getFileName() {
        Scanner sc = new Scanner(System.in);
        String fileName;
        do {
            System.out.print("Wprowadz nazwe pliku: ");
            fileName = sc.nextLine();
        } while (!new File(fileName).exists());

        return fileName;
    }
}
