import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Testtxt {
	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %1$s is ready for use";
	private static final String MESSAGE_INVALID_COMMAND = "This is an invalid command. Pls re-enter command.";
	private static final String MESSAGE_ADD = "added to %1$s: \"%2$s\"";
	private static final String MESSAGE_EMPTY_FILE = "%1$s is empty. Nothing to be displayed";
	private static final String MESSAGE_ERROR = "Error occurred while processing file.";
	private static final String MESSAGE_PRINT = "%1$s. %2$s";
	private static final String MESSAGE_EMPTY_DELETION = "Invalid command. %1$s is empty. Nothing to be deleted.";
	private static final String MESSAGE_DELETE = "deleted from %1$s: \"%2$s\"";
	private static final String MESSAGE_INVALID_DELETE = "Invalid command. There is no line %1$s for deletion";
	private static final String MESSAGE_CLEAR = "all content deleted from %1$s";
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_EXIT = "exit";

	public static void main(String args[]) {
		String fileName = "haha.txt";
		File userFile = openFile(fileName);
		showSystemGreeting(MESSAGE_WELCOME, fileName);
		processCommand(userFile);
	}

	private static File openFile(String fileName) {
		File file = new File(fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	private static void processCommand(File userFile) {
		Scanner sc = new Scanner(System.in);
		String commandLine, commandKey;

		do {
			System.out.print("command: ");
			commandLine = sc.nextLine();
			commandKey = getActionWord(commandLine);
			executeCommand(commandKey, commandLine, userFile);
		} while (!commandKey.equals("exit"));
		sc.close();
	}

	private static void showSystemGreeting(String message, String fileName) {
		System.out.println(String.format(message, fileName));
	}

	private static String getActionWord(String commandLine) {
		String[] tokens = commandLine.split("\\s");
		StringBuilder sb = new StringBuilder();
		sb.insert(0, tokens[0]);
		return sb.toString();
	}

	private static void executeCommand(String commandKey, String commandLine, File userFile) {
		switch (commandKey) {
		case COMMAND_ADD:
			add(commandLine, userFile);
			break;
		case COMMAND_DISPLAY:
			display(userFile);
			break;
		case COMMAND_DELETE:
			delete(commandLine, userFile);
			break;
		case COMMAND_CLEAR:
			clear(userFile);
			break;
		case COMMAND_EXIT:
			exit();
		default:
			showFeedbackMsg(MESSAGE_INVALID_COMMAND);
		}
		return;
	}

	private static void showFeedbackMsg(String message) {
		System.out.println(message);
	}

	private static void add(String commandLine, File userFile) {
		String message = getMessage(commandLine);
		writeToFile(userFile, message);
		showFeedbackMsg(String.format(MESSAGE_ADD, userFile.getName(), message));
	}

	private static void writeToFile(File userFile, String message) {
		try {
			FileWriter fw = new FileWriter(userFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(message).append("\n").toString();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getMessage(String commandLine) {
		String message = commandLine.replace(getActionWord(commandLine), " ");
		return message.trim();
	}

	private static void display(File userFile) {
		String line = null;
		int lineNum = 0;
		if (isEmpty(userFile)) {
			showFeedbackMsg(String.format(MESSAGE_EMPTY_FILE, userFile.getName()));
		} else {
			readAndOutputFile(userFile, line, lineNum);
		}
	}

	private static void readAndOutputFile(File userFile, String line, int lineNum) {
		try {
			FileReader fr = new FileReader(userFile);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				lineNum++;
				showFeedbackMsg(String.format(MESSAGE_PRINT, lineNum, line));
			}
			br.close();
		} catch (FileNotFoundException ex) {
			showFeedbackMsg(MESSAGE_ERROR);
		} catch (IOException ex) {
			showFeedbackMsg(MESSAGE_ERROR);
		}
	}

	private static boolean isEmpty(File userFile) {
		return userFile.length() <= 0;
	}

	private static void delete(String commandLine, File userFile) {
		Vector<String> temp = new Vector<String>();
		extractLineForDelete(temp, commandLine, userFile);
		emptyFile(userFile);
		appendBackNonDeleted(temp, userFile);
	}

	private static void extractLineForDelete(Vector<String> temp, String commandLine, File userFile) {
		int x = getDeletedLineNum(commandLine);
		if (isEmpty(userFile)) {
			showFeedbackMsg(String.format(MESSAGE_EMPTY_DELETION, userFile.getName()));
		} else {
			searchAndStore(temp, userFile, x);
		}
	}

	private static int getDeletedLineNum(String commandLine) {
		try {
			return Integer.parseInt(commandLine.replaceAll("\\D+", ""));
		} catch (NumberFormatException ex) {
			showFeedbackMsg(MESSAGE_ERROR);	
		}
		return -1;
	}

	private static void searchAndStore(Vector<String> temp, File userFile, int x) {
		String line = null;
		int lineNum = 1;
		try {
			FileReader fr = new FileReader(userFile);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				if (lineNum == x) {
					showFeedbackMsg(String.format(MESSAGE_DELETE, userFile.getName(), line));
				} else {
					storeToTemp(temp, line);
				}
				lineNum++;
			}
			br.close();
			if (x >= (lineNum) || x == 0) {
				showFeedbackMsg(String.format(MESSAGE_INVALID_DELETE, x));
			}
		} catch (FileNotFoundException ex) {
			showFeedbackMsg(MESSAGE_ERROR);
		} catch (IOException ex) {
			showFeedbackMsg(MESSAGE_ERROR);
		}
	}

	private static void storeToTemp(Vector<String> temp, String line) {
		temp.add(line);
	}

	private static void appendBackNonDeleted(Vector<String> temp, File userFile) {
		Iterator<String> i = temp.iterator();
		try {
			FileWriter fw = new FileWriter(userFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			while (i.hasNext()) {
				bw.append(i.next()).append("\n").toString();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void clear(File userFile) {
		emptyFile(userFile);
		showFeedbackMsg(String.format(MESSAGE_CLEAR, userFile.getName()));
	}

	private static void emptyFile(File userFile) {
		try {
			FileWriter fw = new FileWriter(userFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			showFeedbackMsg(MESSAGE_ERROR);
			e.printStackTrace();
		}
	}

	private static void exit() {
		System.exit(0);
	}
}