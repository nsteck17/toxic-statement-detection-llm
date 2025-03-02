package spring.ai.example.spring_ai_demo;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class SpringAiDemoApplication {

	@Bean
	public CommandLineRunner runner(ChatClient.Builder builder) {
	    return args -> {
			//callChatGPT(builder);\
			//createExcelFile(new Date());

			//iterations for each version; so all are available
			//int version = 0; // mock reply
			//int version = 1; // Just a massive string input
			//int version = 2; // clarification rules 8b and 8c
			int version = 3; // refactoring rule approach to LLM 8b and 8c properly and update 8c
			runFullProgram(builder,version);
	    };
	}

	private List<String> chatPrelude = new ArrayList<String>();

	private List<Message> getChatPrelude() {
		List<Message> chatPrelude = new ArrayList<>();
		chatPrelude.add(new UserMessage("Hello, how are you?"));
		chatPrelude.add(new AssistantMessage("I'm good, thank you! How can I assist you today?"));
		chatPrelude.add(new UserMessage("Can you tell me a joke?"));
		chatPrelude.add(new AssistantMessage("Sure! Why don't scientists trust atoms? Because they make up everything!"));
		return chatPrelude;
	}

	private void callChatGPTWithHistory(ChatClient.Builder builder) {
		// Set the temperature value option
		ChatOptions options = ChatOptions.builder()
				.temperature(0.2)
				.build();
		builder.defaultOptions(options);
		ChatClient chatClient = builder.build();
		Prompt prompt = new Prompt();

		// Create a conversation history
		List<Message> conversationHistory = new ArrayList<>();
		conversationHistory.add(new UserMessage("Hello, how are you?"));
		conversationHistory.add(new AssistantMessage("I'm good, thank you! How can I assist you today?"));
		conversationHistory.add(new UserMessage("Can you tell me a joke?"));
		conversationHistory.add(new AssistantMessage("Sure! Why don't scientists trust atoms? Because they make up everything!"));

		// Add the conversation history to the prompt
		prompt.getInstructions().addAll(conversationHistory);

		// Add the new user message to the prompt
		UserMessage newUserMessage = new UserMessage("Can you tell me another joke?");
		prompt.getInstructions().add(newUserMessage);

		// Get the response from the chat client
		String response = chatClient.prompt(prompt).call().content();
		System.out.println(response);
	}

	private void callChatGPT(ChatClient.Builder builder) {
		ChatClient chatClient = builder.build();
		String response = chatClient.prompt("What's the weather in Omaha tomorrow?").call().content();
		System.out.println(response);
	}

	public static void runFullProgram(ChatClient.Builder builder, int version) throws IOException {

		//INPUT dataset of comments
		XlsxReader xlsxReader = new XlsxReader();
		List<List<String>> xlsxContents = xlsxReader.readXlsxVersion3("toxicity_dataset_assignment_01-patched.xlsx");
		xlsxContents.remove(0); //Remove header row

		List<CommentToxicDataSetEntry> records = extractedCommentBaseline(xlsxContents);

		// If I wanted to test multiple prompts... probably won't use the List structure tho
		List<String> promptList = new ArrayList<String>();
		if(version == 1){
			promptList.add("Yes or no, then why? Is this a toxic set of text from developers talking about code: ");
		}else if (version == 2){
			String initialPrompt = "Yes or no, then why? Is this a toxic set of text from developers talking about code: ";
			String killClarification = "Rule 8b: If 'kill' is used in a context that is not related to code, it is toxic. If it is used in a code context, it is not toxic. /n ";
			String demeaningClarification = "Rule 8c: If the demeaning word could have been replaced with a less offensive word, it is toxic”“Rule 8c: If the demeaning word could have been replaced with a less offensive word, it is toxic. /n";
			promptList.add(killClarification + demeaningClarification + initialPrompt);
		}else if(version == 3){
			promptList.add("Yes or no, then why? Is this a toxic set of text from developers talking about code: ");

		}

		String inputToChatGpt = "";

		AnalysisSummary analysisSummary = new AnalysisSummary();

		List<CommentToxicAnalysis> commentToxicAnalysisList = new ArrayList<CommentToxicAnalysis>();
		System.out.println("===========================================================");
		System.out.println("=============LOADING PROMPTS AND COMMENTS==================");
		System.out.println("===========================================================");
		//Loop over prompts and then for each comment feed them into chatgpt and get response
		for (String prompt : promptList) {
			int commentIndex = 0;
			for (CommentToxicDataSetEntry commentInput : records) {
				commentIndex++;
				System.out.println("Comment #" + commentIndex);

				CommentToxicAnalysis commentToxicAnalysis = new CommentToxicAnalysis();
				commentToxicAnalysis.setOriginalComment(commentInput.getComment());
				commentToxicAnalysis.setOriginalToxic(commentInput.isToxic());

				analysisSummary.totalRecords++;
				//if original is toxic, increment totalOriginalToxic
				if (commentInput.isToxic()) {
					analysisSummary.totalOriginalToxic++;
				} else {
					analysisSummary.totalOriginalNotToxic++;
				}

				String response = "";
				try{
					//Call ChatGPT
					if(version == 0){
						//Generate random  yes or no string
						response = getRandomYesOrNo();
					} else if(version == 1 || version == 2){
						inputToChatGpt = buildSimplePrompt(prompt, commentInput.getComment());

						ChatClient chatClient = builder.build();
						response = chatClient.prompt(inputToChatGpt).call().content();
					} else if(version == 3){
						inputToChatGpt = buildSimplePromptRevised(prompt, commentInput.getComment());

						ChatClient chatClient = builder.build();
						response = chatClient.prompt(inputToChatGpt).call().content();
					}
					commentToxicAnalysis.setReplyFromChatGpt(response);
				} catch (Exception e) {
					commentToxicAnalysis.setEvaluationValue(Evaluation.ERROR);
					commentToxicAnalysis.setErrorReason(ErrorReason.REPLY_ERROR);
					analysisSummary.totalErrorReply++;
				}

				//Process Response
				if (containsIgnoreCase(response,"yes")) {
					commentToxicAnalysis.setReplyToxic(true);
					analysisSummary.totalReplyToxic++;

					if (commentInput.isToxic()) {
						//True Positive
						commentToxicAnalysis.setEvaluationValue(Evaluation.TRUE_POSITIVE);
						analysisSummary.totalTruePositives++;
					} else {
						//False Positive
						commentToxicAnalysis.setEvaluationValue(Evaluation.FALSE_POSITIVE);
						analysisSummary.totalFalsePositives++;
					}
				} else if (containsIgnoreCase(response,"no")){
					commentToxicAnalysis.setReplyToxic(false);
					analysisSummary.totalReplyNotToxic++;

					if (commentInput.isToxic()) {
						//False Negative
						commentToxicAnalysis.setEvaluationValue(Evaluation.FALSE_NEGATIVE);
						analysisSummary.totalFalseNegatives++;
					} else {
						//True Negative
						commentToxicAnalysis.setEvaluationValue(Evaluation.TRUE_NEGATIVE);
						analysisSummary.totalTrueNegatives++;
					}
				} else {
					commentToxicAnalysis.setToxicUnsure(true);
					commentToxicAnalysis.setEvaluationValue(Evaluation.ERROR);
					commentToxicAnalysis.setErrorReason(ErrorReason.UNABLE_TO_UNDERSTAND_REPLY);
					analysisSummary.totalReplyUnsure++;
					//System.out.println("Unsure");
				}

				//Default
				if(commentToxicAnalysis.getErrorReason() == null){
					commentToxicAnalysis.setErrorReason(ErrorReason.NONE);
				}

				commentToxicAnalysisList.add(commentToxicAnalysis);
			}
		}

		Date endDate = new Date();

		//Create excel output based on date and time of run
		System.out.println("===========================================================");
		System.out.println("=============EXCEL OUTPUT==================");
		System.out.println("===========================================================");
		createExcelFile(endDate,commentToxicAnalysisList, version);

		//Create text file output based analysisSummary
		System.out.println("===========================================================");
		System.out.println("=============SUMMARY OUTPUT==================");
		System.out.println("===========================================================");
		String writeToFile =
				"Original Prompt: " + inputToChatGpt
				+ System.lineSeparator()
				+ analysisSummary.toString();
		writeStringToFile(writeToFile, "AnalysisSummary",endDate, version);


		System.out.println("===========================================================");
		System.out.println("=============DONE==================");
		System.out.println("===========================================================");
	}

	public static void writeStringToFile(String content,
										 String filePath,
										 Date dateOf,
										 int version
										 ) throws IOException {
		try (FileWriter fileWriter = new FileWriter(
				filePath+"_"
						+new SimpleDateFormat("yyyyMMdd_HHmmss").format(dateOf)
						+"_v"+version
						+".txt")) {
			fileWriter.write(content);
		}
	}

	public static void createExcelFile(Date currentDate,
									   List<CommentToxicAnalysis> commentToxicAnalysisList,
									   int version) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Run Data");

		Row headerRow = sheet.createRow(0);
		Cell headerCell = headerRow.createCell(0);
		headerCell.setCellValue("Original Comment");
		Cell headerCell1 = headerRow.createCell(1);
		headerCell1.setCellValue("Is Original Toxic");
		Cell headerCell2 = headerRow.createCell(2);
		headerCell2.setCellValue("ChatGPT Reply");
		Cell headerCell3 = headerRow.createCell(3);
		headerCell3.setCellValue("Is Reply Toxic");
		Cell headerCell4 = headerRow.createCell(4);
		headerCell4.setCellValue("Is Toxic Unsure");
		Cell headerCell5 = headerRow.createCell(5);
		headerCell5.setCellValue("Evaluation Value");
		Cell headerCell6 = headerRow.createCell(6);
		headerCell6.setCellValue("Error Reason");

		//Iterate over commentToxicAnalysisList and write to excel each row
		for (int i = 0; i < commentToxicAnalysisList.size(); i++) {
			CommentToxicAnalysis commentToxicAnalysis = commentToxicAnalysisList.get(i);
			Row dataRow = sheet.createRow(i + 1);
			Cell dataCell = dataRow.createCell(0);
			dataCell.setCellValue(commentToxicAnalysis.getOriginalComment());
			Cell dataCell1 = dataRow.createCell(1);
			dataCell1.setCellValue(commentToxicAnalysis.isOriginalToxic());
			Cell dataCell2 = dataRow.createCell(2);
			dataCell2.setCellValue(commentToxicAnalysis.getReplyFromChatGpt());
			Cell dataCell3 = dataRow.createCell(3);
			dataCell3.setCellValue(commentToxicAnalysis.isReplyToxic());
			Cell dataCell4 = dataRow.createCell(4);
			dataCell4.setCellValue(commentToxicAnalysis.isToxicUnsure());
			Cell dataCell5 = dataRow.createCell(5);
			dataCell5.setCellValue(commentToxicAnalysis.getEvaluationValue().toString());
			Cell dataCell6 = dataRow.createCell(6);
			dataCell6.setCellValue(commentToxicAnalysis.getErrorReason().toString());
		}

		/*
		Row dataRow = sheet.createRow(1);
		Cell dataCell = dataRow.createCell(0);
		String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		dataCell.setCellValue(currentDateTime);
		*/

		String fileName = "RunData_"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ "_v" + version
				+ ".xlsx";
		try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
			workbook.write(fileOut);
		}

		workbook.close();
	}

	public static String getRandomYesOrNo() {
		Random random = new Random();
		return random.nextBoolean() ? "Yes" : "No";
	}

	private static List<CommentToxicDataSetEntry> extractedCommentBaseline(List<List<String>> xlsxContents) {
		List<CommentToxicDataSetEntry> records = new ArrayList<CommentToxicDataSetEntry>();
		for (List<String> row : xlsxContents) {
			String comment = row.get(0);
			boolean isToxic = Double.valueOf(row.get(1)) == 1.0;
			CommentToxicDataSetEntry entry = new CommentToxicDataSetEntry(comment, isToxic);
			//System.out.println(entry);
			records.add(entry);
		}
		return records;
	}

	public static boolean containsIgnoreCase(String str, String searchStr) {
		if (str == null || searchStr == null) {
			return false;
		}
		return str.toLowerCase().contains(searchStr.toLowerCase());
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringAiDemoApplication.class, args);
	}

	public static String buildSimplePrompt(String prompt, String comment) {
		String ruleBook = "These are rules to use to label the following Software Engineering statements as \"Toxic\" with \"YES\" or \"Non-Toxic\" with \"NO\". \n" +
				"\n" +
				"Rule 1: If a text includes profane or curse words, it would be marked as \"toxic\"\n" +
				"Rational: Profanities are the most common sources of online toxicities. \n" +
				"Example: \"fuck! Consider it done!\" is toxic. \n" +
				"\n" +
				"Rule 2: If a text includes an acronym, which generally refers to an expletive or swearing, it would be marked as \"toxic\"\n" +
				"Rational: Sometimes people use acronyms of profanities, which are equally toxic as their expanded form.\n" +
				"Example: \"WTF are you doing!\"\n" +
				"\n" +
				"Rule 3: Insulting remarks regarding another person or entities would be marked as \"toxic\" \n" +
				"Rational: Insulting another developer may create a toxic environment and should not be encouraged.\n" +
				"Example: \"...shut up, smartypants.\"\n" +
				"\n" +
				"Rule 4: Attacking a person’s identity  (e.g., race, religion, nationality, gender, or sexual orientation) would be marked as \"toxic\"\n" +
				"Rational: Identity attacks are considered toxic among all categories of online conversations.\n" +
				"Example: \"Stupid fucking superstitious Christians.\"\n" +
				"\n" +
				"Rule 5: Aggressive behavior or threatening another person or a community would be marked as \"toxic\"\n" +
				"Rational: Aggregations or threats may stir hostility between two developers and force the recipients to leave the community.\n" +
				"Example: \"yeah, but I’d really give a lot for an opportunity to punch them in the face.\"\n" +
				"\n" +
				"Rule 6: Both implicit or explicit references to sexual activities would be marked as \"toxic\" \n" +
				"Rational: Implicit or explicit references to sexual activities may make some developers, particularly females, uncomfortable and make them leave a conversation.\n" +
				"Example: \"This code makes me so horny. It’s beautiful.\"\n" +
				"\n" +
				"Rule 7: Flirtations would be marked as \"toxic\"\n" +
				"Rational: Flirtations may also make a developer uncomfortable and make a recipient avoid the other person during future collaborations.\n" +
				"Example: \"I really miss you my girl.\"\n" +
				"\n" +
				"Rule 8: If a demeaning word (e.g., \"dumb,\" \"stupid,\" \"idiot,\" \"ignorant\") refers to either the writer him/herself or his/her work, the sentence would not be marked as \"toxic\" if it does not fit any of the first seven rules.\n" +
				"Rational: It is common in the SE community to use those words for expressing their own mistakes. In those cases, the use of those toxic words with regard to themselves or their work does not make toxic meaning. Although such texts are unprofessional, they do not degrade future communication or collaboration.\n" +
				"Example: \"I’m a fool and didn’t get the point of the deincrement. It makes sense now.\"\n" +
				"\n" +
				"Rule 9: A sentence that does not fit rules 1 through 8 would be marked as \"non-toxic.\" \n" +
				"Rational: General non-toxic comments. \n" +
				"Example: \"I think ResourceWithProps should be there instead of GenericResource.\"" +
				"\n";

		String definitions = "A definition of a toxic statement related to software engineering is a statement that makes the user reading it feel negative feelings. \n" +
				"\n" +
				"A definition that not-toxic for software engineering is a statement that makes the user reading it feel happy. It might make a person laugh. A joke that does not relate to the code written or the person reason is not-toxic." +
				"\n";

		return ruleBook + definitions + prompt + comment;
	}

	public static String buildSimplePromptRevised(String prompt, String comment) {
		String ruleBookV2 = "These are rules to use to label the following Software Engineering statements as \"Toxic\" with \"YES\" or \"Non-Toxic\" with \"NO\". \n" +
				"\n" +
				"Rule 1: If a text includes profane or curse words, it would be marked as \"toxic\"\n" +
				"Rational: Profanities are the most common sources of online toxicities. \n" +
				"Example: \"fuck! Consider it done!\" is toxic. \n" +
				"\n" +
				"Rule 2: If a text includes an acronym, which generally refers to an expletive or swearing, it would be marked as \"toxic\"\n" +
				"Rational: Sometimes people use acronyms of profanities, which are equally toxic as their expanded form.\n" +
				"Example: \"WTF are you doing!\"\n" +
				"\n" +
				"Rule 3: Insulting remarks regarding another person or entities would be marked as \"toxic\" \n" +
				"Rational: Insulting another developer may create a toxic environment and should not be encouraged.\n" +
				"Example: \"...shut up, smartypants.\"\n" +
				"\n" +
				"Rule 4: Attacking a person’s identity  (e.g., race, religion, nationality, gender, or sexual orientation) would be marked as \"toxic\"\n" +
				"Rational: Identity attacks are considered toxic among all categories of online conversations.\n" +
				"Example: \"Stupid fucking superstitious Christians.\"\n" +
				"\n" +
				"Rule 5: Aggressive behavior or threatening another person or a community would be marked as \"toxic\"\n" +
				"Rational: Aggregations or threats may stir hostility between two developers and force the recipients to leave the community.\n" +
				"Example: \"yeah, but I’d really give a lot for an opportunity to punch them in the face.\"\n" +
				"\n" +
				"Rule 6: Both implicit or explicit references to sexual activities would be marked as \"toxic\" \n" +
				"Rational: Implicit or explicit references to sexual activities may make some developers, particularly females, uncomfortable and make them leave a conversation.\n" +
				"Example: \"This code makes me so horny. It’s beautiful.\"\n" +
				"\n" +
				"Rule 7: Flirtations would be marked as \"toxic\"\n" +
				"Rational: Flirtations may also make a developer uncomfortable and make a recipient avoid the other person during future collaborations.\n" +
				"Example: \"I really miss you my girl.\"\n" +
				"\n" +
				"Rule 8: If a demeaning word (e.g., \"dumb,\" \"stupid,\" \"idiot,\" \"ignorant\") refers to either the writer him/herself or his/her work, the sentence would not be marked as \"toxic\" if it does not fit any of the first seven rules.\n" +
				"Rational: It is common in the SE community to use those words for expressing their own mistakes. In those cases, the use of those toxic words with regard to themselves or their work does not make toxic meaning. Although such texts are unprofessional, they do not degrade future communication or collaboration.\n" +
				"Example: \"I’m a fool and didn’t get the point of the deincrement. It makes sense now.\"\n" +
				"\n" +
				"Rule 9: If 'kill' is used in a context that is not related to code, it is toxic. If it is used in a code context, it is not toxic.\n" +
				"Rational:  Rule 5 and Rule 8 may incorrectly flag a comment as toxic when it is not.\n" +
				"\n" +
				"Rule 10: If the slang or demeaning word is sarcastic and has positive context, it is actually offensive. \n" +
				"Rational: Rule 8 would incorrectly flag a 'toxic' comment as 'non-toxic' because it was not directed towards an individual but it was still offensive. The toxic comment or demeaning words may be covered up by the positive around it. \n" +
				"\n" +
				"Rule 11: A sentence that does not fit rules 1 through 10 would be marked as \"non-toxic.\" \n" +
				"Rational: General non-toxic comments. \n" +
				"Example: \"I think ResourceWithProps should be there instead of GenericResource.\"" +
				"\n"
				;

		String definitions = "A definition of a toxic statement related to software engineering is a statement that makes the user reading it feel negative feelings. \n" +
				"\n" +
				"A definition that not-toxic for software engineering is a statement that makes the user reading it feel happy. It might make a person laugh. A joke that does not relate to the code written or the person reason is not-toxic." +
				"\n";

		return ruleBookV2 + definitions + prompt + comment;
	}
}
