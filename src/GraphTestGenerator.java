import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GraphTestGenerator {
    public static void main(String[] args) {
        int vertices = 60_000; // quantidade de vértices
        int edges = 6_000_000; // número de arestas
        String fileName = "graph1M.txt";

        Random rand = new Random();

        try (FileWriter writer = new FileWriter(fileName)) {
            // Primeira linha: número de vértices e arestas
            writer.write(vertices + " " + edges + "\n");

            // Gera arestas aleatórias
            for (int i = 0; i < edges; i++) {
                int v = rand.nextInt(vertices) + 1;
                int w = rand.nextInt(vertices) + 1;
                writer.write(v + " " + w + "\n");
            }

            System.out.println("Arquivo gerado: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
