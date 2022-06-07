package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.LinkedList;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	private static Input input;
	private static LinkedList<Runnable> microServices;
	private static Gson gson;

	public static void main(String[] args) {
		microServices = new LinkedList<>();
		 gson=new Gson();
		 if(args.length!=2){
		 	return;
		 }
		try{
			//reading the json file.
			Reader reader=new FileReader(args[0]);
			 input=gson.fromJson(reader,Input.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Ewoks ewoks = Ewoks.getInstance();
		//getting and creating all the microservices of the program, using the json file information.
		LeiaMicroservice leia = new LeiaMicroservice(input.getAttacks());
		microServices.add(leia);
		HanSoloMicroservice hanSoloMicroservice=new HanSoloMicroservice();
		microServices.addLast(hanSoloMicroservice);
		C3POMicroservice c3POMicroservice=new C3POMicroservice();
		microServices.addLast(c3POMicroservice);
		R2D2Microservice  r2D2Microservice = new R2D2Microservice(input.getR2D2());
		microServices.addLast(r2D2Microservice);
		LandoMicroservice landoMicroservice = new LandoMicroservice(input.getLando());
		microServices.addLast(landoMicroservice);

		//getting all the ewoks from the input file.
		for(int i=1;i<=input.getEwoks();i++)
		{
			ewoks.add(i);
		}

		//creating all the threads and running them at the same wime
		//while we make the main file to wait for them all to finish.
		Thread leiaT = new Thread(microServices.get(0));
		Thread hanT=new Thread(microServices.get(1));
		Thread c3poT=new Thread(microServices.get(2));
		Thread r2d2T=new Thread(microServices.get(3));
		Thread Lando = new Thread(microServices.get(4));
		leiaT.start();
		hanT.start();
		c3poT.start();
		r2d2T.start();
		Lando.start();
		try{
			leiaT.join(); hanT.join(); c3poT.join(); r2d2T.join(); Lando.join();
		}
		catch (InterruptedException e)
		{
		}
		//outputing the diary so we get the needed information.
		output(args[1]);
	}

	public static void output(String outputPath)
	{

		//outputing the diary to the json.
		try{
			gson=new GsonBuilder().setPrettyPrinting().create();
			FileWriter writer = new FileWriter(outputPath);
			Diary d= Diary.getInstance();
			gson.toJson(d,writer);
			writer.flush();
			writer.close();
		}catch (IOException e) {
			e.printStackTrace();
		}

	}
}
