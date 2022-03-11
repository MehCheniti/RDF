import java.io.*;
import java.util.*;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.*;
import com.hp.hpl.jena.sparql.*;
import com.hp.hpl.jena.datatypes.*;
import com.hp.hpl.jena.vocabulary.*;

public class Oblig4{

private Model rdfSchema;
private Model rdfData;
private InfModel inferredModel;
private Model resultModel;

  private Oblig4(){
		rdfSchema = ModelFactory.createDefaultModel();
		resultModel = ModelFactory.createDefaultModel();
		rdfData = ModelFactory.createDefaultModel();
	}

  public void loadSchema(String schema){
		FileManager.get().addLocatorClassLoader(ObligFour.class.getClassLoader());
		rdfSchema = FileManager.get().loadModel(schema);
	}

	public void loadRdfGraph(String rdfGraph){
		loadFileIntoModel(rdfData, rdfGraph, "TURTLE");
	}

	private void loadFileIntoModel(Model model, String rdfFile){
		loadFileIntoModel(model, rdfFile, FileUtils.guessLang(rdfFile));
	}

	private void loadFileIntoModel(Model model, String rdfFile, String language){
		try{
			model.read(rdfFile, language);
		} catch (Exception e){
			System.out.printf(e.getMessage());
		}
	}

	public void reasoning(){
		inferredModel = ModelFactory.createRDFSModel(rdfSchema, rdfData);
	}

  public void execute(String newQuery){
		if (inferredModel == null){
      reasoning();
    }

    Query query = QueryFactory.read(newQuery);
    QueryExecution q = QueryExecutionFactory.create(query, inferredModel);
    resultModel = q.execConstruct();
	}

	public void save(String foafFile){
		write(resultModel, foafFile);
	}

  public void write(Model model, String output){
		try (PrintWriter pw = new PrintWriter(output)){
			model.write(pw, "TURTLE");
		} catch (Exception e){
			System.out.printf(e.getMessage());
		}
	}

  public static void main(String[] args){

		String schemaFile;
    String queryFile;
    String foafFile;

    schemaFile = args[0];
    queryFile = args[1];
    foafFile = args[2];

		Oblig4 o4 = new Oblig4();
		o4.loadSchema(schemaFile);
		o4.loadRdfGraph("https://www.uio.no/studier/emner/matnat/ifi/IN3060/v21/obliger/simpsons.ttl");
		o4.reasoning();
		o4.execute(queryFile);
		o4.save(foafFile);
    
	}

}
