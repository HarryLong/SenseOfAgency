
public class TimedCallback implements Runnable{	
	int time_in_ms;
	CallbackListener receiver;
		
	public TimedCallback(CallbackListener receiver, int time_in_ms)
	{
		this.time_in_ms = time_in_ms;
		this.receiver = receiver;
	}
		
	public static final int _SLEEP_INTERVAL = 5; //ms
	@Override
	public void run() {
		long start_time = System.currentTimeMillis();
		while(System.currentTimeMillis() < start_time + time_in_ms)
		{
			try {
				Thread.sleep(_SLEEP_INTERVAL);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		receiver.callback();
	}
}
