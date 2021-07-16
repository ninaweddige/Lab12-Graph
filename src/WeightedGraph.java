import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

public class WeightedGraph {
	//First element of the LinkedList is the vertex itself, followed by all adjacent vertices
	private ArrayList<LinkedList<Vertex>> vertices;
	
	public class Vertex{
		private String name;
		private int weight;
		
		public Vertex(String name, int weight) {
			this.name = name;
			this.weight = weight;
		}
		
		public String getName() {
			return name;
		}
		
		public int getWeight() {
			return weight;
		}
		
		public String toString() {
			return name + " (" + weight + ")";
		}
	}
	
	private static class Label implements Comparable<Label> {
	      String labeledVertex;
	      String predecessorVertex;
	      double cost;
	      public int compareTo(Label o) {
	         return Double.compare(cost, o.cost);         
	      }
	}
	
	//Constructor
	public WeightedGraph() {
		vertices = new ArrayList<LinkedList<Vertex>>();
	}
	
	//Adds a vertex to the Graph
	public void addVertex(String name) {
		for(LinkedList<Vertex> list : vertices) {
			if(list.getFirst().getName().equals(name)){ //Graph already contains a vertex with this name
				//System.out.println("Vertex already exists, was not added.");
				return;
			}
		}
		LinkedList<Vertex> adjacencyList = new LinkedList<>(); //Make a new adjacencyList for this vertex
		adjacencyList.add(new Vertex(name, 0)); //Add the new vertex to the beginning of the list
		vertices.add(adjacencyList); //Add the adjacencyList to the ArrayList
	}
	
	/**
	 * Adds a weighted edge between two vertices. If an edge already exists between them, it is replaced.
	 * @param from The name of the one of the vertices incident on the edge.
	 * @param to The name of the other vertex.
	 * @param weight The weight of the edge.
	 * @return b Returns -1 if no edge was created because one or both of the vertices aren't in the graph,
	 * 0 if an existing edge was replaced, and 1 if an edge was created where none existed before.
	 */
	public int addEdge(String from, String to, int weight) {
		int b = 1;
		//Make sure that both vertices exist in the graph and save their indices
		int vTo, vFrom;
		vTo = vFrom = -1;
		
		while(vTo < 0 && vFrom < 0) {
			for(LinkedList<Vertex> list : vertices) {
				if(list.getFirst().getName().equals(to)) {
					vTo = vertices.indexOf(list);
				}
				if(list.getFirst().getName().equals(from)) {
					vFrom = vertices.indexOf(list);
				}
			}
		}
		
		if(vTo < 0 || vFrom < 0) {return -1;}
	
		//Check if the Vertex already has a neighbor by this name. If so, remove it.
		ListIterator<Vertex> it = vertices.get(vFrom).listIterator();
		while(it.hasNext()) {
			if(it.next().getName().equals(to)) {
				it.remove();
				b = 0;
			}
		}
		
		ListIterator<Vertex> it2 = vertices.get(vTo).listIterator();
		while(it2.hasNext()) {
			if(it2.next().getName().equals(from)) {
				it2.remove();
			}
		}
		
		//Adds the other vertex to the adjacency list of each of the vertices incident to the edge
		vertices.get(vFrom).add(new Vertex(to, weight));
		vertices.get(vTo).add(new Vertex(from, weight));
		return b;
	}
	
	//Returns an ArrayList containing all the names of the vertices of this Graph.
	public ArrayList<String> getVertices(){
		ArrayList<String> v = new ArrayList<>();
		for(LinkedList<Vertex> list : vertices) {
			v.add(list.peekFirst().getName());
		}
		return v;
	}
	
	//Returns the number of vertices in this Graph.
	public int getVertexCount() {
		int v = 0;
		for(LinkedList<Vertex> list : vertices) {
			v++;
		}
		return v;
	}
	
	//Returns an ArrayList containing all neighboring vertices of the Vertex with the given name
	public ArrayList<Vertex> getNeighbors(String name){
		ArrayList<Vertex> neighbors = new ArrayList<>();
		int index = getIndex(name);
		LinkedList<Vertex> a = vertices.get(index);
		Vertex first = a.removeFirst();
		neighbors.addAll(a);
		a.addFirst(first);
		return neighbors;
	}
	
