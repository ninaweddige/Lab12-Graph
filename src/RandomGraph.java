import java.util.Random;

public class RandomGraph extends WeightedGraph{
	private WeightedGraph graph;
	private String[] vertexNames = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", 
			                                "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	
	//Constructor
	public RandomGraph(int v, int e) {
		super();
		graph = makeGraph(v, e);
	}
	
	public WeightedGraph makeGraph(int v, int e) {
		WeightedGraph graph = new WeightedGraph();
		Random r = new Random();
		int edges = 0;
		
		//Add the vertices
		for(int i = 0; i < v; i++) {
			graph.addVertex(vertexNames[i]);
		}
		
		while(edges < e) {
			//The indices of the vertexes to connect
			int a = r.nextInt(v);
			int b = r.nextInt(v);
			//The weight of the edge (1 to 10)
			int w = r.nextInt(10) + 1;
			//Make sure the generated integers for the vertices are different
			while(a == b) {
				b = r.nextInt(v);
			}
			int c = graph.addEdge(vertexNames[a], vertexNames[b], w);
			//Increment if a new edge was created
			if(c == 1) edges++;
		}
		return graph;
	}
	
	public String toString() {
		return this.graph.toString();
	}
	
	public int getVertexCount() {
		return this.graph.getVertexCount();
	}
	
	public static void main(String[] args) {
		RandomGraph randomGraph = new RandomGraph(10, 10);
		System.out.println(randomGraph.toString());
		//randomGraph.printPath(randomGraph.cheapestPath());

	}

}
