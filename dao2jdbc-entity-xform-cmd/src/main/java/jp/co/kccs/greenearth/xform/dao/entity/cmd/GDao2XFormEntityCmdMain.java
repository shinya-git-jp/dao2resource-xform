package jp.co.kccs.greenearth.xform.dao.entity.cmd;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.xform.entity.jdbc.core.service.GEntityFileDescriptor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GDao2XFormEntityCmdMain {

	public static void main(String[] args) {
		args = new String[6];
		args[0] = "-s";
		args[1] = "D:\\2月のリリース\\settings-oracle.yaml";
		args[2] = "-i";
		args[3] = "D:\\2月のリリース\\dao2-entity-xform(input).yml";
		args[4] = "-o";
		args[5] = "D:\\2月のリリース";
		Map<String, String> parameters = getParameters(args);
		String inputFile = parameters.get("input_file");
		String settingFile = parameters.get("setting_file");
		String outputPath = parameters.get("output_file");
		List<GEntityFileDescriptor> results = export(inputFile, settingFile);
		results.forEach(result-> {
			writeFile(outputPath, result);
		});
		System.out.println("エンティティ生成は成功しました。");
	}
	private static List<GEntityFileDescriptor> export(String inputFile, String settingFile) {
		GDao2XFormEntityCmdService service = GDao2XFormEntityCmdService.getInstance();
		return service.export(inputFile, settingFile);
	}
	private static Map<String, String> getParameters(String[] args) {
		Map<String, String> parameters = new HashMap<>();
		for (int i = 0; i < args.length - 1; i += 2) {
			parameters.putAll(getParameter(args[i], args[i + 1]));
		}
		return parameters;
	}
	private static Map<String, String> getParameter(String argName, String argValue) {
		if (argName.equals("-s")) {
			return Map.of("setting_file", argValue);
		} else if (argName.equals("-o")) {
			return Map.of("output_file", argValue);
		} else if (argName.equals("-i")) {
			return Map.of("input_file", argValue);
		}
		throw new GFrameworkException("Parameter is error!");
	}

	private static void writeFile(String outputPath, GEntityFileDescriptor entityFileDescriptor) {
		String output = outputPath + "/" + entityFileDescriptor.getName() + ".entity";
		try {
			Files.write(Paths.get(output), entityFileDescriptor.getResult());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
