import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.management.RuntimeErrorException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class Menu {

    private static Scanner sc = new Scanner(System.in);

    public static void RunVAnality(Digraph G) {
        int v = 0;

        System.out.print("Digite o vértice a ser analisado: ");

        v = sc.nextInt();

        if (v > 0) {
            Digraph.vInfo(G, v);
        }

    }

    public static void RunDeepSeach(Digraph G) {
        int v = 0;

        System.out.print("Digite o vértice a ser analisado: ");

        v = sc.nextInt();

        if (v > 0) {
            G.DeepSearch(v);

            System.out.println();
        }
    }

    public static Digraph init() {
        sc = new Scanner(System.in);

        try {
            System.out.print("Digite o nome do arquivo: ");
            String fileName = sc.nextLine();

            FileGraph fp = new FileGraph(fileName);

            return new Digraph(fp);

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }

        return null;
    }

}

class FileGraph {
    private String path;
    private String outputPath;
    private BufferedReader br;

    public FileGraph(String path, String outputPath) {
        this.path = path;
        this.outputPath = outputPath;
        initReader();
    }

    public FileGraph(String path) {
        this.path = path;
        this.outputPath = "";
        initReader();
    }

    private void initReader() {
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] ReadLine() {
        try {
            String line = br.readLine();
            if (line != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 2) {
                    int a = Integer.parseInt(parts[0]);
                    int b = Integer.parseInt(parts[1]);
                    return new int[] { a, b };
                }
            } else {
                close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            if (br != null)
                br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public String toString() {
        return "FileGraph [path=" + path + ", outputPath=" + outputPath + "]";
    }
}

/**
 * Original code is available on
 * https://algs4.cs.princeton.edu/13stacks/Bag.java.html
 * Lista encadeada para armazenamento dos itens
 * 
 * @author Robert Sedgewick
 * @author Kevin Wayne
 *
 */

class Bag<T extends Comparable<T>> implements Iterable<T> {
    private Node<T> first;
    private int n; // Grau de saída

    private static class Node<T> {
        private T item;
        private Node<T> next;
    }

    public Bag() {
        first = null;
        n = 0;
    }

    public boolean isEmpty() {
        return first == null;
    }

    public int size() {
        return n;
    }

    public void add(T item) {
        Node<T> newNode = new Node<>();
        newNode.item = item;

        if (first == null || item.compareTo(first.item) <= 0) {
            newNode.next = first;
            first = newNode;
        } else {
            Node<T> current = first;
            while (current.next != null && item.compareTo(current.next.item) > 0) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        n++;
    }

    public Iterator<T> iterator() {
        return new LinkedIterator(first);
    }

    private class LinkedIterator implements Iterator<T> {
        private Node<T> current;

        public LinkedIterator(Node<T> first) {
            current = first;
        }

        public boolean hasNext() {
            return current != null;
        }

        public T next() {
            if (!hasNext())
                throw new NoSuchElementException();
            T item = current.item;
            current = current.next;
            return item;
        }
    }
}

enum EdgeType {
    ARVORE,
    RETORNO,
    AVANCO,
    CRUZAMENTO;
}

record Edge(int from, int to, EdgeType type) {
    @Override
    public String toString() {
        return type + " = { " + from + " , " + to + " }";
    }
}

/**
 * Mudanças foram feitas para se adequar ao contexto do uso
 * 
 * Código original disponível em
 * https://algs4.cs.princeton.edu/42digraph/Digraph.java.html
 * 
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */

class Digraph {
    private static final String NEWLINE = "; \n";

    private final int V; // número de vértices do grafo
    private int E; // número de arestas do grafo
    private Bag<Integer>[] adj; // lista de adjacência para cada vértice V
    private int[] indegree; // grau de entrada de cada vértice V

    boolean debug = false;

    Integer T = 0;
    Integer[] TD;
    Integer[] TT;
    Integer[] PAI;
    List<Edge> edges;

    public Digraph(FileGraph fg) {

        int[] header = fg.ReadLine();
        if (header == null || header.length < 2) {
            throw new IllegalArgumentException("Arquivo inválido: primeira linha deve conter V e E");
        }

        this.V = header[0];
        this.E = 0;

        indegree = new int[V + 1];
        adj = (Bag<Integer>[]) new Bag[V + 1];
        for (int v = 1; v <= V; v++) {
            adj[v] = new Bag<Integer>();
        }

        int[] edge;
        while ((edge = fg.ReadLine()) != null) {
            if (edge.length < 2) {
                throw new IllegalArgumentException("Arquivo inválido");
            }
            int v = edge[0];
            int w = edge[1];
            addEdge(v, w);
        }

        if (this.E != header[1])
            throw new RuntimeErrorException(null, "Numero de arestas invalido");

        TD = new Integer[this.V + 1];
        TT = new Integer[this.V + 1];
        PAI = new Integer[this.V + 1];
        edges = new LinkedList<>();

        for (int i = 1; i <= this.V; i++) {
            TD[i] = 0;
            TT[i] = 0;
            PAI[i] = 0;
        }
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    private void validateVertex(int v) {
        if (v < 1 || v > V) {
            throw new IllegalArgumentException("O vértice " + v + " não está entre 1 e " + V);
        }
    }

    public void addEdge(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        adj[v].add(w);
        indegree[w]++;
        E++;
    }

    /**
     * Retorna os vértices adjacentes a partir do vértice {@code v} neste dígrafo.
     *
     * @param v o vértice
     * @return os vértices alcançáveis a partir de {@code v}, como um iterável
     * @throws IllegalArgumentException caso v não esteja no intervalo válido
     */
    public Iterable<Integer> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    /**
     * Retorna o número de arestas que saem do vértice {@code v}.
     * Isso é conhecido como o <em>grau de saída</em> do vértice.
     *
     * @param v o vértice
     * @return o grau de saída do vértice {@code v}
     * @throws IllegalArgumentException caso v não esteja no intervalo válido
     */
    public int outdegree(int v) {
        validateVertex(v);
        return adj[v].size();
    }

    /**
     * Retorna o número de arestas que chegam no vértice {@code v}.
     * Isso é conhecido como o <em>grau de entrada</em> do vértice.
     *
     * @param v o vértice
     * @return o grau de entrada do vértice {@code v}
     * @throws IllegalArgumentException caso v não esteja no intervalo válido
     */
    public int indegree(int v) {
        validateVertex(v);
        return indegree[v];
    }

    /**
     * Retorna informações de um vértice.
     * 
     * @param Digraph o grafos
     * @param v       o vértice
     * @throws IllegalArgumentException caso v não esteja no intervalo válido
     */
    public static void vInfo(Digraph G, int v) {
        System.out.println("\n--- Resultado ---");
        System.out.println("Vértice: " + v);
        System.out.println("Grau de entrada: " + G.indegree(v));
        System.out.println("Grau de saída: " + G.outdegree(v));

        System.out.print("Sucessores: ");
        for (int w : G.adj(v)) {
            System.out.print(w + " ");
        }
        System.out.println();

        System.out.print("Predecessores: ");
        for (int u = 1; u <= G.V(); u++) {
            for (int w : G.adj(u)) {
                if (w == v) {
                    System.out.print(u + " ");
                    break;
                } else if (w > v) {
                    break;
                }
            }
        }
        System.out.println("\n");
    }

    public void DeepSearch(int start) {

        for (int i = 1; i <= V; i++) {
            if (TD[i] == 0) {
                // DeepSearchRec(i);
                iterativeDeepSearch(i);
            }
        }

        // edges.forEach(e -> System.out.println(e));

        System.out.println("\n--- Arestas de árvore encontradas ---");
        for (Edge e : edges) {
            if (e.type() == EdgeType.ARVORE) {
                System.out.println(e);
            }
        }

        System.out.println("\n--- Classificação das arestas que saem do vértice " + start + " ---");
        for (Edge e : edges) {
            if (e.from() == start) {
                System.out.println(e);
            }
        }

        if (debug) {
            System.out.println("\n--- Tabela ---");

            System.out.printf("%-6s", "V");
            for (int i = 1; i <= V; i++) {
                System.out.printf("%-6d", i);
            }
            System.out.println();

            System.out.printf("%-6s", "TD");
            for (int i = 1; i <= V; i++) {
                System.out.printf("%-6d", TD[i]);
            }
            System.out.println();

            System.out.printf("%-6s", "TT");
            for (int i = 1; i <= V; i++) {
                System.out.printf("%-6d", TT[i]);
            }
            System.out.println();

            System.out.printf("%-6s", "PAI");
            for (int i = 1; i <= V; i++) {
                if (PAI[i] == 0) {
                    System.out.printf("%-6s", "∅");
                } else {
                    System.out.printf("%-6d", PAI[i]);
                }
            }
            System.out.println();
        }
    }

    private void DeepSearchRec(int v) {
        T++;
        TD[v] = T;

        for (int w : adj(v)) {
            if (TD[w] == 0) {
                edges.add(new Edge(v, w, EdgeType.ARVORE));
                PAI[w] = v;
                DeepSearchRec(w);
            } else if (TT[w] == 0) {
                edges.add(new Edge(v, w, EdgeType.RETORNO));
            } else if (TD[v] < TD[w]) {
                edges.add(new Edge(v, w, EdgeType.AVANCO));
            } else {
                edges.add(new Edge(v, w, EdgeType.CRUZAMENTO));
            }
        }

        T++;
        TT[v] = T;
    }

    private void iterativeDeepSearch(int startNode) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(startNode);

        T++;
        TD[startNode] = T;

        Iterator<Integer>[] iterators = new Iterator[V + 1];
        iterators[startNode] = adj(startNode).iterator();

        while (!stack.isEmpty()) {
            int v = stack.peek();

            if (iterators[v].hasNext()) {
                int w = iterators[v].next();

                if (TD[w] == 0) {
                    edges.add(new Edge(v, w, EdgeType.ARVORE));
                    PAI[w] = v;
                    T++;
                    TD[w] = T;

                    stack.push(w);
                    iterators[w] = adj(w).iterator();

                } else if (TT[w] == 0) {

                    if (PAI[v] != w) {
                        edges.add(new Edge(v, w, EdgeType.RETORNO));
                    }

                } else {
                    if (TD[v] < TD[w]) {
                        edges.add(new Edge(v, w, EdgeType.AVANCO));
                    } else {
                        edges.add(new Edge(v, w, EdgeType.CRUZAMENTO));
                    }
                }
            } else {

                T++;
                TT[v] = T;
                stack.pop();
            }
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " vértices, " + E + " arestas " + NEWLINE);
        for (int v = 1; v <= V; v++) {
            s.append(String.format("%d: ", v));
            for (int w : adj[v]) {
                s.append(String.format("%d ", w));
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

}

public class App {
    public static void main(String[] args) throws Exception {

        Digraph G = Menu.init();

        Menu.RunDeepSeach(G);

    }
}
