import java.io.*;
//***************************************************************************
//Class:		TextFileClass
//Description:	Provides for reading the contents of a text file
//Uses:			KeyboardInputClass
//Author:       Steve Donaldson
//Revised:      1/23/2014

class TextFileClass {
	String fileName;
	static final int MAX_TEXT_ROWS = 6000;	//if the first constructor is used this is the maximum number of rows of text
											//that can be stored in the text[] array
	String text[];							//the text read from the file (one line per row)
	int lineCount;							//the actual number of rows of text in the text[] array
	KeyboardInputClass keyboardInput = new KeyboardInputClass();
	//************************************************************************
	//Method:		TextFileClass
	//Description:	Constructor - used when it is known that the max rows of text is < MAX_TEXT_ROWS
	//Parameters:	none
	//Returns:		nothing
	//Throws		nothing
	//Calls:		nothing
	TextFileClass() {
		text = new String[MAX_TEXT_ROWS];
		lineCount = 0;
	}
	//************************************************************************
	//Method:		TextFileClass
	//Description:	Constructor - used when a specific # of rows of text are to be read or when the # of rows is unknown but
    //                              is likely larger than MAX_TEXT_ROWS. Will result in two passes on the file in order to get
    //                              the text (one to count the # of lines and allocate space in the array and one to load it).
	//Parameters:	maxTextRows - maximum number of rows that can be read from the file and stored in the text[] array. If a
	//				value of zero is passed, space for the array is not assigned until the getFileContents method is called.
	//Returns:		nothing
	//Throws		nothing
	//Calls:		nothing
	TextFileClass(int maxTextRows) {
		if (maxTextRows > 0)
			text = new String[maxTextRows];
		lineCount = 0;
	}
	//************************************************************************
	//Method:		getFileName
	//Description:	Gets the name of a text file from the user
	//Parameters:	prompt - descriptive text telling the user what to enter
	//Returns:		nothing
	//Throws		nothing
	//Calls:		getKeyboardInput from class KeyboardInputClass
	public void getFileName(String prompt) {
		fileName=keyboardInput.getKeyboardInput(prompt);
		return;
	}
	//************************************************************************
	//Method:		getFileContents
	//Description:	Reads the contents of a specified text file. If space has not yet been allocated for the text[] array,
	//				this routine makes two passes on the file--one to determine how many rows of text it contains (so space
	//				for the text[] array can be allocated) and the second to actually load the array.
	//Parameters:	none
	//Returns:		lineCount - the number of lines read. A value of 0 can be interpreted to mean that nothing was read,
	//				perhaps due to a problem with the file contents, inability to locate it, etc.
	//Throws		Exception (and displays a generic error message)
	//Calls:		no user defined methods
	public int getFileContents() {
		String s;
        int maxRows = MAX_TEXT_ROWS;
		int passStart = 2;
		if (text == null) {
			passStart = 1;
            maxRows = 50000000;
        }
		for (int pass = passStart; pass <= 2; pass++) {
			lineCount = 0;
			try {
				FileReader reader = new FileReader(fileName);
				BufferedReader buffer = new BufferedReader(reader);
				s = buffer.readLine();
				while ((s != null) && (lineCount < maxRows)) {
					if (pass == 2)
						text[lineCount] = s;
					lineCount++;
					s = buffer.readLine();
				}
				reader.close();
			}
			catch (Exception e) {
				keyboardInput.getKeyboardInput("Problem trying to access or read file. Press ENTER to continue...");
				pass = 3;
			}
			if(pass==1)
				text = new String[lineCount];
		}
		return lineCount;
	}
	//************************************************************************
}
//***************************************************************************
//***************************************************************************
//Here is how to begin using it:
//TextFileClass textFile = new TextFileClass();
//textFile.getFileName("Specify the text file to be read");
//if (textFile.fileName.length()>0) {/*do something here!*/}

