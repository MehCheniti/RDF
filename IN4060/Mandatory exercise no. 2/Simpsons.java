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

public class Simpsons{

private Model model;
String sim;
String fam;

  private Simpsons(){
	  model = ModelFactory.createDefaultModel();
	}

  public Simpsons readInput(String input){
    FileManager.get().addLocatorClassLoader(Simpsons.class.getClassLoader());
    model = FileManager.get().loadModel(input);
    sim = model.getNsPrefixURI("sim");
    fam = model.getNsPrefixURI("fam");
    return this;
  }

  private Simpsons addInfo(){
    Resource maggie = addSimpson("Maggie Simpson", 1);
  	Resource mona = addSimpson("Mona Simpson", 70);
  	Resource abraham = addSimpson("Abraham Simpson", 78);
  	Resource herb = addName("Herb Simpson");
    marriage(abraham, mona);
    addFather(model.createResource(), herb);
  	return this;
  }

  private Resource addSimpson(String name, Integer age){
	  Resource simpson = addName(name);
	  Property ageProperty =
    model.createProperty(model.getNsPrefixURI("foaf") + "age");
	  simpson.addProperty(ageProperty, age.toString(), XSDDatatype.XSDint);
	  return simpson;
  }

  private Resource addName(String name){
	  String n = name.split(" ")[0];
	  Resource simpson = model.createResource(prefix(name, sim));
	  simpson.addProperty(RDF.type, FOAF.Person);
	  simpson.addProperty(FOAF.name, name);
	  return simpson;
  }

  private String prefix(String name, String prefix){
	  return prefix + name;
	}

  private void marriage(Resource husband, Resource wife){
	  Property spouse = model.createProperty(prefix("hasSpouse", fam));
	  husband.addProperty(spouse, wife);
	  wife.addProperty(spouse, husband);
	}

  private void addFather(Resource father, Resource child){
	  Property fatherProperty = model.createProperty(prefix("hasFather", fam));
	  child.addProperty(fatherProperty, father);
	}

  private void checkAge(Resource simpson, Integer age){
	  Resource infant = model.createResource(prefix("Infant", fam));
	  Resource minor = model.createResource(prefix("Minor", fam));
	  Resource old = model.createResource(prefix("Old", fam));

	  if (age < 18){
		  simpson.addProperty(RDF.type, minor);
		  if (age < 2){
			  simpson.addProperty(RDF.type, infant);
			}
		}

		if (age > 70){
		  simpson.addProperty(RDF.type, old);
		}
	}

  private Simpsons ageType(){
	  Property age =
    model.createProperty(prefix("age", model.getNsPrefixURI("foaf")));
	  Iterator<Statement> statements =
    model.listStatements((Resource) null, age, (Resource) null);

		while(statements.hasNext()){
		  Statement statement = statements.next();
		  Literal ageLiteral = (Literal) statement.getObject();
	    Integer checkAge = ageLiteral.getInt();
		  Resource simpson = (Resource) statement.getSubject();
		  checkAge(simpson, checkAge);
		}

	  return this;
	}

  public Simpsons createFile(String file){
		try (PrintWriter pw = new PrintWriter(file)){
		  model.write(pw, "Turtle");
		} catch (Exception e){
		  System.out.printf(e.getMessage());
		}

	  return this;
	}

  public static void main(String[] args){

	  Simpsons s = new Simpsons();
	  s.readInput(args[0]);
	  s.addInfo();
	  s.ageType();
	  s.createFile(args[1]);

  }

}
