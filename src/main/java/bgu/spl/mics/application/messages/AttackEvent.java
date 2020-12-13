package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

public class AttackEvent implements Event<Boolean> {
	public Attack getAttack(){return null;};

	@Override
	public String getType() {
		return "AttackEvent";
	}
}
