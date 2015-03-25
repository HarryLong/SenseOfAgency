package dialogs;

public class PreBlockDialogContent {
	
	public static String getDialogContent(int version, int block)
	{
		if(version == 1)
		{
			if(block == 1)
				return BLOCK_1_1;
			if(block == 2)
				return BLOCK_1_2;
			if(block == 3)
				return BLOCK_1_3;
			if(block == 4)
				return BLOCK_1_4;
		}
		else // version = 2
		{
			if(block == 1)
				return BLOCK_2_1;
			if(block == 2)
				return BLOCK_2_2;
			if(block == 3)
				return BLOCK_2_3;
			if(block == 4)
				return BLOCK_2_4;
		}
		return "";
	}
	
	public static final String BLOCK_1_1 = "Instructions:\n"
			+ "- The clock will start spinning at a rate of one rotation every 2560 ms.\n"
			+ "- At the time of YOUR choosing (not the clocks choosing!), click the space bar.\n"
			+ "- The clock will stop rotating.\n" 
			+ "\nAt the end of the run, you will be asked the following question:\n" 
			+ "- At what position was the clock hand when you pressed the space bar?\n";
	
	public static final String BLOCK_1_2 = "Instructions:\n"
			+ "- The clock will start spinning at a rate of one rotation every 2560 ms.\n"
			+ "- An audible tone will be emitted.\n"
			+ "- The clock will stop rotating.\n" 
			+ "\nAt the end of the run, you will be asked the following question:\n" 
			+ "- At what position was the clock hand when you heard the tone?\n"
			+ "- With what certainty do you think you caused the tone to?\n";

	public static final String BLOCK_1_3 = "Instructions:\n"
			+ "- The clock will start spinning at a rate of one rotation every 2560 ms.\n"
			+ "- At the time of YOUR choosing (not the clocks choosing!), click the space bar.\n"
			+ "- An audible tone will be emitted.\n"
			+ "- The clock will stop rotating.\n" 
			+ "\nAt the end of the run, you will be asked the following question:\n" 
			+ "- At what position was the clock hand when you pressed the space bar?\n";
	
	public static final String BLOCK_1_4 = "Instructions:\n"
			+ "- The clock will start spinning at a rate of one rotation every 2560 ms.\n"
			+ "- At the time of YOUR choosing (not the clocks choosing!), click the space bar.\n"
			+ "- An audible tone will be emitted.\n"
			+ "- The clock will stop rotating.\n" 
			+ "\nAt the end of the run, you will be asked the following question:\n" 
			+ "- At what position was the clock hand when you heard the tone?\n"
			+ "- With what certainty do you think you caused the tone to?\n";
	
	
	public static final String BLOCK_2_1 = "Instructions:\n"
			+ "- The clock will start spinning at a rate of one rotation every 2560 ms.\n"
			+ "- At the time of YOUR choosing (not the clocks choosing!), click the space bar.\n"
			+ "- The clock will stop rotating.\n" 
			+ "\nAt the end of the run, you will be asked the following question:\n" 
			+ "- At what position was the clock hand when you pressed the space bar?\n";
	
	public static final String BLOCK_2_2 = "Instructions:\n"
			+ "- The clock will start spinning at a rate of one rotation every 2560 ms.\n"
			+ "- An audible tone will be emitted.\n"
			+ "- The clock will stop rotating.\n" 
			+ "\nAt the end of the run, you will be asked the following question:\n" 
			+ "- At what position was the clock hand when you heard the tone?\n"
			+ "- With what certainty do you think you caused the tone?\n"
			+ "- How loud did the tone seem?\n";
	
//	public static final String BLOCK_2_3 = "<html>Status: <font color='green'>OK</font></html>";
	
	public static final String BLOCK_2_3 = "Instructions:\n"
			+ "- The clock will start spinning at a rate of one rotation every 2560 ms.\n"
			+ "- The color of the clock face will alternate.\n"
			+ "- When the color of the clock face is one of Red, Green or Blue"
				+ " click the associated button (R: red, G: green, B: blue) as fast as you can in an attempt to beat your competitor.\n"
			+ "- An audible tone will be emitted.\n"
			+ "- The clock will stop rotating.\n" 
			+ "\nAt the end of the run, you will be asked the following question:\n" 
			+ "- At what position was the clock hand when you pressed the button?\n"
			+ "- With what certainty do you think you caused the tone to?\n"
			+ "- How loud did the tone seem?\n";

	public static final String BLOCK_2_4 = "Instructions:\n"
			+ "- The clock will start spinning at a rate of one rotation every 2560 ms.\n"
			+ "- The color of the clock face will alternate.\n"
			+ "- When the color of the clock face is one of Red, Green or Blue"
				+ " click the associated button (R: red, G: green, B: blue) as fast as you can in an attempt to beat your competitor.\n"
			+ "- An audible tone will be emitted.\n"
			+ "- The clock will stop rotating.\n" 
			+ "\nAt the end of the run, you will be asked the following question:\n" 
			+ "- At what position was the clock hand when you heard the tone?\n"
			+ "- With what certainty do you think you caused the tone to?\n"
			+ "- How loud did the tone seem?\n";	
}
