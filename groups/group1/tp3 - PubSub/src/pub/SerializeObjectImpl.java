
package pub;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SerializeObjectImpl {
		
	
	private static String nameCleaner(String input){
		//System.err.println("before: " + input);
		input = input.replaceAll("[^a-zA-Z 0-9]+","");	
		//System.err.println("after: " + input);
		return input;
	}
		 
	//To serialize (save the Object state to a file) :
	public static void serializeToFile(Object obj, String objName){
		  objName = nameCleaner(objName);
		  try {
		      BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(objName+".dat", false));
		      ObjectOutputStream oos = new ObjectOutputStream(fout);
		      oos.writeObject(obj);
		      //fout.flush();
		      oos.flush();
		      oos.close();
		      
		      fout.flush();
		      fout.close();
		      //
		      
		      fout = null;
		      oos = null;
		      obj = null;			      
	          
		      }
		   catch (Exception e) { System.err.println("not serialized: " + objName);}
		
	}
	
	public static Object unserializeToFile(String objName){
		Object obj;
		objName = nameCleaner(objName);
		
	   try {		
		    BufferedInputStream fin = new BufferedInputStream(new FileInputStream(objName+".dat"));
			ObjectInputStream ois = new ObjectInputStream(fin);
		    obj = ois.readObject();
			ois.close();
			fin.close();
		    return obj;
	    }catch (Exception e) { 		   
		    return null;
		 }
	  	 
	}

}
