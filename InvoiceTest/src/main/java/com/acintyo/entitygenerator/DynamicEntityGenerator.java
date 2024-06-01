package com.acintyo.entitygenerator;

import java.io.FileWriter;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class DynamicEntityGenerator {

    public static void generateAndCompileDynamicEntity(String packageName, String className, String[] columnNames) {
//        Map<String, Class<?>> columnTypes = new HashMap<>(); // Simulated column types

        // Create the class definition
        StringBuilder classDefinition = new StringBuilder();
        classDefinition.append("package ").append(packageName).append(";\n\n");
        classDefinition.append("import jakarta.persistence.Id;\n");
        classDefinition.append("import jakarta.persistence.Entity;\n\n");
        classDefinition.append("import lombok.AllArgsConstructor;\n");
        classDefinition.append("import lombok.NoArgsConstructor;\n");
        classDefinition.append("import lombok.Data;\n\n");
        classDefinition.append("@Data\n");
        classDefinition.append("@AllArgsConstructor\n");
        classDefinition.append("@NoArgsConstructor\n");
        classDefinition.append("@Entity\n");
        
        classDefinition.append("public class ").append(className).append(" {\n");
        classDefinition.append("\t@Id\n"); // Assuming the first column is the primary key

        // Add fields with appropriate annotations based on column names
        for (String columnName : columnNames) {
                classDefinition.append("\tprivate ").append("String").append(" ").append(columnName).append(";\n");
        }

        // Close the class definition
        classDefinition.append("}");

        // Write the class definition to a .java file
        String filePath = "D://AcintyoProjects//Invoice//src//main//java//"+packageName.replace('.', '/') + "/" + className + ".java";
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(classDefinition.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Compile the generated .java file
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int compilationResult = compiler.run(null, null, null, filePath);
        if (compilationResult == 0) {
            System.out.println("Compilation is successful");
        } else {
            System.out.println("Compilation failed");
        }

        try {
			Class.forName("com.acintyo.entity."+className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Delete the generated .java file
//        File file = new File(filePath);
//        file.delete();
    }
}