	//Return the index of the adjacency list for the Vertex with the given name
	public int getIndex(String name) {
		for(LinkedList<Vertex> list : vertices) {
			if(list.peek().getName().equals(name)) {
				return vertices.indexOf(list);
			}
		}
		return -1;
	}
	
	public String toString() {
		String graph = "";
		for(LinkedList<Vertex> list : vertices) {
			graph += "Vertex: " + list.peekFirst() + " Neighbors: ";
			ListIterator<Vertex> iterator = list.listIterator(1);
			while(iterator.hasNext()) {
				graph += (iterator.next()) + " ";
			}
			graph += "\n";
		}
		return graph;
	}
	
	/**
	 * Finds the cheapest path (lowest weight) between two randomly chosen vertices in the graph.
	 */
	public LinkedList<String> cheapestPath() {
		  int numberVertices = this.getVertexCount();
		  Random random = new Random();
		
		  //Generate start and end of the path
		  int f = random.nextInt(numberVertices);
		  int t = random.nextInt(numberVertices);
		  while(t == f) {
			  t = random.nextInt(numberVertices);
		  }

		  Vertex from =  this.vertices.get(f).peek();
		  Vertex to = this.vertices.get(t).peek();
		
		  System.out.println("Path from " + from.getName() + " to " + to.getName());
		
	      Set<String> searched = new HashSet<>();
	      Map<String, Label> labels = new HashMap<>();
	      PriorityQueue<Label> labelQueue = new PriorityQueue<>();
	      // Step 1
	      for (Vertex neighbor : this.getNeighbors(from.getName())) {
	    	  Label l = new Label();
	    	  l.cost = neighbor.getWeight();
	    	  l.labeledVertex = neighbor.getName();
	    	  l.predecessorVertex = from.getName();
	    	  labelQueue.add(l);
	    	  labels.put(l.labeledVertex, l);
	      }
	      boolean done = false;
	      while (!done) {
	         // Step 2
	         Label r = labelQueue.remove(); //remove highest priority (lowest cost) node and travel to it
	         for (Vertex e : this.getNeighbors(r.labeledVertex)) {
	        	 String neighbor = e.getName();
	        	 if(!searched.contains(neighbor)) {
	        		// neighbor unlabeled
	                  Label l = new Label();
	                  l.cost = r.cost + e.getWeight(); //cost of getting to r + this edge's data
	                  l.labeledVertex = e.getName();//neighbor
	                  l.predecessorVertex = r.labeledVertex;
	                  labelQueue.add(l);
	                  labels.put(l.labeledVertex, l);
	        	 } else {
	                  Label neighborLabel = labels.get(neighbor);
	                  if (r.cost + e.getWeight() < neighborLabel.cost) {
	                     neighborLabel.cost = r.cost + e.getWeight();
	                     neighborLabel.predecessorVertex = r.labeledVertex;
	                  }
	               }
	            }
	         searched.add(r.labeledVertex);
	         if (r.labeledVertex.equals(to.getName()))
	            done = true;
	      }
	      // Step 4
	      LinkedList<String> result = new LinkedList<>();
	      String current = to.getName();
	      result.addFirst(current);
	      while (!current.equals(to.getName())) {
	         current = labels.get(current).predecessorVertex;
	         result.addFirst(current);
	      }
	      return result;
	}
	
	public void printPath(LinkedList<String> path) {
		while(!path.isEmpty()) {
			System.out.print(path.pop());
		}
	}
	
	public static void main(String[] args) {
		WeightedGraph g = new WeightedGraph();
		g.addVertex("A");
		g.addVertex("B");
		g.addVertex("C");
		g.addVertex("D");
		g.addVertex("E");
		g.addVertex("A");
		g.addEdge("A", "B", 5);
		g.addEdge("B", "C", 3);
		g.addEdge("C", "D", 2);
		g.addEdge("A", "D", 1);
		g.addEdge("E", "B", 1);
		g.addVertex("A");
		g.addEdge("A", "D", 0);
		System.out.println(g.toString());
		//System.out.println(g.getNeighbors("A"));
		//System.out.println(g.getVertexCount());
		g.printPath(g.cheapestPath());
	}
	
}
