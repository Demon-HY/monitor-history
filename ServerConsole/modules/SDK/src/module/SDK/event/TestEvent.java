package module.SDK.event;

public class TestEvent {

	public static class MyEvent extends Event {
		enum Type implements EventType {
			MyEvent
		};

		public String data = "hello";
	}
	
	public static class MyListener implements IListener {

	    public void onEvent(EventType type, Event e) {
			MyEvent event = (MyEvent)e;
			System.out.println(event.data);
			System.out.println("fired");
		}
	}
	
//	public static void main(String[] args) {
//		EventHub hub = EventHub.getInst("test_module");
//		
//		hub.registListener(MyEvent.Type.MyEvent, new MyListener());
//		hub.dispatchEvent(MyEvent.Type.MyEvent, new MyEvent(), new Env("testEvent"));
//	}

}
