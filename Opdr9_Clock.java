import java.util.*;
import java.time.*;
import java.time.format.*;

public class Opdr9_Clock
{
	public static void main(String[] args)
	{
		//check parameters
		Boolean isAMPM = false;
		Boolean sizeIsSet = false;
		int size = 0;

		if(args.length != 0)
		{
			for(int i = 0; i < args.length; i++)
			{
				if(args[i].equals("-12"))
				{
					isAMPM = true; 
				}
				if(args[i].equals("-s"))
				{
					sizeIsSet = true;

					if (i < args.length)
					{
						size = Integer.parseInt(args[i+1]);
					}
				}
			}
		}

		//create clock
		LCDClock clock;
		if(sizeIsSet)
		{
			clock = new LCDClock(size, isAMPM);
		}
		else
		{
			clock = new LCDClock(isAMPM);
		}
		
		clock.displayCurrentTime();
	}
}

class LCDClock
{
	private LCDChar[] clockDigits;
	private int size;
	private boolean isAMPM;

	public LCDClock()
	{
		this(2, false);
	}
	public LCDClock(int printSize)
	{
		this(printSize, false);
	}
	public LCDClock(Boolean isAMPM)
	{
		this(2, isAMPM);
	}
	public LCDClock(int printSize, Boolean isAMPM)//, int numDigits)
	{
		size = checkSize(printSize);
		this.isAMPM = isAMPM;	
	}

	private int checkSize(int inputSize)
	{
		if(inputSize < 1)
		{
			System.out.println("Given size was too small");
			return 1;
		}
		else if(inputSize > 5)
		{
			System.out.println("Given size was too big");
			return 5;
		}

		return inputSize;
	}

	public void displayCurrentTime()
	{
		String currTime = "";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");

		if(isAMPM)
		{
			DateTimeFormatter getAMPM = DateTimeFormatter.ofPattern("a");
			currTime = (LocalTime.now().format(getAMPM)).substring(0,1);
		}		
		currTime += (LocalTime.now().format(formatter));
		System.out.println("Formatted time: " + currTime);
		
		clockDigits = new LCDChar[currTime.length()];

		for(int i = 0; i < clockDigits.length; i++)
		{
			clockDigits[i] = new LCDChar(currTime.substring(i, (i+1)));

		}

		printClock();
	}

	private void printClock()
	{
		System.out.println(""); //always start on new line

		for(int row = 0; row < 5; row ++)
		{
			for(int s = 0; s < size; s++)
			{
				printClockRow(row);
				
				if(row%2 == 0)
				{
					break;
				}
			}
		}

	}

	private void printClockRow(int row)
	{
		for(int i = 0; i < clockDigits.length; i++)
		{
			clockDigits[i].printDigitRow(row, size);
			
			for(int s = 0; s < size; s++)
			{
				System.out.print(" ");
			}
			
		}

		System.out.println("");
	}
}


class LCDChar
{
	/* Segments:	0			0
				1		2		1
					3			2
				4		5		3	
					6			4*/
	
	private LCDSegment[][] digitSegments;

	public LCDChar(String input)
	{
		String i = input.trim();
		createDigitArray(i);
	}

	private void createDigitArray(String input)
	{

		if(input.equals(":"))
		{
			 digitSegments = new LCDSegment[][]{{new LCDSegment(false)},
												{new LCDSegment(), new LCDSegment()},
												{new LCDSegment(false)},
												{new LCDSegment(), new LCDSegment()},
												{new LCDSegment(false)}
												};
		}
		else
		{
			digitSegments = new LCDSegment[][]{	{new LCDSegment(0)},
												{new LCDSegment(1), new LCDSegment(1)},
												{new LCDSegment(2)},
												{new LCDSegment(3), new LCDSegment(3)},
												{new LCDSegment(4)}
												};	
			setToChar(input);
		}
	}

	private void setToChar(String input)
	{
		switch(input.toLowerCase())
		{
			case "1": 	for(int i = 0; i < digitSegments.length; i++) //turn off all but s2 & s5
							{								
								digitSegments[i][0].setIsOn(false);
							}
						break;
			case "2":	digitSegments[1][0].setIsOn(false);
						digitSegments[3][1].setIsOn(false);
						break;
			case "3": 	digitSegments[1][0].setIsOn(false);
						digitSegments[3][0].setIsOn(false);
						break;
			case "4":	digitSegments[0][0].setIsOn(false);
						digitSegments[3][0].setIsOn(false);
						digitSegments[4][0].setIsOn(false);
						break;
			case "5":	digitSegments[1][1].setIsOn(false);
						digitSegments[3][0].setIsOn(false);
						break;
			case "6":	digitSegments[1][1].setIsOn(false);
						break;
			case "7":	digitSegments[2][0].setIsOn(false);
						digitSegments[3][0].setIsOn(false);
						digitSegments[4][0].setIsOn(false);
						break;
			case "8": 	
			default: 
						break;
			case "9":	digitSegments[3][0].setIsOn(false); 
						break;
			case "0":	digitSegments[2][0].setIsOn(false); 
						break;
			case "a":	digitSegments[4][0].setIsOn(false); 
						break;
			case "p":	digitSegments[3][1].setIsOn(false);
						digitSegments[4][0].setIsOn(false); 
						break;
			}		
		}

	public void printDigitRow(int rowIndex, int size)
	{
		if(rowIndex%2 == 0) //= segment 0, 3, 6
		{
			System.out.print(" ");
			
			for(int i = 0; i < size; i++)
			{
				System.out.print(digitSegments[rowIndex][0].getSymbol());
			}

			System.out.print(" ");
		}
		else
		{
			if(digitSegments[rowIndex][0].getSymbol().equals("-"))
			{
				System.out.print(" ");
			
				for(int i = 0; i < size; i++)
				{
					System.out.print(digitSegments[rowIndex][0].getSymbol());
				}

				System.out.print(" ");
			}
			else
			{
				System.out.print(digitSegments[rowIndex][0].getSymbol());
				for(int i = 0; i < size; i++)
				{
					System.out.print(" ");	
				}
				System.out.print(digitSegments[rowIndex][1].getSymbol());
			}			
		}

	}
}

class LCDSegment
{
	private Boolean isOn;
	private String onSymbol;
	private String offSymbol = " ";

	public LCDSegment()
	{
		this(true);
	}
	public LCDSegment(boolean isOn)
	{
		this.isOn = isOn;
		onSymbol = "-";
	}
	public LCDSegment(int rowIndex)
	{
		isOn = true;

		if(rowIndex%2 == 0)
		{
			onSymbol = "-";
		}
		else
		{
			onSymbol = "|";
		}		
	}

	public void setIsOn(boolean isOn)
	{
		this.isOn = isOn;
	}

	public String getSymbol()
	{
		if(isOn)
		{
			return onSymbol;
		}
		else
		{
			return offSymbol;
		}
	}	
}

