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
		Ewoks ewoks = Ewoks.getInstance();
		ewoks.setEwoks(input.getEwoks());
		Diary diary = Diary.getInstance();


		Thread Leia = new Thread(new LeiaMicroservice(attacks));
		Thread HanSolo = new Thread(new HanSoloMicroservice(ewoks));
		Thread C3PO = new Thread(new C3POMicroservice(ewoks));
		Thread R2D2 = new Thread(new R2D2Microservice(R2D2Duration));
		Thread Lando = new Thread(new LandoMicroservice(LandoDuration));



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
		System.out.println(diary.toString());
	}
}
