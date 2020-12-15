package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.JsonInputReader;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;

import java.io.IOException;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) throws IOException {
		JsonInputReader reader = new JsonInputReader();
		Input input = reader.getInputFromJson("C:\\Users\\Yonatan\\IdeaProjects\\Assignment2\\SPL211\\input.json");
		Attack[] attacks = input.getAttacks();
		long R2D2Duration = input.getR2D2();
		long LandoDuration = input.getLando();
		//int num_of_ewoks = input.getEwoks();
		Ewoks ewoks = Ewoks.getInstance();
		Ewoks.set_num_of_Ewoks(input.getEwoks());
		Diary dairy = Diary.getInstance();
		long termination_time ;

		Thread Leia = new Thread(new LeiaMicroservice(attacks));
		Thread HanSolo = new Thread(new HanSoloMicroservice(ewoks));
		Thread C3PO = new Thread(new C3POMicroservice(ewoks));
		Thread R2D2 = new Thread(new R2D2Microservice(R2D2Duration));
		Thread Lando = new Thread(new LandoMicroservice(LandoDuration));
		//System.out.println(System.currentTimeMillis());

		// Leia need to sleep at first because need to let other subscribe for events first
		Leia.start();
		HanSolo.start();
		C3PO.start();
		R2D2.start();
		Lando.start();


		try {
			Leia.join();
			HanSolo.join();
			C3PO.join();
			R2D2.join();
			Lando.join();
		} catch (InterruptedException e) {

		}
		System.out.println(dairy.toString());
		termination_time=System.currentTimeMillis();
		System.out.println("All threads terminate "+termination_time+" milliseconds later.");
	}
}
