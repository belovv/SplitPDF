package com.split;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Split {
    public static void main(String[] args) {
        File input = new File("C:\\Users\\Vasil\\Desktop\\do\\one.pdf");
        try {
            readAndWritePDF(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getFile();
    }

    private static String getFile() {
        Path path = FileSystems.getDefault().getPath("");
        String file = null;
        try (Stream<Path> streamDir = Files.find(path, 5,
                (p, a) -> String.valueOf(p).endsWith(".pdf"))) {
            file = streamDir
                    .map(String::valueOf)
                    .collect(Collectors.joining());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static void readAndWritePDF(File input) throws IOException {
        PDDocument inputDocument = PDDocument.load(input);
        PDFTextStripper stripper = new PDFTextStripper();
        PDDocument outputDocument = new PDDocument();
        String text;
        String number = "no number";
        for (int page = 1; page <= inputDocument.getNumberOfPages(); ++page) {
            stripper.setStartPage(page);
            stripper.setEndPage(page);
            text = stripper.getText(inputDocument);
            Pattern pattern = Pattern.compile("\\D\\s(\\d){2,7}");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                number = matcher.group();
            }
            outputDocument.importPage(inputDocument.getDocumentCatalog().getPages().get(page - 1));
            saveCloseCurrent(number, outputDocument);
            outputDocument = new PDDocument();
        }
        inputDocument.close();
        outputDocument.close();
    }

    private static void saveCloseCurrent(String currentNo, PDDocument outputDocument) {
        if (currentNo != null) {
            File file = new File("C:\\Users\\Vasil\\Desktop\\do\\" + currentNo + ".pdf");
            if (file.exists()) {
                System.err.println("File " + file + " exists?!");
                System.exit(-1);
            }
            try {
                outputDocument.save(file);
                outputDocument.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
