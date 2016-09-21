//**************************************************************************************************************************
//**************************************************************************************************************************
//Class:		KeyboardInputClass
//Description:	Provides multiple methods for entering information from the keyboard for console based programs.
//Author:		Steve Donaldson
//Revised:		August 6, 2013
import java.io.*;
class KeyboardInputClass {
	//**********************************************************************************************************************
	//Method:		getKeyboardInput
	//Description:	Permits keyboard input for strings
	//Parameters:	prompt - descriptive text telling the user what to enter
	//Returns:		inputString	- the entered text (i.e., the user's response). Note that even though this is a string,
	//								it can be converted to an integer, double, etc. if necessary in the client routine.
	//Throws:		Exception (but doesn't do anything with it!)
	//Calls:		nothing
	public String getKeyboardInput(String prompt) 
	{
		String inputString="";
		System.out.println(prompt);
		try {
			InputStreamReader reader=new InputStreamReader(System.in);
			BufferedReader buffer=new BufferedReader(reader);
			inputString=buffer.readLine();
		}
		catch (Exception e) {}
		return inputString;
	}
	//**********************************************************************************************************************
	//Method:		getCharacter
	//Description:	Gets a character (char) from the keyboard. If validateInput=true, the routine loops until the user entry
	//				matches defaultResult (which may be obtained just by pressing the ENTER key without entering anything)
	//				or is one of the validEntries.
	//Parameters:	validateInput		- true=make sure the entered character is in validEntries; false=accept any
	//										character that is entered
	//				defaultResult		- character to be returned if the user enters no character (i.e., just presses
	//										ENTER). If validateInput=true, the method assumes that this is a valid entry
	//										even if it is not explicitly included in validEntries (i.e., the method will
	//										add it to validEntries).
	//				validEntries		- acceptable characters if validateInput = true. Note: if validation is to be
	//										performed, then unless one of the case conversion modes is specified, the user
	//										entry must match one of the validEntries characters exactly in order to be
	//										accepted.
	//				caseConversionMode	- 0=no conversion occurs; 1=the entered character is converted to uppercase before
	//										being checked against validEntries and before being returned; 2= the entered
	//										character is converted to lowercase before being checked against validEntries
	//										and before being returned. Note: both case conversion modes 1 and 2 also convert
	//										validEntries to the specified case prior to checking the validity of the entry.
	//										If validateInput=false, this parameter is ignored.
	//				prompt				- descriptive text prompting the user for an entry
	//Returns:		result				- the character entered by the user or defaultResult if no character was entered
	//Calls:		getKeyboardInput
	public char getCharacter(boolean validateInput, char defaultResult, String validEntries, int caseConversionMode, String prompt) {
		if (validateInput) {
			if (caseConversionMode == 1) {
				validEntries = validEntries.toUpperCase();
				defaultResult = Character.toUpperCase(defaultResult);
			}
			else if (caseConversionMode == 2) {
				validEntries = validEntries.toLowerCase();
				defaultResult = Character.toLowerCase(defaultResult);
			}
			if ((validEntries.indexOf(defaultResult) < 0))								//if default not in validEntries
				validEntries = (new Character(defaultResult)).toString() + validEntries;//then add it
		}
		String inputString="";
		char result = defaultResult;
		boolean entryAccepted = false;
		while (!entryAccepted) {
			result = defaultResult;
			entryAccepted = true;
			inputString = getKeyboardInput(prompt);
			if (inputString.length() > 0) {
				result = (inputString.charAt(0));
				if (caseConversionMode == 1)
					result = Character.toUpperCase(result);
				else if (caseConversionMode == 2)
					result = Character.toLowerCase(result);
			}
			if (validateInput)
				if (validEntries.indexOf(result) < 0) {
					entryAccepted = false;
					System.out.println("Invalid entry. Select an entry from the characters shown in brackets: [" + validEntries + "]");
				}
		}
		return result;
	}
	//**********************************************************************************************************************
	//Method:		getInteger
	//Description:	Gets an integer (int) from the keyboard. If validateInput=true, the routine loops until the user entry
	//				matches defaultResult (which may be obtained just by pressing the ENTER key without entering anything)
	//				or falls within the range specified by minAllowableResult and maxAllowableResult.
	//Parameters:	validateInput		- true=make sure the entered integer equals the default or is in in the allowable
	//										range specified by minAllowableResult and maxAllowableResult; false=accept any
	//										integer that is entered
	//				defaultResult		- integer to be returned if the user enters nothing (i.e., just presses (ENTER).
	//										If validateInput=true, the method assumes that this is a valid entry
	//										even if it is not explicitly included in the specified range.
	//				minAllowableResult	- the minimum allowable value for the user entry (if validateEntries=true)
	//				maxAllowableResult	- the maximum allowable value for the user entry (if validateEntries=true)
	//										Note: if validateInput=false,these values are ignored
	//				prompt				- descriptive text prompting the user for an entry
	//Returns:		result				- the integer entered by the user or defaultResult if no integer was entered
	//Calls:		getKeyboardInput
	public int getInteger(boolean validateInput, int defaultResult, int minAllowableResult, int maxAllowableResult, String prompt) {
		String inputString = "";
		int result = defaultResult;
		boolean entryAccepted = false;
		while (!entryAccepted) {
			result = defaultResult;
			entryAccepted = true;
			inputString = getKeyboardInput(prompt);
			if (inputString.length() > 0) {
				try {
					result = Integer.parseInt(inputString);
				}
				catch (Exception e) {
					entryAccepted = false;
					System.out.println("Invalid entry...");
				}
			}
			if (entryAccepted && validateInput) {
				if ((result != defaultResult) && ((result < minAllowableResult) || (result > maxAllowableResult))) {
					entryAccepted = false;
					System.out.println("Invalid entry. Allowable range is " + minAllowableResult + "..." + maxAllowableResult + " (default = " + defaultResult + ").");
				}
			}
		}
		return result;
	}
	//**********************************************************************************************************************
	//Method:		getLong
	//Description:	Gets a long integer (long) from the keyboard. If validateInput=true, the routine loops until the user entry
	//				matches defaultResult (which may be obtained just by pressing the ENTER key without entering anything)
	//				or falls within the range specified by minAllowableResult and maxAllowableResult.
	//Parameters:	validateInput		- true=make sure the entered long integer equals the default or is in in the allowable
	//										range specified by minAllowableResult and maxAllowableResult; false=accept any
	//										integer that is entered
	//				defaultResult		- long integer to be returned if the user enters nothing (i.e., just presses (ENTER).
	//										If validateInput=true, the method assumes that this is a valid entry
	//										even if it is not explicitly included in the specified range.
	//				minAllowableResult	- the minimum allowable value for the user entry (if validateEntries=true)
	//				maxAllowableResult	- the maximum allowable value for the user entry (if validateEntries=true)
	//										Note: if validateInput=false,these values are ignored
	//				prompt				- descriptive text prompting the user for an entry
	//Returns:		result				- the long integer entered by the user or defaultResult if nothing was entered
	//Calls:		getKeyboardInput
	public long getLong(boolean validateInput, long defaultResult, long minAllowableResult, long maxAllowableResult, String prompt) {
		String inputString = "";
		long result = defaultResult;
		boolean entryAccepted = false;
		while (!entryAccepted) {
			result = defaultResult;
			entryAccepted = true;
			inputString = getKeyboardInput(prompt);
			if (inputString.length() > 0) {
				try {
					result = Long.parseLong(inputString);
				}
				catch (Exception e) {
					entryAccepted = false;
					System.out.println("Invalid entry...");
				}
			}
			if (entryAccepted && validateInput) {
				if ((result != defaultResult) && ((result < minAllowableResult) || (result > maxAllowableResult))) {
					entryAccepted = false;
					System.out.println("Invalid entry. Allowable range is " + minAllowableResult + "..." + maxAllowableResult + " (default = " + defaultResult + ").");
				}
			}
		}
		return result;
	}
	//**********************************************************************************************************************
	//Method:		getDouble
	//Description:	Gets a double from the keyboard. If validateInput=true, the routine loops until the user entry
	//				matches defaultResult (which may be obtained just by pressing the ENTER key without entering anything)
	//				or falls within the range specified by minAllowableResult and maxAllowableResult.
	//Parameters:	validateInput		- true=make sure the entered double equals the default or is in in the allowable
	//										range specified by minAllowableResult and maxAllowableResult; false=accept any
	//										double that is entered
	//				defaultResult		- double to be returned if the user enters nothing (i.e., just presses (ENTER).
	//										If validateInput=true, the method assumes that this is a valid entry
	//										even if it is not explicitly included in the specified range.
	//				minAllowableResult	- the minimum allowable value for the user entry (if validateEntries=true)
	//				maxAllowableResult	- the maximum allowable value for the user entry (if validateEntries=true)
	//										Note: if validateInput=false,these values are ignored
	//				prompt				- descriptive text prompting the user for an entry
	//Returns:		result				- the double entered by the user or defaultResult if no double was entered
	//Calls:		getKeyboardInput
	public double getDouble(boolean validateInput, double defaultResult, double minAllowableResult, double maxAllowableResult, String prompt) {
		String inputString = "";
		double result = defaultResult;
		boolean entryAccepted = false;
		while (!entryAccepted) {
			result = defaultResult;
			entryAccepted = true;
			inputString = getKeyboardInput(prompt);
			if (inputString.length() > 0) {
				try {
					result = Double.parseDouble(inputString);
				}
				catch (Exception e) {
					entryAccepted = false;
					System.out.println("Invalid entry...");
				}
			}
			if (entryAccepted && validateInput) {
				if ((result != defaultResult) && ((result < minAllowableResult) || (result > maxAllowableResult))) {
					entryAccepted = false;
					System.out.println("Invalid entry. Allowable range is " + minAllowableResult + "..." + maxAllowableResult + " (default = " + defaultResult + ").");
				}
			}
		}
		return result;
	}
	//**********************************************************************************************************************
	//Method:		getString
	//Description:	Gets a string of alphanumeric text from the keyboard. If the ENTER key is pressed without anything else
	//				being entered, returns defaultResult.
	//Parameters:	defaultResult	- the string to be returned if the user enters nothing (i.e., just presses (ENTER).
	//				prompt			- descriptive text prompting the user for an entry
	//Returns:		result			- the string entered by the user or defaultResult if no string was entered
	//Calls:		getKeyboardInput
	public String getString(String defaultResult, String prompt) {
		String result = getKeyboardInput(prompt);
		if (result.length() == 0)
			result = defaultResult;
		return result;
	}
	//**********************************************************************************************************************
}
//**************************************************************************************************************************
//**************************************************************************************************************************

//Here is how to use it... (remove the comments!)
//KeyboardInputClass keyboardInput = new KeyboardInputClass();
//String userInput="";
//userInput=keyboardInput.getKeyboardInput("Specify the string to be processed");
